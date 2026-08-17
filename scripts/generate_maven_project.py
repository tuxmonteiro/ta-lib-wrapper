#   Copyright (c) 2025 fibonsai.com
#   All rights reserved.
#
#   This source is subject to the Apache License, Version 2.0.
#   Please see the LICENSE file for more information.
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

import os
import xml.etree.ElementTree as ET

base_package = 'com.tictactec.ta.lib'


def create_maven_project(xml_file):
    """
    Generates a complete Maven project for ta-lib using the Java 22+
    Foreign Function & Memory API (FFM API) for native interoperation.
    """
    base_dir = 'src/main/java'
    test_dir = 'src/test/java'

    # Create directory structure
    main_java_dir = os.path.join(base_dir, "/".join(base_package.split(".")))
    results_java_dir = os.path.join(base_dir, "/".join(base_package.split(".")) + '/results')
    functions_java_dir = os.path.join(base_dir, "/".join(base_package.split(".")) + '/functions')
    test_java_dir = os.path.join(test_dir, "/".join(base_package.split(".")) + '/functions')
    os.makedirs(main_java_dir, exist_ok=True)
    os.makedirs(results_java_dir, exist_ok=True)
    os.makedirs(functions_java_dir, exist_ok=True)
    os.makedirs(test_java_dir, exist_ok=True)

    # Parse the XML file
    tree = ET.parse(xml_file)
    root = tree.getroot()
    functions = list(root.findall('FinancialFunction'))

    # Create TALib.java core FFM class
    create_core_lib_class(main_java_dir, functions)

    # Create a class for each function and its test
    for func in functions:
        create_function_class(functions_java_dir, func)
        create_test_class(test_java_dir, func)

    print(f"Maven project created")


def get_java_type(ta_type):
    """Maps TA-Lib XML types to Java types."""
    if ta_type in ['Integer', 'MA Type']:
        return 'int'
    if ta_type == 'Double':
        return 'double'
    if 'Array' in ta_type:
        if 'Integer' in ta_type:
            return 'int[]'
        return 'double[]'
    return 'double[]'  # Default for High, Low, Open, Close, Volume


def get_ffm_layout(ta_type):
    """Maps TA-Lib XML types to FFM ValueLayout strings for function descriptors."""
    if ta_type in ['Integer', 'MA Type']:
        return 'ValueLayout.JAVA_INT'
    if ta_type == 'Double':
        return 'ValueLayout.JAVA_DOUBLE'
    # All array types and price types (High, Low, Open, Close, Volume) are pointers
    return 'ValueLayout.ADDRESS'


def get_segment_layout(java_type):
    """Returns the ValueLayout constant for a Java array element type."""
    if java_type == 'int[]':
        return 'ValueLayout.JAVA_INT'
    return 'ValueLayout.JAVA_DOUBLE'


def get_result_class(output_params):
    """Determines the result class based on output parameter names."""
    for p in output_params:
        match p['name']:
            case "outReal":
                return 'RealResult'
            case "outInteger":
                return 'IntegerResult'
            case "outAroonDown" | "outAroonUp":
                return 'AroonResult'
            case "outRealUpperBand" | "outRealMiddleBand" | "outRealLowerBand":
                return 'BandsResult'
            case "outInPhase" | "outQuadrature":
                return 'HtPhasorResult'
            case "outSine" | "outLeadSine":
                return 'HtSineResult'
            case "outMACD" | "outMACDSignal" | "outMACDHist":
                return 'MACDResult'
            case "outMAMA" | "outFAMA":
                return 'MamaResult'
            case "outMin" | "outMax":
                return 'MinMaxResult'
            case "outMinIdx" | "outMaxIdx":
                return 'MinMaxIdxResult'
            case "outSlowK" | "outSlowD":
                return 'SlowResult'
            case "outFastK" | "outFastD":
                return 'FastResult'
    return 'RealResult'  # fallback


def create_core_lib_class(main_java_dir, functions):
    """
    Creates the core TALib.java class using the Java 22+
    Foreign Function & Memory API (FFM API).
    """
    lines = [
        f"package {base_package};",
        "",
        "import java.lang.foreign.*;",
        "import java.lang.invoke.MethodHandle;",
        "import java.nio.file.Files;",
        "import java.nio.file.Path;",
        "",
        "/**",
        " * Core TA-Lib native interface using the Java 22+ Foreign Function &amp; Memory API.",
        " * <p>",
        " * This class loads the native TA-Lib shared library via",
        " * {@link System#loadLibrary(String)} and binds each {@code TA_*} C function",
        "     * to a pre-compiled {@link MethodHandle} through the FFM",
        "     * {@link Linker}.  Function handles are invoked via {@link #call}",
        "     * which converts the checked {@code Throwable} from",
        "     * {@link MethodHandle#invokeWithArguments(Object...)} into an",
        "     * unchecked {@link AssertionError}.",
        "     * <p>",
        "     * Callers (the generated wrapper classes in {@code functions.*}) pass",
        "     * Java primitive arrays and scalars directly; this class handles",
        "     * the mapping from Java heap memory to native memory through",
        "     * {@link Arena} allocations in each wrapper.",
        "     * <p>",
        "     * The native library is first loaded via {@link System#loadLibrary(String)}.",
        "     * If that fails, common system library paths are searched as a",
        "     * fallback so callers do not need to set {@code java.library.path}",
        "     * {@code LD_LIBRARY_PATH} or {@code DYLD_LIBRARY_PATH}.",
        "     */",
        "public final class TALib {",
        "",
        "    public static final Linker LINKER = Linker.nativeLinker();",
        "    public static final SymbolLookup LOOKUP;",
        "",
        "    static {",
        "        try {",
        "            System.loadLibrary(\"ta-lib\");",
        "        } catch (UnsatisfiedLinkError e) {",
        "            loadFromCommonPaths(\"ta-lib\");",
        "        }",
        "        LOOKUP = SymbolLookup.loaderLookup();",
        "    }",
        "",
        "    /**",
        "     * Searches common system library directories for the named native library",
        "     * and loads it via {@link System#load(String)}.",
        "     *",
        "     * @param libName the library name without prefix/suffix (e.g. {@code \"ta-lib\"})",
        "     * @throws UnsatisfiedLinkError if the library cannot be found in any",
        "     *         of the well-known locations",
        "     */",
        "    private static void loadFromCommonPaths(String libName) {",
        "        String osName = System.getProperty(\"os.name\").toLowerCase();",
        "        String libFile;",
        "        String[] searchPaths;",
        "        if (osName.contains(\"win\")) {",
        "            libFile = libName + \".dll\";",
        "            searchPaths = new String[0];",
        "        } else if (osName.contains(\"mac\")) {",
        "            libFile = \"lib\" + libName + \".dylib\";",
        "            searchPaths = new String[]{\"/usr/local/lib\", \"/opt/homebrew/lib\", \"/usr/lib\"};",
        "        } else {",
        "            libFile = \"lib\" + libName + \".so\";",
        "            searchPaths = new String[]{\"/usr/lib/x86_64-linux-gnu\", \"/usr/lib\", \"/usr/local/lib\", \"/lib/x86_64-linux-gnu\", \"/lib\"};",
        "        }",
        "",
        "        for (String dir : searchPaths) {",
        "            Path candidate = Path.of(dir, libFile);",
        "            if (Files.isReadable(candidate)) {",
        "                System.load(candidate.toAbsolutePath().toString());",
        "                return;",
        "            }",
        "        }",
        "",
        "        // Also honour LD_LIBRARY_PATH / DYLD_LIBRARY_PATH as a last resort.",
        "        String envPath = osName.contains(\"win\") ? \"\" : System.getenv(\"LD_LIBRARY_PATH\");",
        "        if (osName.contains(\"mac\") && envPath.isEmpty()) {",
        "            envPath = System.getenv(\"DYLD_LIBRARY_PATH\");",
        "        }",
        "        if (!envPath.isEmpty()) {",
        "            String sep = System.getProperty(\"path.separator\");",
        "            for (String dir : envPath.split(sep)) {",
        "                Path candidate = Path.of(dir, libFile);",
        "                if (Files.isReadable(candidate)) {",
        "                    System.load(candidate.toAbsolutePath().toString());",
        "                    return;",
        "                }",
        "            }",
        "        }",
        "",
        "        String searched = String.join(\", \", searchPaths);",
        "        throw new UnsatisfiedLinkError(",
        "            \"Native library '\" + libName + \"' not found in java.library.path, \"",
        "                + \"common system paths [\" + searched + \"], or LD_LIBRARY_PATH.\");",
        "    }",
        "",
        "    private static MethodHandle downcall(String name, FunctionDescriptor fd) {",
        "        return LINKER.downcallHandle(",
        "            LOOKUP.find(name).orElseThrow(() ->",
        "                new UnsatisfiedLinkError(\"Native function \" + name + \" not found in ta-lib\")),",
        "            fd);",
        "    }",
        "",
        "    /**",
        "     * Invokes a native TA-Lib function via the given method handle.",
        "     *",
        "     * @param handle the method handle for the native function",
        "     * @param args   the arguments to pass to the native function",
        "     * @return the integer return code from the native function",
        "     */",
        "    public static int call(MethodHandle handle, Object... args) {",
        "        try {",
        "            return (int) handle.invokeWithArguments(args);",
        "        } catch (RuntimeException | Error e) {",
        "            throw e;",
        "        } catch (Throwable t) {",
        "            throw new AssertionError(t);",
        "        }",
        "    }",
        "",
        "    // --- Core ---",
        "    public static final MethodHandle TA_Initialize = downcall(\"TA_Initialize\",",
        "        FunctionDescriptor.of(ValueLayout.JAVA_INT));",
        "    public static final MethodHandle TA_Shutdown = downcall(\"TA_Shutdown\",",
        "        FunctionDescriptor.of(ValueLayout.JAVA_INT));",
        "",
    ]

    for func in functions:
        abbr = func.find('Abbreviation').text
        short_desc = func.find('ShortDescription').text

        # Build layout list for the function descriptor
        layouts = ["ValueLayout.JAVA_INT"]  # return type: TA_RetCode (int)
        layouts.append("ValueLayout.JAVA_INT")  # startIdx
        layouts.append("ValueLayout.JAVA_INT")  # endIdx

        # Required inputs
        req_inputs = func.find('RequiredInputArguments')
        if req_inputs is not None:
            for arg in req_inputs.findall('RequiredInputArgument'):
                arg_type = arg.find('Type').text
                layouts.append(get_ffm_layout(arg_type))

        # Optional inputs
        opt_inputs = func.find('OptionalInputArguments')
        if opt_inputs is not None:
            for arg in opt_inputs.findall('OptionalInputArgument'):
                arg_type = arg.find('Type').text
                layouts.append(get_ffm_layout(arg_type))

        # outBegIdx (int*)
        layouts.append("ValueLayout.ADDRESS")
        # outNBElement (int*)
        layouts.append("ValueLayout.ADDRESS")

        # Output arrays (double[] or int[])
        outputs = func.find('OutputArguments')
        if outputs is not None:
            for arg in outputs.findall('OutputArgument'):
                arg_type = arg.find('Type').text
                layouts.append(get_ffm_layout(arg_type))

        fd_params = ', '.join(layouts)

        lines.append(f"    // {abbr} - {short_desc}")
        lines.append(f"    public static final MethodHandle TA_{abbr} = downcall(\"TA_{abbr}\",")
        lines.append(f"        FunctionDescriptor.of({fd_params}));")
        lines.append("")

    lines.append("}")

    with open(os.path.join(main_java_dir, 'TALib.java'), 'w') as f:
        f.write('\n'.join(lines))


def create_function_class(functions_java_dir, func):
    """
    Creates a Java class for a single TA-Lib function using the FFM API.
    """
    camel_case_name = func.find('CamelCaseName').text
    abbr = func.find('Abbreviation').text
    short_desc = func.find('ShortDescription').text

    # Build parameter lists
    required_params = []
    optional_params = []
    output_params = []

    req_inputs = func.find('RequiredInputArguments')
    if req_inputs is not None:
        for arg in req_inputs.findall('RequiredInputArgument'):
            p_type = get_java_type(arg.find('Type').text)
            p_name = arg.find('Name').text.lower()
            required_params.append({'type': p_type, 'name': p_name})

    opt_inputs = func.find('OptionalInputArguments')
    if opt_inputs is not None:
        for arg in opt_inputs.findall('OptionalInputArgument'):
            p_type = get_java_type(arg.find('Type').text)
            p_name = "optIn" + arg.find('Name').text.replace(' ', '').replace('-', '')
            default_val = arg.find('DefaultValue').text
            optional_params.append({'type': p_type, 'name': p_name, 'default': default_val})

    outputs = func.find('OutputArguments')
    if outputs is not None:
        for arg in outputs.findall('OutputArgument'):
            p_type = get_java_type(arg.find('Type').text)
            p_name = arg.find('Name').text
            output_params.append({'type': p_type, 'name': p_name})

    result_class = get_result_class(output_params)

    # Start building the class content
    L = []
    L.append(f"package {base_package}.functions;")
    L.append("")
    L.append("import org.slf4j.Logger;")
    L.append("import org.slf4j.LoggerFactory;")
    L.append("")
    L.append(f"import {base_package}.results.*;")
    L.append(f"import {base_package}.TALib;")
    L.append("")
    L.append("import java.lang.foreign.*;")
    L.append("")
    L.append("/**")
    L.append(f" * This class is a wrapper for the TA-Lib function {abbr}: {short_desc}.")
    L.append(" */")
    L.append(f"public class {camel_case_name} {{")
    L.append("")
    L.append(f"    private static final Logger logger = LoggerFactory.getLogger({camel_case_name}.class);")
    L.append("")

    # Method signature
    method_params = ["int startIdx", "int endIdx"]
    method_params.extend([f"{p['type']} {p['name']}" for p in required_params])
    method_params.extend([f"{p['type']} {p['name']}" for p in optional_params])

    L.append(f"    public static Result execute({', '.join(method_params)}) throws ArithmeticException, IndexOutOfBoundsException {{")

    # Input validation
    L.append("        // Input validation")
    L.append("        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {")
    L.append("            throw new IndexOutOfBoundsException(\"Invalid startIdx or endIdx. startIdx=\" + startIdx + \", endIdx=\" + endIdx);")
    L.append("        }")
    for p in required_params:
        if '[]' in p['type']:
            L.append(f"        if ({p['name']} == null || {p['name']}.length <= endIdx) {{")
            L.append(f"            throw new IndexOutOfBoundsException(\"Input array '{p['name']}' is null or too small for endIdx=\" + endIdx);")
            L.append("        }")

    # Allocation size
    if required_params:
        input_len_provider = required_params[0]['name']
        L.append(f"        int allocationSize = {input_len_provider}.length;")
    else:
        L.append("        int allocationSize = endIdx - startIdx + 1;")

    # Try-with-resources for arena
    L.append("")
    L.append("        try (var arena = Arena.ofConfined()) {")

    # Allocate input segments (copy Java arrays to native memory)
    for p in required_params:
        if '[]' in p['type']:
            seg_layout = get_segment_layout(p['type'])
            L.append(f"            var {p['name']}Seg = arena.allocateFrom({seg_layout}, {p['name']});")

    # Allocate outBegIdx and outNBElement (int*)
    L.append("            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);")
    L.append("            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);")

    # Allocate output segments
    for p in output_params:
        if '[]' in p['type']:
            seg_layout = get_segment_layout(p['type'])
            prim_type = p['type'].replace('[]', '')
            L.append(f"            var {p['name']}Seg = arena.allocateFrom({seg_layout}, new {prim_type}[allocationSize]);")

    # Build call parameters for TALib.call()
    call_params = ["TALib.TA_" + abbr]
    call_params.append("startIdx")
    call_params.append("endIdx")

    # Required input params: segments for arrays, direct for primitives
    for p in required_params:
        if '[]' in p['type']:
            call_params.append(f"{p['name']}Seg")
        else:
            call_params.append(p['name'])

    # Optional params: always passed directly (primitives)
    for p in optional_params:
        call_params.append(p['name'])

    # outBegIdx, outNBElement
    call_params.append("outBegIdx")
    call_params.append("outNBElement")

    # Output params: segments for arrays, direct for primitives
    for p in output_params:
        if '[]' in p['type']:
            call_params.append(f"{p['name']}Seg")
        else:
            call_params.append(p['name'])

    L.append("")
    L.append(f"            int retCode = TALib.call({', '.join(call_params)});")

    # Error handling
    L.append("            if (retCode != 0) {")
    L.append(f"                logger.error(\"TA-Lib function {abbr} returned error code: {{}}\", retCode);")
    L.append(f"                throw new ArithmeticException(\"TA-Lib function {abbr} returned error code: \" + retCode);")
    L.append("            }")

    # Copy output arrays back to Java heap
    L.append("")
    for p in output_params:
        if '[]' in p['type']:
            seg_layout = get_segment_layout(p['type'])
            prim_type = p['type'].replace('[]', '')
            L.append(f"            {p['type']} {p['name']} = new {prim_type}[allocationSize];")
            L.append(f"            MemorySegment.copy({p['name']}Seg, {seg_layout}, 0, {p['name']}, 0, allocationSize);")

    # Build and return result
    L.append("")
    L.append(f"            return {result_class}.builder()")
    for p in output_params:
        L.append(f"                .{p['name']}({p['name']})")
    L.append("                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))")
    L.append("                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))")
    L.append("                .build();")
    L.append("        }")
    L.append("    }")
    L.append("}")

    with open(os.path.join(functions_java_dir, f"{camel_case_name}.java"), 'w') as f:
        f.write('\n'.join(L))


def create_test_class(test_java_dir, func):
    """
    Creates a JUnit test class for a single TA-Lib function.
    """
    camel_case_name = func.find('CamelCaseName').text

    required_params = []
    req_inputs = func.find('RequiredInputArguments')
    if req_inputs is not None:
        for arg in req_inputs.findall('RequiredInputArgument'):
            p_type = get_java_type(arg.find('Type').text)
            p_name = arg.find('Name').text.lower()
            required_params.append({'type': p_type, 'name': p_name})

    optional_params = []
    opt_inputs = func.find('OptionalInputArguments')
    if opt_inputs is not None:
        for arg in opt_inputs.findall('OptionalInputArgument'):
            p_type = get_java_type(arg.find('Type').text)
            p_name = "optIn" + arg.find('Name').text.replace(' ', '').replace('-', '')
            default_val = arg.find('DefaultValue').text
            optional_params.append({'type': p_type, 'name': p_name, 'default': default_val})

    content = [
        f"package {base_package}.functions;",
        "",
        "import org.junit.jupiter.api.Test;",
        "import static org.junit.jupiter.api.Assertions.*;",
        f"import {base_package}.functions.*;",
        f"import {base_package}.results.*;",
        "",
        f"public class {camel_case_name}Test {{",
        "",
        "    @Test",
        "    public void testExecute() {",
        "        // This is a simple smoke test to ensure the function can be called without crashing.",
        "        int size = 100;",
        "        int startIdx = 0;",
        "        int endIdx = size - 1;"
    ]

    # Create dummy input data
    for p in required_params:
        content.append(f"        {p['type']} {p['name']} = new {p['type'].replace('[]', '')}[size];")
        content.append(f"        for(int i=0; i<size; i++) {{ {p['name']}[i] = i; }} // Dummy data")

    call_params = ["startIdx", "endIdx"]
    call_params.extend([p['name'] for p in required_params])

    # Use default values for optional params
    for p in optional_params:
        content.append(f"        // TODO: optIn{p['name']} default: {p['default']}")
        call_params.append(f"({p['type']}){p['default']}")

    content.append(f"        Result result = {camel_case_name}.execute({', '.join(call_params)});")
    content.append("        assertNotNull(result);")
    content.append("        // Further assertions can be added here if expected values are known.")
    content.append("    }")
    content.append("}")

    with open(os.path.join(test_java_dir, f"{camel_case_name}Test.java"), 'w') as f:
        f.write('\n'.join(content))


if __name__ == "__main__":
    xml_file = './ta_func_api.xml'
    if not os.path.exists(xml_file):
        print(f"Error: '{xml_file}' not found. Make sure you are in the right directory.")
    else:
        create_maven_project(xml_file)

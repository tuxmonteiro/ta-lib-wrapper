# TA-Lib FFM Wrapper

A Java 22+ **Foreign Function & Memory API (FFM API)** wrapper for the
[TA-Lib](https://ta-lib.org/) (Technical Analysis Library) native C library.
This project provides type-safe, high-performance access to every TA-Lib
function — from simple moving averages to Bollinger Bands, MACD, RSI, and
candlestick pattern recognition — without any third-party JNI bridge such as
JNA or JNIgen.

The entire public API is generated from the official TA-Lib function
definitions (`ta_func_api.xml`) via a Python code generator
(`scripts/generate_maven_project.py`).

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Building and Installing Native TA-Lib](#building-and-installing-native-ta-lib)
- [Building the Java Project](#building-the-java-project)
- [Running the Tests](#running-the-tests)
- [Project Structure](#project-structure)
- [Usage Examples](#usage-examples)
- [API Quick Reference](#api-quick-reference)
- [Regenerating Sources](#regenerating-sources)
- [License](#license)

---

## Prerequisites

| Tool     | Minimum Version |
|----------|----------------|
| JDK      | 22             |
| Maven    | 3.8+           |
| TA-Lib C library | 0.4.x or 0.8.x |

### Why Java 22+?

This wrapper uses the **Foreign Function & Memory API (FFM API)**, which was
finalized as a standard feature in Java 22 (JEP 443 / JSR 442).  Earlier
incubations were available in Java 19–21, but the stable, supported API
requires Java 22 or later.

The native TA-Lib shared library (`libta-lib.so` on Linux,
`libta-lib.dylib` on macOS, `ta-lib.dll` on Windows) must be installed and
reachable on the JVM's library path.

---

## Building and Installing Native TA-Lib

### From source (CMake)

```bash
git clone https://github.com/TA-Lib/ta-lib.git
cd ta-lib
mkdir build && cd build
cmake ..
make -j$(nproc)
sudo make install            # installs to /usr/local/lib
sudo ldconfig                # refresh the dynamic linker cache
```

If you prefer not to install system-wide, skip `sudo make install` and set
`LD_LIBRARY_PATH` (see [Running the Tests](#running-the-tests)).

### Using a package manager

```bash
# Ubuntu / Debian
sudo apt-get install libta-lib-dev

# macOS (Homebrew)
brew install ta-lib
```

---

## Building the Java Project

```bash
mvn clean install
```

This compiles the source, runs the tests, and packages the project into a JAR.

---

## Running the Tests

The wrapper uses `System.loadLibrary("ta-lib")` to load the native library.
At class-initialisation time `TALib` first tries the standard
`System.loadLibrary` mechanism (i.e. `java.library.path` and, on Linux,
`LD_LIBRARY_PATH`).  If that fails it automatically searches common system
library directories — for example `/usr/lib/x86_64-linux-gnu`,
`/usr/lib`, `/usr/local/lib`, `/lib` on Linux, or `/usr/local/lib`,
`/opt/homebrew/lib`, `/usr/lib` on macOS — so in most cases **no extra
configuration is required**.  Simply run:

```bash
mvn test
```

If your library lives somewhere outside the searched paths, make it
discoverable to the JVM.  Two approaches work:

**Option A — `LD_LIBRARY_PATH` (Linux / macOS):**

```bash
export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
mvn test
```

**Option B — `-Djava.library.path` (cross-platform):**

```bash
mvn test -DargLine="-Djava.library.path=/usr/local/lib"
```

> **Note:** On Java 22+, `System.loadLibrary` is a *restricted method*.
> The surefire configuration in `pom.xml` already includes
> `--enable-native-access=ALL-UNNAMED` via the `@{argLine}` placeholder,
> so it is automatically merged with any `-DargLine` you pass on the
> command line.  No manual `--enable-native-access` flag is needed.

---

## Project Structure

The project is a standard Maven project.  Sources are **auto-generated** from
`ta_func_api.xml` by the Python script in `scripts/`.

```
ta-lib-jna/
├── pom.xml                          Maven configuration (Java 22+, no JNA)
├── ta_func_api.xml                  TA-Lib function definitions (input by lib/)
├── scripts/
│   ├── generate_maven_project.py    Python code generator (FFM API)
│   └── exports.sh                   Convenience: export LD_LIBRARY_PATH
└── src/
    ├── main/java/com/tictactec/ta/lib/
    │   ├── TALib.java               FFM core: Linker, SymbolLookup, MethodHandles
    │   ├── TALibBuilder.java        Fluent builder API for function calls
    │   ├── TaLibFunction.java       Enum of all functions (reflection dispatch)
    │   ├── MAType.java              Moving-average type enum
    │   ├── functions/               One wrapper class per TA-Lib function
    │   └── results/                 Result classes (RealResult, BandsResult, ...)
    └── test/java/com/tictactec/ta/lib/
        ├── TaLibBuilderTest.java    Builder-level integration test
        └── functions/               One smoke test per function
```

### Core architecture

```
┌──────────────────────────────────────────────┐
│  Function wrapper class                      │  ← public API: .execute(...)
│  (e.g. Sma, Bbands, Macd, …)                 │
│                                              │
│  • Validates inputs                          │
│  • Allocates native memory via Arena         │
│  • Copies Java arrays → MemorySegment        │
│  • Calls TALib.call(handle, …)               │
│  • Copies results back & builds Result       │
└─────────────────────────┬────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────┐
│  TALib (FFM core)                            │
│                                              │
│  • Linker.nativeLinker()                     │
│  • SymbolLookup.loaderLookup()               │
│  • MethodHandle per TA_* function            │
│  • call(handle, args…) → invokeWithArguments │
└──────────────────────────────────────────────┘
```

---

## Usage Examples

### Simple Moving Average (SMA)

```java
import com.tictactec.ta.lib.functions.Sma;
import com.tictactec.ta.lib.results.RealResult;
import com.tictactec.ta.lib.results.Result;

public class Example {
    public static void main(String[] args) {
        // Sample input data
        double[] inReal = new double[100];
        for (int i = 0; i < 100; i++) {
            inReal[i] = i * 1.0;
        }

        int startIdx = 0;
        int endIdx = inReal.length - 1;
        int optInTimePeriod = 10; // 10-period SMA

        try {
            Result result = Sma.execute(startIdx, endIdx, inReal, optInTimePeriod);

            System.out.println("SMA Calculation Result:");
            System.out.println("Output begins at index: " + result.outBegIdx());
            System.out.println("Number of elements in output: " + result.outNBElement());

            RealResult sma = (RealResult) result;
            for (int i = 0; i < result.outNBElement(); i++) {
                System.out.println("SMA[" + (result.outBegIdx() + i) + "] = " + sma.outReal()[i]);
            }
        } catch (ArithmeticException e) {
            System.err.println("TA-Lib error: " + e.getMessage());
        }
    }
}
```

### Bollinger Bands (multiple outputs)

```java
import com.tictactec.ta.lib.functions.Bbands;
import com.tictactec.ta.lib.results.BandsResult;

double[] inReal = { /* your price data */ };
BandsResult result = (BandsResult) Bbands.execute(
    0, inReal.length - 1,   // startIdx, endIdx
    inReal,                 // input data
    20,                     // optInTimePeriod
    2.0,                    // optInDeviationsup
    2.0,                    // optInDeviationsdown
    0                       // optInMAType (SMA)
);

double[] upper = result.outRealUpperBand();
double[] middle = result.outRealMiddleBand();
double[] lower = result.outRealLowerBand();
```

### Using the Fluent Builder API

```java
import com.tictactec.ta.lib.TALibBuilder;
import com.tictactec.ta.lib.TaLibFunction;
import com.tictactec.ta.lib.results.RealResult;

double[] prices = { /* your price data */ };

Result result = TaLibFunction.SMA.builder()
    .startIdx(0)
    .endIdx(prices.length - 1)
    .optInTimePeriod(20)
    .inReal(prices)
    .execute();

RealResult sma = (RealResult) result;
```

### Candlestick Pattern Recognition

```java
import com.tictactec.ta.lib.functions.CdlEngulfing;
import com.tictactec.ta.lib.results.IntegerResult;

double[] open = { /* open prices */ };
double[] high  = { /* high prices */ };
double[] low   = { /* low prices */ };
double[] close = { /* close prices */ };

IntegerResult result = (IntegerResult) CdlEngulfing.execute(
    0, close.length - 1, open, high, low, close
);

// result.outInteger() contains pattern signals (positive = bullish,
// negative = bearish, 0 = no pattern)
```

---

## API Quick Reference

| Category | Functions | Result Type | Output Type |
|----------|-----------|-------------|-------------|
| **Math Operators** | Add, Sub, Mult, Div | `RealResult` | `double[]` |
| **Math Transform** | Sin, Cos, Tan, Exp, Ln, Sqrt, ... | `RealResult` | `double[]` |
| **Overlap Studies** | Sma, Ema, Bbands, Trima, ... | `RealResult` / `BandsResult` | `double[]` |
| **Momentum** | Rsi, Macd, Cci, Roc, ... | `RealResult` / `MACDResult` | `double[]` |
| **Volume** | Obv, Ad, AdOsc, Mfi | `RealResult` | `double[]` |
| **Pattern Recognition** | CdlEngulfing, CdlHarami, ... | `IntegerResult` | `int[]` |
| **Price Transform** | TypPrice, MedPrice, AvgPrice, ... | `RealResult` | `double[]` |
| **Volatility** | Atr, Natr, Trange | `RealResult` | `double[]` |
| **Cycle / Trend** | Aroon, Adx, Dx, ... | Various | `double[]` / `int[]` |
| **HT (Hilbert)** | HtTrendline, HtSine, HtPhasor, ... | Various | `double[]` / `int[]` |

### Available result types

| Result class | Fields |
|---|---|
| `RealResult` | `outReal()` → `double[]` |
| `IntegerResult` | `outInteger()` → `int[]` |
| `BandsResult` | `outRealUpperBand()`, `outRealMiddleBand()`, `outRealLowerBand()` |
| `MACDResult` | `outMACD()`, `outMACDSignal()`, `outMACDHist()` |
| `AroonResult` | `outAroonDown()`, `outAroonUp()` |
| `MamaResult` | `outMAMA()`, `outFAMA()` |
| `HtPhasorResult` | `outInPhase()`, `outQuadrature()` |
| `HtSineResult` | `outSine()`, `outLeadSine()` |
| `MinMaxResult` | `outMin()`, `outMax()` |
| `MinMaxIdxResult` | `outMinIdx()`, `outMaxIdx()` |
| `FastResult` | `outFastK()`, `outFastD()` |
| `SlowResult` | `outSlowK()`, `outSlowD()` |

Every `Result` subclass also provides `outBegIdx()` (the index where output
starts) and `outNBElement()` (the number of valid elements) via the abstract
`Result` base class.

---

## Regenerating Sources

The Java sources (function wrappers, TALib core, and tests) are generated from
`ta_func_api.xml`:

```bash
cd ta-lib-jna
python3 scripts/generate_maven_project.py
```

To regenerate the `TaLibFunction` enum after adding or modifying function
classes:

```bash
bash scripts/generate_TaLibFunction.sh
```

---

## License

This project is licensed under the Apache License, Version 2.0. See the
`LICENSE` file for details.

TA-Lib itself is licensed under the TA-Lib license; see
<https://ta-lib.org/license/> for details.

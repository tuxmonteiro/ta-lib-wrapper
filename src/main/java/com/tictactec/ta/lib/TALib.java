package com.tictactec.ta.lib;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Core TA-Lib native interface using the Java 22+ Foreign Function &amp; Memory API.
 * <p>
 * This class loads the native TA-Lib shared library via
 * {@link System#loadLibrary(String)} and binds each {@code TA_*} C function
 * to a pre-compiled {@link MethodHandle} through the FFM
 * {@link Linker}.  Function handles are invoked via {@link #call}
 * which converts the checked {@code Throwable} from
 * {@link MethodHandle#invokeWithArguments(Object...)} into an
 * unchecked {@link AssertionError}.
 * <p>
 * Callers (the generated wrapper classes in {@code functions.*}) pass
 * Java primitive arrays and scalars directly; this class handles
 * the mapping from Java heap memory to native memory through
 * {@link Arena} allocations in each wrapper.
 */
public final class TALib {

    public static final Linker LINKER = Linker.nativeLinker();
    public static final SymbolLookup LOOKUP;

    static {
        System.loadLibrary("ta-lib");
        LOOKUP = SymbolLookup.loaderLookup();
    }

    private static MethodHandle downcall(String name, FunctionDescriptor fd) {
        return LINKER.downcallHandle(
            LOOKUP.find(name).orElseThrow(() ->
                new UnsatisfiedLinkError("Native function " + name + " not found in ta-lib")),
            fd);
    }

    /**
     * Invokes a native TA-Lib function via the given method handle.
     *
     * @param handle the method handle for the native function
     * @param args   the arguments to pass to the native function
     * @return the integer return code from the native function
     */
    public static int call(MethodHandle handle, Object... args) {
        try {
            return (int) handle.invokeWithArguments(args);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    // --- Core ---
    public static final MethodHandle TA_Initialize = downcall("TA_Initialize",
        FunctionDescriptor.of(ValueLayout.JAVA_INT));
    public static final MethodHandle TA_Shutdown = downcall("TA_Shutdown",
        FunctionDescriptor.of(ValueLayout.JAVA_INT));

    // ACCBANDS - Acceleration Bands
    public static final MethodHandle TA_ACCBANDS = downcall("TA_ACCBANDS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ACOS - Vector Trigonometric ACos
    public static final MethodHandle TA_ACOS = downcall("TA_ACOS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // AD - Chaikin A/D Line
    public static final MethodHandle TA_AD = downcall("TA_AD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ADD - Vector Arithmetic Add
    public static final MethodHandle TA_ADD = downcall("TA_ADD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ADOSC - Chaikin A/D Oscillator
    public static final MethodHandle TA_ADOSC = downcall("TA_ADOSC",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ADX - Average Directional Movement Index
    public static final MethodHandle TA_ADX = downcall("TA_ADX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ADXR - Average Directional Movement Index Rating
    public static final MethodHandle TA_ADXR = downcall("TA_ADXR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // APO - Absolute Price Oscillator
    public static final MethodHandle TA_APO = downcall("TA_APO",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // AROON - Aroon
    public static final MethodHandle TA_AROON = downcall("TA_AROON",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // AROONOSC - Aroon Oscillator
    public static final MethodHandle TA_AROONOSC = downcall("TA_AROONOSC",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ASIN - Vector Trigonometric ASin
    public static final MethodHandle TA_ASIN = downcall("TA_ASIN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ATAN - Vector Trigonometric ATan
    public static final MethodHandle TA_ATAN = downcall("TA_ATAN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ATR - Average True Range
    public static final MethodHandle TA_ATR = downcall("TA_ATR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // AVGPRICE - Average Price
    public static final MethodHandle TA_AVGPRICE = downcall("TA_AVGPRICE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // AVGDEV - Average Deviation
    public static final MethodHandle TA_AVGDEV = downcall("TA_AVGDEV",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // BBANDS - Bollinger Bands
    public static final MethodHandle TA_BBANDS = downcall("TA_BBANDS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // BETA - Beta
    public static final MethodHandle TA_BETA = downcall("TA_BETA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // BOP - Balance Of Power
    public static final MethodHandle TA_BOP = downcall("TA_BOP",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CCI - Commodity Channel Index
    public static final MethodHandle TA_CCI = downcall("TA_CCI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL2CROWS - Two Crows
    public static final MethodHandle TA_CDL2CROWS = downcall("TA_CDL2CROWS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL3BLACKCROWS - Three Black Crows
    public static final MethodHandle TA_CDL3BLACKCROWS = downcall("TA_CDL3BLACKCROWS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL3INSIDE - Three Inside Up/Down
    public static final MethodHandle TA_CDL3INSIDE = downcall("TA_CDL3INSIDE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL3LINESTRIKE - Three-Line Strike 
    public static final MethodHandle TA_CDL3LINESTRIKE = downcall("TA_CDL3LINESTRIKE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL3OUTSIDE - Three Outside Up/Down
    public static final MethodHandle TA_CDL3OUTSIDE = downcall("TA_CDL3OUTSIDE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL3STARSINSOUTH - Three Stars In The South
    public static final MethodHandle TA_CDL3STARSINSOUTH = downcall("TA_CDL3STARSINSOUTH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDL3WHITESOLDIERS - Three Advancing White Soldiers
    public static final MethodHandle TA_CDL3WHITESOLDIERS = downcall("TA_CDL3WHITESOLDIERS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLABANDONEDBABY - Abandoned Baby
    public static final MethodHandle TA_CDLABANDONEDBABY = downcall("TA_CDLABANDONEDBABY",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLADVANCEBLOCK - Advance Block
    public static final MethodHandle TA_CDLADVANCEBLOCK = downcall("TA_CDLADVANCEBLOCK",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLBELTHOLD - Belt-hold
    public static final MethodHandle TA_CDLBELTHOLD = downcall("TA_CDLBELTHOLD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLBREAKAWAY - Breakaway
    public static final MethodHandle TA_CDLBREAKAWAY = downcall("TA_CDLBREAKAWAY",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLCLOSINGMARUBOZU - Closing Marubozu
    public static final MethodHandle TA_CDLCLOSINGMARUBOZU = downcall("TA_CDLCLOSINGMARUBOZU",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLCONCEALBABYSWALL - Concealing Baby Swallow
    public static final MethodHandle TA_CDLCONCEALBABYSWALL = downcall("TA_CDLCONCEALBABYSWALL",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLCOUNTERATTACK - Counterattack
    public static final MethodHandle TA_CDLCOUNTERATTACK = downcall("TA_CDLCOUNTERATTACK",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLDARKCLOUDCOVER - Dark Cloud Cover
    public static final MethodHandle TA_CDLDARKCLOUDCOVER = downcall("TA_CDLDARKCLOUDCOVER",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLDOJI - Doji
    public static final MethodHandle TA_CDLDOJI = downcall("TA_CDLDOJI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLDOJISTAR - Doji Star
    public static final MethodHandle TA_CDLDOJISTAR = downcall("TA_CDLDOJISTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLDRAGONFLYDOJI - Dragonfly Doji
    public static final MethodHandle TA_CDLDRAGONFLYDOJI = downcall("TA_CDLDRAGONFLYDOJI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLENGULFING - Engulfing Pattern
    public static final MethodHandle TA_CDLENGULFING = downcall("TA_CDLENGULFING",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLEVENINGDOJISTAR - Evening Doji Star
    public static final MethodHandle TA_CDLEVENINGDOJISTAR = downcall("TA_CDLEVENINGDOJISTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLEVENINGSTAR - Evening Star
    public static final MethodHandle TA_CDLEVENINGSTAR = downcall("TA_CDLEVENINGSTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLGAPSIDESIDEWHITE - Up/Down-gap side-by-side white lines
    public static final MethodHandle TA_CDLGAPSIDESIDEWHITE = downcall("TA_CDLGAPSIDESIDEWHITE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLGRAVESTONEDOJI - Gravestone Doji
    public static final MethodHandle TA_CDLGRAVESTONEDOJI = downcall("TA_CDLGRAVESTONEDOJI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHAMMER - Hammer
    public static final MethodHandle TA_CDLHAMMER = downcall("TA_CDLHAMMER",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHANGINGMAN - Hanging Man
    public static final MethodHandle TA_CDLHANGINGMAN = downcall("TA_CDLHANGINGMAN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHARAMI - Harami Pattern
    public static final MethodHandle TA_CDLHARAMI = downcall("TA_CDLHARAMI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHARAMICROSS - Harami Cross Pattern
    public static final MethodHandle TA_CDLHARAMICROSS = downcall("TA_CDLHARAMICROSS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHIGHWAVE - High-Wave Candle
    public static final MethodHandle TA_CDLHIGHWAVE = downcall("TA_CDLHIGHWAVE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHIKKAKE - Hikkake Pattern
    public static final MethodHandle TA_CDLHIKKAKE = downcall("TA_CDLHIKKAKE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHIKKAKEMOD - Modified Hikkake Pattern
    public static final MethodHandle TA_CDLHIKKAKEMOD = downcall("TA_CDLHIKKAKEMOD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLHOMINGPIGEON - Homing Pigeon
    public static final MethodHandle TA_CDLHOMINGPIGEON = downcall("TA_CDLHOMINGPIGEON",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLIDENTICAL3CROWS - Identical Three Crows
    public static final MethodHandle TA_CDLIDENTICAL3CROWS = downcall("TA_CDLIDENTICAL3CROWS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLINNECK - In-Neck Pattern
    public static final MethodHandle TA_CDLINNECK = downcall("TA_CDLINNECK",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLINVERTEDHAMMER - Inverted Hammer
    public static final MethodHandle TA_CDLINVERTEDHAMMER = downcall("TA_CDLINVERTEDHAMMER",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLKICKING - Kicking
    public static final MethodHandle TA_CDLKICKING = downcall("TA_CDLKICKING",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLKICKINGBYLENGTH - Kicking - bull/bear determined by the longer marubozu
    public static final MethodHandle TA_CDLKICKINGBYLENGTH = downcall("TA_CDLKICKINGBYLENGTH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLLADDERBOTTOM - Ladder Bottom
    public static final MethodHandle TA_CDLLADDERBOTTOM = downcall("TA_CDLLADDERBOTTOM",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLLONGLEGGEDDOJI - Long Legged Doji
    public static final MethodHandle TA_CDLLONGLEGGEDDOJI = downcall("TA_CDLLONGLEGGEDDOJI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLLONGLINE - Long Line Candle
    public static final MethodHandle TA_CDLLONGLINE = downcall("TA_CDLLONGLINE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLMARUBOZU - Marubozu
    public static final MethodHandle TA_CDLMARUBOZU = downcall("TA_CDLMARUBOZU",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLMATCHINGLOW - Matching Low
    public static final MethodHandle TA_CDLMATCHINGLOW = downcall("TA_CDLMATCHINGLOW",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLMATHOLD - Mat Hold
    public static final MethodHandle TA_CDLMATHOLD = downcall("TA_CDLMATHOLD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLMORNINGDOJISTAR - Morning Doji Star
    public static final MethodHandle TA_CDLMORNINGDOJISTAR = downcall("TA_CDLMORNINGDOJISTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLMORNINGSTAR - Morning Star
    public static final MethodHandle TA_CDLMORNINGSTAR = downcall("TA_CDLMORNINGSTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLONNECK - On-Neck Pattern
    public static final MethodHandle TA_CDLONNECK = downcall("TA_CDLONNECK",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLPIERCING - Piercing Pattern
    public static final MethodHandle TA_CDLPIERCING = downcall("TA_CDLPIERCING",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLRICKSHAWMAN - Rickshaw Man
    public static final MethodHandle TA_CDLRICKSHAWMAN = downcall("TA_CDLRICKSHAWMAN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLRISEFALL3METHODS - Rising/Falling Three Methods
    public static final MethodHandle TA_CDLRISEFALL3METHODS = downcall("TA_CDLRISEFALL3METHODS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLSEPARATINGLINES - Separating Lines
    public static final MethodHandle TA_CDLSEPARATINGLINES = downcall("TA_CDLSEPARATINGLINES",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLSHOOTINGSTAR - Shooting Star
    public static final MethodHandle TA_CDLSHOOTINGSTAR = downcall("TA_CDLSHOOTINGSTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLSHORTLINE - Short Line Candle
    public static final MethodHandle TA_CDLSHORTLINE = downcall("TA_CDLSHORTLINE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLSPINNINGTOP - Spinning Top
    public static final MethodHandle TA_CDLSPINNINGTOP = downcall("TA_CDLSPINNINGTOP",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLSTALLEDPATTERN - Stalled Pattern
    public static final MethodHandle TA_CDLSTALLEDPATTERN = downcall("TA_CDLSTALLEDPATTERN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLSTICKSANDWICH - Stick Sandwich
    public static final MethodHandle TA_CDLSTICKSANDWICH = downcall("TA_CDLSTICKSANDWICH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLTAKURI - Takuri (Dragonfly Doji with very long lower shadow)
    public static final MethodHandle TA_CDLTAKURI = downcall("TA_CDLTAKURI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLTASUKIGAP - Tasuki Gap
    public static final MethodHandle TA_CDLTASUKIGAP = downcall("TA_CDLTASUKIGAP",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLTHRUSTING - Thrusting Pattern
    public static final MethodHandle TA_CDLTHRUSTING = downcall("TA_CDLTHRUSTING",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLTRISTAR - Tristar Pattern
    public static final MethodHandle TA_CDLTRISTAR = downcall("TA_CDLTRISTAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLUNIQUE3RIVER - Unique 3 River
    public static final MethodHandle TA_CDLUNIQUE3RIVER = downcall("TA_CDLUNIQUE3RIVER",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLUPSIDEGAP2CROWS - Upside Gap Two Crows
    public static final MethodHandle TA_CDLUPSIDEGAP2CROWS = downcall("TA_CDLUPSIDEGAP2CROWS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CDLXSIDEGAP3METHODS - Upside/Downside Gap Three Methods
    public static final MethodHandle TA_CDLXSIDEGAP3METHODS = downcall("TA_CDLXSIDEGAP3METHODS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CEIL - Vector Ceil
    public static final MethodHandle TA_CEIL = downcall("TA_CEIL",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CMO - Chande Momentum Oscillator
    public static final MethodHandle TA_CMO = downcall("TA_CMO",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // CORREL - Pearson's Correlation Coefficient (r)
    public static final MethodHandle TA_CORREL = downcall("TA_CORREL",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // COS - Vector Trigonometric Cos
    public static final MethodHandle TA_COS = downcall("TA_COS",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // COSH - Vector Trigonometric Cosh
    public static final MethodHandle TA_COSH = downcall("TA_COSH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // DEMA - Double Exponential Moving Average
    public static final MethodHandle TA_DEMA = downcall("TA_DEMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // DIV - Vector Arithmetic Div
    public static final MethodHandle TA_DIV = downcall("TA_DIV",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // DX - Directional Movement Index
    public static final MethodHandle TA_DX = downcall("TA_DX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // EMA - Exponential Moving Average
    public static final MethodHandle TA_EMA = downcall("TA_EMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // EXP - Vector Arithmetic Exp
    public static final MethodHandle TA_EXP = downcall("TA_EXP",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // FLOOR - Vector Floor
    public static final MethodHandle TA_FLOOR = downcall("TA_FLOOR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // HT_DCPERIOD - Hilbert Transform - Dominant Cycle Period
    public static final MethodHandle TA_HT_DCPERIOD = downcall("TA_HT_DCPERIOD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // HT_DCPHASE - Hilbert Transform - Dominant Cycle Phase
    public static final MethodHandle TA_HT_DCPHASE = downcall("TA_HT_DCPHASE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // HT_PHASOR - Hilbert Transform - Phasor Components
    public static final MethodHandle TA_HT_PHASOR = downcall("TA_HT_PHASOR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // HT_SINE - Hilbert Transform - SineWave
    public static final MethodHandle TA_HT_SINE = downcall("TA_HT_SINE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // HT_TRENDLINE - Hilbert Transform - Instantaneous Trendline
    public static final MethodHandle TA_HT_TRENDLINE = downcall("TA_HT_TRENDLINE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // HT_TRENDMODE - Hilbert Transform - Trend vs Cycle Mode
    public static final MethodHandle TA_HT_TRENDMODE = downcall("TA_HT_TRENDMODE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // IMI - Intraday Momentum Index
    public static final MethodHandle TA_IMI = downcall("TA_IMI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // KAMA - Kaufman Adaptive Moving Average
    public static final MethodHandle TA_KAMA = downcall("TA_KAMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // LINEARREG - Linear Regression
    public static final MethodHandle TA_LINEARREG = downcall("TA_LINEARREG",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // LINEARREG_ANGLE - Linear Regression Angle
    public static final MethodHandle TA_LINEARREG_ANGLE = downcall("TA_LINEARREG_ANGLE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // LINEARREG_INTERCEPT - Linear Regression Intercept
    public static final MethodHandle TA_LINEARREG_INTERCEPT = downcall("TA_LINEARREG_INTERCEPT",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // LINEARREG_SLOPE - Linear Regression Slope
    public static final MethodHandle TA_LINEARREG_SLOPE = downcall("TA_LINEARREG_SLOPE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // LN - Vector Log Natural
    public static final MethodHandle TA_LN = downcall("TA_LN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // LOG10 - Vector Log10
    public static final MethodHandle TA_LOG10 = downcall("TA_LOG10",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MA - Moving average
    public static final MethodHandle TA_MA = downcall("TA_MA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MACD - Moving Average Convergence/Divergence
    public static final MethodHandle TA_MACD = downcall("TA_MACD",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MACDEXT - MACD with controllable MA type
    public static final MethodHandle TA_MACDEXT = downcall("TA_MACDEXT",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MACDFIX - Moving Average Convergence/Divergence Fix 12/26
    public static final MethodHandle TA_MACDFIX = downcall("TA_MACDFIX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MAMA - MESA Adaptive Moving Average
    public static final MethodHandle TA_MAMA = downcall("TA_MAMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MAVP - Moving average with variable period
    public static final MethodHandle TA_MAVP = downcall("TA_MAVP",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MAX - Highest value over a specified period
    public static final MethodHandle TA_MAX = downcall("TA_MAX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MAXINDEX - Index of highest value over a specified period
    public static final MethodHandle TA_MAXINDEX = downcall("TA_MAXINDEX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MEDPRICE - Median Price
    public static final MethodHandle TA_MEDPRICE = downcall("TA_MEDPRICE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MFI - Money Flow Index
    public static final MethodHandle TA_MFI = downcall("TA_MFI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MIDPOINT - MidPoint over period
    public static final MethodHandle TA_MIDPOINT = downcall("TA_MIDPOINT",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MIDPRICE - Midpoint Price over period
    public static final MethodHandle TA_MIDPRICE = downcall("TA_MIDPRICE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MIN - Lowest value over a specified period
    public static final MethodHandle TA_MIN = downcall("TA_MIN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MININDEX - Index of lowest value over a specified period
    public static final MethodHandle TA_MININDEX = downcall("TA_MININDEX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MINMAX - Lowest and highest values over a specified period
    public static final MethodHandle TA_MINMAX = downcall("TA_MINMAX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MINMAXINDEX - Indexes of lowest and highest values over a specified period
    public static final MethodHandle TA_MINMAXINDEX = downcall("TA_MINMAXINDEX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MINUS_DI - Minus Directional Indicator
    public static final MethodHandle TA_MINUS_DI = downcall("TA_MINUS_DI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MINUS_DM - Minus Directional Movement
    public static final MethodHandle TA_MINUS_DM = downcall("TA_MINUS_DM",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MOM - Momentum
    public static final MethodHandle TA_MOM = downcall("TA_MOM",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // MULT - Vector Arithmetic Mult
    public static final MethodHandle TA_MULT = downcall("TA_MULT",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // NATR - Normalized Average True Range
    public static final MethodHandle TA_NATR = downcall("TA_NATR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // OBV - On Balance Volume
    public static final MethodHandle TA_OBV = downcall("TA_OBV",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // PLUS_DI - Plus Directional Indicator
    public static final MethodHandle TA_PLUS_DI = downcall("TA_PLUS_DI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // PLUS_DM - Plus Directional Movement
    public static final MethodHandle TA_PLUS_DM = downcall("TA_PLUS_DM",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // PPO - Percentage Price Oscillator
    public static final MethodHandle TA_PPO = downcall("TA_PPO",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ROC - Rate of change : ((price/prevPrice)-1)*100
    public static final MethodHandle TA_ROC = downcall("TA_ROC",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ROCP - Rate of change Percentage: (price-prevPrice)/prevPrice
    public static final MethodHandle TA_ROCP = downcall("TA_ROCP",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ROCR - Rate of change ratio: (price/prevPrice)
    public static final MethodHandle TA_ROCR = downcall("TA_ROCR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ROCR100 - Rate of change ratio 100 scale: (price/prevPrice)*100
    public static final MethodHandle TA_ROCR100 = downcall("TA_ROCR100",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // RSI - Relative Strength Index
    public static final MethodHandle TA_RSI = downcall("TA_RSI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SAR - Parabolic SAR
    public static final MethodHandle TA_SAR = downcall("TA_SAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SAREXT - Parabolic SAR - Extended
    public static final MethodHandle TA_SAREXT = downcall("TA_SAREXT",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SIN - Vector Trigonometric Sin
    public static final MethodHandle TA_SIN = downcall("TA_SIN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SINH - Vector Trigonometric Sinh
    public static final MethodHandle TA_SINH = downcall("TA_SINH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SMA - Simple Moving Average
    public static final MethodHandle TA_SMA = downcall("TA_SMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SQRT - Vector Square Root
    public static final MethodHandle TA_SQRT = downcall("TA_SQRT",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // STDDEV - Standard Deviation
    public static final MethodHandle TA_STDDEV = downcall("TA_STDDEV",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // STOCH - Stochastic
    public static final MethodHandle TA_STOCH = downcall("TA_STOCH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // STOCHF - Stochastic Fast
    public static final MethodHandle TA_STOCHF = downcall("TA_STOCHF",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // STOCHRSI - Stochastic Relative Strength Index
    public static final MethodHandle TA_STOCHRSI = downcall("TA_STOCHRSI",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SUB - Vector Arithmetic Subtraction
    public static final MethodHandle TA_SUB = downcall("TA_SUB",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // SUM - Summation
    public static final MethodHandle TA_SUM = downcall("TA_SUM",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // T3 - Triple Exponential Moving Average (T3)
    public static final MethodHandle TA_T3 = downcall("TA_T3",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TAN - Vector Trigonometric Tan
    public static final MethodHandle TA_TAN = downcall("TA_TAN",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TANH - Vector Trigonometric Tanh
    public static final MethodHandle TA_TANH = downcall("TA_TANH",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TEMA - Triple Exponential Moving Average
    public static final MethodHandle TA_TEMA = downcall("TA_TEMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TRANGE - True Range
    public static final MethodHandle TA_TRANGE = downcall("TA_TRANGE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TRIMA - Triangular Moving Average
    public static final MethodHandle TA_TRIMA = downcall("TA_TRIMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TRIX - 1-day Rate-Of-Change (ROC) of a Triple Smooth EMA
    public static final MethodHandle TA_TRIX = downcall("TA_TRIX",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TSF - Time Series Forecast
    public static final MethodHandle TA_TSF = downcall("TA_TSF",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // TYPPRICE - Typical Price
    public static final MethodHandle TA_TYPPRICE = downcall("TA_TYPPRICE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // ULTOSC - Ultimate Oscillator
    public static final MethodHandle TA_ULTOSC = downcall("TA_ULTOSC",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // VAR - Variance
    public static final MethodHandle TA_VAR = downcall("TA_VAR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // WCLPRICE - Weighted Close Price
    public static final MethodHandle TA_WCLPRICE = downcall("TA_WCLPRICE",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // WILLR - Williams' %R
    public static final MethodHandle TA_WILLR = downcall("TA_WILLR",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    // WMA - Weighted Moving Average
    public static final MethodHandle TA_WMA = downcall("TA_WMA",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS));

}
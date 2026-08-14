package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function MACDFIX: Moving Average Convergence/Divergence Fix 12/26.
 */
public class MacdFix {

    private static final Logger logger = LoggerFactory.getLogger(MacdFix.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal, int optInSignalPeriod) throws ArithmeticException, IndexOutOfBoundsException {
        // Input validation
        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new IndexOutOfBoundsException("Invalid startIdx or endIdx. startIdx=" + startIdx + ", endIdx=" + endIdx);
        }
        if (inreal == null || inreal.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'inreal' is null or too small for endIdx=" + endIdx);
        }
        int allocationSize = inreal.length;

        try (var arena = Arena.ofConfined()) {
            var inrealSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, inreal);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outMACDSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outMACDSignalSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outMACDHistSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_MACDFIX, startIdx, endIdx, inrealSeg, optInSignalPeriod, outBegIdx, outNBElement, outMACDSeg, outMACDSignalSeg, outMACDHistSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function MACDFIX returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function MACDFIX returned error code: " + retCode);
            }

            double[] outMACD = new double[allocationSize];
            MemorySegment.copy(outMACDSeg, ValueLayout.JAVA_DOUBLE, 0, outMACD, 0, allocationSize);
            double[] outMACDSignal = new double[allocationSize];
            MemorySegment.copy(outMACDSignalSeg, ValueLayout.JAVA_DOUBLE, 0, outMACDSignal, 0, allocationSize);
            double[] outMACDHist = new double[allocationSize];
            MemorySegment.copy(outMACDHistSeg, ValueLayout.JAVA_DOUBLE, 0, outMACDHist, 0, allocationSize);

            return MACDResult.builder()
                .outMACD(outMACD)
                .outMACDSignal(outMACDSignal)
                .outMACDHist(outMACDHist)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
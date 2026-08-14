package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function SAREXT: Parabolic SAR - Extended.
 */
public class SarExt {

    private static final Logger logger = LoggerFactory.getLogger(SarExt.class);

    public static Result execute(int startIdx, int endIdx, double[] high, double[] low, double optInStartValue, double optInOffsetonReverse, double optInAFInitLong, double optInAFLong, double optInAFMaxLong, double optInAFInitShort, double optInAFShort, double optInAFMaxShort) throws ArithmeticException, IndexOutOfBoundsException {
        // Input validation
        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new IndexOutOfBoundsException("Invalid startIdx or endIdx. startIdx=" + startIdx + ", endIdx=" + endIdx);
        }
        if (high == null || high.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'high' is null or too small for endIdx=" + endIdx);
        }
        if (low == null || low.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'low' is null or too small for endIdx=" + endIdx);
        }
        int allocationSize = high.length;

        try (var arena = Arena.ofConfined()) {
            var highSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, high);
            var lowSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, low);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outRealSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_SAREXT, startIdx, endIdx, highSeg, lowSeg, optInStartValue, optInOffsetonReverse, optInAFInitLong, optInAFLong, optInAFMaxLong, optInAFInitShort, optInAFShort, optInAFMaxShort, outBegIdx, outNBElement, outRealSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function SAREXT returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function SAREXT returned error code: " + retCode);
            }

            double[] outReal = new double[allocationSize];
            MemorySegment.copy(outRealSeg, ValueLayout.JAVA_DOUBLE, 0, outReal, 0, allocationSize);

            return RealResult.builder()
                .outReal(outReal)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
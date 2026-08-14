package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function HT_TRENDMODE: Hilbert Transform - Trend vs Cycle Mode.
 */
public class HtTrendMode {

    private static final Logger logger = LoggerFactory.getLogger(HtTrendMode.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal) throws ArithmeticException, IndexOutOfBoundsException {
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
            var outIntegerSeg = arena.allocateFrom(ValueLayout.JAVA_INT, new int[allocationSize]);

            int retCode = TALib.call(TALib.TA_HT_TRENDMODE, startIdx, endIdx, inrealSeg, outBegIdx, outNBElement, outIntegerSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function HT_TRENDMODE returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function HT_TRENDMODE returned error code: " + retCode);
            }

            int[] outInteger = new int[allocationSize];
            MemorySegment.copy(outIntegerSeg, ValueLayout.JAVA_INT, 0, outInteger, 0, allocationSize);

            return IntegerResult.builder()
                .outInteger(outInteger)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
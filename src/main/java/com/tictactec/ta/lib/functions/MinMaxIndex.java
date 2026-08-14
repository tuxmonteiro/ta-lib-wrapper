package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function MINMAXINDEX: Indexes of lowest and highest values over a specified period.
 */
public class MinMaxIndex {

    private static final Logger logger = LoggerFactory.getLogger(MinMaxIndex.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal, int optInTimePeriod) throws ArithmeticException, IndexOutOfBoundsException {
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
            var outMinIdxSeg = arena.allocateFrom(ValueLayout.JAVA_INT, new int[allocationSize]);
            var outMaxIdxSeg = arena.allocateFrom(ValueLayout.JAVA_INT, new int[allocationSize]);

            int retCode = TALib.call(TALib.TA_MINMAXINDEX, startIdx, endIdx, inrealSeg, optInTimePeriod, outBegIdx, outNBElement, outMinIdxSeg, outMaxIdxSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function MINMAXINDEX returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function MINMAXINDEX returned error code: " + retCode);
            }

            int[] outMinIdx = new int[allocationSize];
            MemorySegment.copy(outMinIdxSeg, ValueLayout.JAVA_INT, 0, outMinIdx, 0, allocationSize);
            int[] outMaxIdx = new int[allocationSize];
            MemorySegment.copy(outMaxIdxSeg, ValueLayout.JAVA_INT, 0, outMaxIdx, 0, allocationSize);

            return MinMaxIdxResult.builder()
                .outMinIdx(outMinIdx)
                .outMaxIdx(outMaxIdx)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
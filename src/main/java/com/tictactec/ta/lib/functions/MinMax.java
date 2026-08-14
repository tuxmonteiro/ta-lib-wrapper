package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function MINMAX: Lowest and highest values over a specified period.
 */
public class MinMax {

    private static final Logger logger = LoggerFactory.getLogger(MinMax.class);

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
            var outMinSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outMaxSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_MINMAX, startIdx, endIdx, inrealSeg, optInTimePeriod, outBegIdx, outNBElement, outMinSeg, outMaxSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function MINMAX returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function MINMAX returned error code: " + retCode);
            }

            double[] outMin = new double[allocationSize];
            MemorySegment.copy(outMinSeg, ValueLayout.JAVA_DOUBLE, 0, outMin, 0, allocationSize);
            double[] outMax = new double[allocationSize];
            MemorySegment.copy(outMaxSeg, ValueLayout.JAVA_DOUBLE, 0, outMax, 0, allocationSize);

            return MinMaxResult.builder()
                .outMin(outMin)
                .outMax(outMax)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
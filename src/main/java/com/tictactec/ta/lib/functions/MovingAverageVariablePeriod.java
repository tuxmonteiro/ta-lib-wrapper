package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function MAVP: Moving average with variable period.
 */
public class MovingAverageVariablePeriod {

    private static final Logger logger = LoggerFactory.getLogger(MovingAverageVariablePeriod.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal, double[] inperiods, int optInMinimumPeriod, int optInMaximumPeriod, int optInMAType) throws ArithmeticException, IndexOutOfBoundsException {
        // Input validation
        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new IndexOutOfBoundsException("Invalid startIdx or endIdx. startIdx=" + startIdx + ", endIdx=" + endIdx);
        }
        if (inreal == null || inreal.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'inreal' is null or too small for endIdx=" + endIdx);
        }
        if (inperiods == null || inperiods.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'inperiods' is null or too small for endIdx=" + endIdx);
        }
        int allocationSize = inreal.length;

        try (var arena = Arena.ofConfined()) {
            var inrealSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, inreal);
            var inperiodsSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, inperiods);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outRealSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_MAVP, startIdx, endIdx, inrealSeg, inperiodsSeg, optInMinimumPeriod, optInMaximumPeriod, optInMAType, outBegIdx, outNBElement, outRealSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function MAVP returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function MAVP returned error code: " + retCode);
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
package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function CORREL: Pearson's Correlation Coefficient (r).
 */
public class Correl {

    private static final Logger logger = LoggerFactory.getLogger(Correl.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal0, double[] inreal1, int optInTimePeriod) throws ArithmeticException, IndexOutOfBoundsException {
        // Input validation
        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new IndexOutOfBoundsException("Invalid startIdx or endIdx. startIdx=" + startIdx + ", endIdx=" + endIdx);
        }
        if (inreal0 == null || inreal0.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'inreal0' is null or too small for endIdx=" + endIdx);
        }
        if (inreal1 == null || inreal1.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'inreal1' is null or too small for endIdx=" + endIdx);
        }
        int allocationSize = inreal0.length;

        try (var arena = Arena.ofConfined()) {
            var inreal0Seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, inreal0);
            var inreal1Seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, inreal1);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outRealSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_CORREL, startIdx, endIdx, inreal0Seg, inreal1Seg, optInTimePeriod, outBegIdx, outNBElement, outRealSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function CORREL returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function CORREL returned error code: " + retCode);
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
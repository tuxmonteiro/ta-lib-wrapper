package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function MAMA: MESA Adaptive Moving Average.
 */
public class Mama {

    private static final Logger logger = LoggerFactory.getLogger(Mama.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal, double optInFastLimit, double optInSlowLimit) throws ArithmeticException, IndexOutOfBoundsException {
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
            var outMAMASeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outFAMASeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_MAMA, startIdx, endIdx, inrealSeg, optInFastLimit, optInSlowLimit, outBegIdx, outNBElement, outMAMASeg, outFAMASeg);
            if (retCode != 0) {
                logger.error("TA-Lib function MAMA returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function MAMA returned error code: " + retCode);
            }

            double[] outMAMA = new double[allocationSize];
            MemorySegment.copy(outMAMASeg, ValueLayout.JAVA_DOUBLE, 0, outMAMA, 0, allocationSize);
            double[] outFAMA = new double[allocationSize];
            MemorySegment.copy(outFAMASeg, ValueLayout.JAVA_DOUBLE, 0, outFAMA, 0, allocationSize);

            return MamaResult.builder()
                .outMAMA(outMAMA)
                .outFAMA(outFAMA)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
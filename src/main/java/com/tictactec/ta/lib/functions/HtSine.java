package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function HT_SINE: Hilbert Transform - SineWave.
 */
public class HtSine {

    private static final Logger logger = LoggerFactory.getLogger(HtSine.class);

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
            var outSineSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outLeadSineSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_HT_SINE, startIdx, endIdx, inrealSeg, outBegIdx, outNBElement, outSineSeg, outLeadSineSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function HT_SINE returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function HT_SINE returned error code: " + retCode);
            }

            double[] outSine = new double[allocationSize];
            MemorySegment.copy(outSineSeg, ValueLayout.JAVA_DOUBLE, 0, outSine, 0, allocationSize);
            double[] outLeadSine = new double[allocationSize];
            MemorySegment.copy(outLeadSineSeg, ValueLayout.JAVA_DOUBLE, 0, outLeadSine, 0, allocationSize);

            return HtSineResult.builder()
                .outSine(outSine)
                .outLeadSine(outLeadSine)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
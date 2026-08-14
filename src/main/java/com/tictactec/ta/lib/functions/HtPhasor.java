package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function HT_PHASOR: Hilbert Transform - Phasor Components.
 */
public class HtPhasor {

    private static final Logger logger = LoggerFactory.getLogger(HtPhasor.class);

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
            var outInPhaseSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outQuadratureSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_HT_PHASOR, startIdx, endIdx, inrealSeg, outBegIdx, outNBElement, outInPhaseSeg, outQuadratureSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function HT_PHASOR returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function HT_PHASOR returned error code: " + retCode);
            }

            double[] outInPhase = new double[allocationSize];
            MemorySegment.copy(outInPhaseSeg, ValueLayout.JAVA_DOUBLE, 0, outInPhase, 0, allocationSize);
            double[] outQuadrature = new double[allocationSize];
            MemorySegment.copy(outQuadratureSeg, ValueLayout.JAVA_DOUBLE, 0, outQuadrature, 0, allocationSize);

            return HtPhasorResult.builder()
                .outInPhase(outInPhase)
                .outQuadrature(outQuadrature)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
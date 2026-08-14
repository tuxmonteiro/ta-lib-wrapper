package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function STOCH: Stochastic.
 */
public class Stoch {

    private static final Logger logger = LoggerFactory.getLogger(Stoch.class);

    public static Result execute(int startIdx, int endIdx, double[] high, double[] low, double[] close, int optInFastKPeriod, int optInSlowKPeriod, int optInSlowKMA, int optInSlowDPeriod, int optInSlowDMA) throws ArithmeticException, IndexOutOfBoundsException {
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
        if (close == null || close.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'close' is null or too small for endIdx=" + endIdx);
        }
        int allocationSize = high.length;

        try (var arena = Arena.ofConfined()) {
            var highSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, high);
            var lowSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, low);
            var closeSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, close);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outSlowKSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outSlowDSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_STOCH, startIdx, endIdx, highSeg, lowSeg, closeSeg, optInFastKPeriod, optInSlowKPeriod, optInSlowKMA, optInSlowDPeriod, optInSlowDMA, outBegIdx, outNBElement, outSlowKSeg, outSlowDSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function STOCH returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function STOCH returned error code: " + retCode);
            }

            double[] outSlowK = new double[allocationSize];
            MemorySegment.copy(outSlowKSeg, ValueLayout.JAVA_DOUBLE, 0, outSlowK, 0, allocationSize);
            double[] outSlowD = new double[allocationSize];
            MemorySegment.copy(outSlowDSeg, ValueLayout.JAVA_DOUBLE, 0, outSlowD, 0, allocationSize);

            return SlowResult.builder()
                .outSlowK(outSlowK)
                .outSlowD(outSlowD)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
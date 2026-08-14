package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function STOCHF: Stochastic Fast.
 */
public class StochF {

    private static final Logger logger = LoggerFactory.getLogger(StochF.class);

    public static Result execute(int startIdx, int endIdx, double[] high, double[] low, double[] close, int optInFastKPeriod, int optInFastDPeriod, int optInFastDMA) throws ArithmeticException, IndexOutOfBoundsException {
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
            var outFastKSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outFastDSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_STOCHF, startIdx, endIdx, highSeg, lowSeg, closeSeg, optInFastKPeriod, optInFastDPeriod, optInFastDMA, outBegIdx, outNBElement, outFastKSeg, outFastDSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function STOCHF returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function STOCHF returned error code: " + retCode);
            }

            double[] outFastK = new double[allocationSize];
            MemorySegment.copy(outFastKSeg, ValueLayout.JAVA_DOUBLE, 0, outFastK, 0, allocationSize);
            double[] outFastD = new double[allocationSize];
            MemorySegment.copy(outFastDSeg, ValueLayout.JAVA_DOUBLE, 0, outFastD, 0, allocationSize);

            return FastResult.builder()
                .outFastK(outFastK)
                .outFastD(outFastD)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
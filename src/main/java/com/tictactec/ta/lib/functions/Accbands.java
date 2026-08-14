package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function ACCBANDS: Acceleration Bands.
 */
public class Accbands {

    private static final Logger logger = LoggerFactory.getLogger(Accbands.class);

    public static Result execute(int startIdx, int endIdx, double[] high, double[] low, double[] close, int optInTimePeriod) throws ArithmeticException, IndexOutOfBoundsException {
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
            var outRealUpperBandSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outRealMiddleBandSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outRealLowerBandSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_ACCBANDS, startIdx, endIdx, highSeg, lowSeg, closeSeg, optInTimePeriod, outBegIdx, outNBElement, outRealUpperBandSeg, outRealMiddleBandSeg, outRealLowerBandSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function ACCBANDS returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function ACCBANDS returned error code: " + retCode);
            }

            double[] outRealUpperBand = new double[allocationSize];
            MemorySegment.copy(outRealUpperBandSeg, ValueLayout.JAVA_DOUBLE, 0, outRealUpperBand, 0, allocationSize);
            double[] outRealMiddleBand = new double[allocationSize];
            MemorySegment.copy(outRealMiddleBandSeg, ValueLayout.JAVA_DOUBLE, 0, outRealMiddleBand, 0, allocationSize);
            double[] outRealLowerBand = new double[allocationSize];
            MemorySegment.copy(outRealLowerBandSeg, ValueLayout.JAVA_DOUBLE, 0, outRealLowerBand, 0, allocationSize);

            return BandsResult.builder()
                .outRealUpperBand(outRealUpperBand)
                .outRealMiddleBand(outRealMiddleBand)
                .outRealLowerBand(outRealLowerBand)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
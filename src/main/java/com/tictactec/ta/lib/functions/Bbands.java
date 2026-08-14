package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function BBANDS: Bollinger Bands.
 */
public class Bbands {

    private static final Logger logger = LoggerFactory.getLogger(Bbands.class);

    public static Result execute(int startIdx, int endIdx, double[] inreal, int optInTimePeriod, double optInDeviationsup, double optInDeviationsdown, int optInMAType) throws ArithmeticException, IndexOutOfBoundsException {
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
            var outRealUpperBandSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outRealMiddleBandSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outRealLowerBandSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_BBANDS, startIdx, endIdx, inrealSeg, optInTimePeriod, optInDeviationsup, optInDeviationsdown, optInMAType, outBegIdx, outNBElement, outRealUpperBandSeg, outRealMiddleBandSeg, outRealLowerBandSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function BBANDS returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function BBANDS returned error code: " + retCode);
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
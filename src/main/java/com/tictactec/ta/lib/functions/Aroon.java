package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function AROON: Aroon.
 */
public class Aroon {

    private static final Logger logger = LoggerFactory.getLogger(Aroon.class);

    public static Result execute(int startIdx, int endIdx, double[] high, double[] low, int optInTimePeriod) throws ArithmeticException, IndexOutOfBoundsException {
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
        int allocationSize = high.length;

        try (var arena = Arena.ofConfined()) {
            var highSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, high);
            var lowSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, low);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outAroonDownSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);
            var outAroonUpSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, new double[allocationSize]);

            int retCode = TALib.call(TALib.TA_AROON, startIdx, endIdx, highSeg, lowSeg, optInTimePeriod, outBegIdx, outNBElement, outAroonDownSeg, outAroonUpSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function AROON returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function AROON returned error code: " + retCode);
            }

            double[] outAroonDown = new double[allocationSize];
            MemorySegment.copy(outAroonDownSeg, ValueLayout.JAVA_DOUBLE, 0, outAroonDown, 0, allocationSize);
            double[] outAroonUp = new double[allocationSize];
            MemorySegment.copy(outAroonUpSeg, ValueLayout.JAVA_DOUBLE, 0, outAroonUp, 0, allocationSize);

            return AroonResult.builder()
                .outAroonDown(outAroonDown)
                .outAroonUp(outAroonUp)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
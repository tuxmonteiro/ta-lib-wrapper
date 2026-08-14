package com.tictactec.ta.lib.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tictactec.ta.lib.results.*;
import com.tictactec.ta.lib.TALib;

import java.lang.foreign.*;

/**
 * This class is a wrapper for the TA-Lib function CDLEVENINGSTAR: Evening Star.
 */
public class CdlEveningStar {

    private static final Logger logger = LoggerFactory.getLogger(CdlEveningStar.class);

    public static Result execute(int startIdx, int endIdx, double[] open, double[] high, double[] low, double[] close, double optInPenetration) throws ArithmeticException, IndexOutOfBoundsException {
        // Input validation
        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new IndexOutOfBoundsException("Invalid startIdx or endIdx. startIdx=" + startIdx + ", endIdx=" + endIdx);
        }
        if (open == null || open.length <= endIdx) {
            throw new IndexOutOfBoundsException("Input array 'open' is null or too small for endIdx=" + endIdx);
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
        int allocationSize = open.length;

        try (var arena = Arena.ofConfined()) {
            var openSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, open);
            var highSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, high);
            var lowSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, low);
            var closeSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, close);
            var outBegIdx = arena.allocate(ValueLayout.JAVA_INT);
            var outNBElement = arena.allocate(ValueLayout.JAVA_INT);
            var outIntegerSeg = arena.allocateFrom(ValueLayout.JAVA_INT, new int[allocationSize]);

            int retCode = TALib.call(TALib.TA_CDLEVENINGSTAR, startIdx, endIdx, openSeg, highSeg, lowSeg, closeSeg, optInPenetration, outBegIdx, outNBElement, outIntegerSeg);
            if (retCode != 0) {
                logger.error("TA-Lib function CDLEVENINGSTAR returned error code: {}", retCode);
                throw new ArithmeticException("TA-Lib function CDLEVENINGSTAR returned error code: " + retCode);
            }

            int[] outInteger = new int[allocationSize];
            MemorySegment.copy(outIntegerSeg, ValueLayout.JAVA_INT, 0, outInteger, 0, allocationSize);

            return IntegerResult.builder()
                .outInteger(outInteger)
                .outBegIdx(outBegIdx.get(ValueLayout.JAVA_INT, 0))
                .outNBElement(outNBElement.get(ValueLayout.JAVA_INT, 0))
                .build();
        }
    }
}
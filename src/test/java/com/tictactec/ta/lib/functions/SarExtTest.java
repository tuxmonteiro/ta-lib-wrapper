package com.tictactec.ta.lib.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.tictactec.ta.lib.functions.*;
import com.tictactec.ta.lib.results.*;

public class SarExtTest {

    @Test
    public void testExecute() {
        // This is a simple smoke test to ensure the function can be called without crashing.
        int size = 100;
        int startIdx = 0;
        int endIdx = size - 1;
        double[] high = new double[size];
        for(int i=0; i<size; i++) { high[i] = i; } // Dummy data
        double[] low = new double[size];
        for(int i=0; i<size; i++) { low[i] = i; } // Dummy data
        // TODO: optInoptInStartValue default: 0.000000e+0
        // TODO: optInoptInOffsetonReverse default: 0.000000e+0
        // TODO: optInoptInAFInitLong default: 2.000000e-2
        // TODO: optInoptInAFLong default: 2.000000e-2
        // TODO: optInoptInAFMaxLong default: 2.000000e-1
        // TODO: optInoptInAFInitShort default: 2.000000e-2
        // TODO: optInoptInAFShort default: 2.000000e-2
        // TODO: optInoptInAFMaxShort default: 2.000000e-1
        Result result = SarExt.execute(startIdx, endIdx, high, low, (double)0.000000e+0, (double)0.000000e+0, (double)2.000000e-2, (double)2.000000e-2, (double)2.000000e-1, (double)2.000000e-2, (double)2.000000e-2, (double)2.000000e-1);
        assertNotNull(result);
        // Further assertions can be added here if expected values are known.
    }
}
package com.tictactec.ta.lib.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.tictactec.ta.lib.functions.*;
import com.tictactec.ta.lib.results.*;

public class WillRTest {

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
        double[] close = new double[size];
        for(int i=0; i<size; i++) { close[i] = i; } // Dummy data
        // TODO: optInoptInTimePeriod default: 14
        Result result = WillR.execute(startIdx, endIdx, high, low, close, (int)14);
        assertNotNull(result);
        // Further assertions can be added here if expected values are known.
    }
}
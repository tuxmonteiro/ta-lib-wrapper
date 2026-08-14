package com.tictactec.ta.lib.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.tictactec.ta.lib.functions.*;
import com.tictactec.ta.lib.results.*;

public class CorrelTest {

    @Test
    public void testExecute() {
        // This is a simple smoke test to ensure the function can be called without crashing.
        int size = 100;
        int startIdx = 0;
        int endIdx = size - 1;
        double[] inreal0 = new double[size];
        for(int i=0; i<size; i++) { inreal0[i] = i; } // Dummy data
        double[] inreal1 = new double[size];
        for(int i=0; i<size; i++) { inreal1[i] = i; } // Dummy data
        // TODO: optInoptInTimePeriod default: 30
        Result result = Correl.execute(startIdx, endIdx, inreal0, inreal1, (int)30);
        assertNotNull(result);
        // Further assertions can be added here if expected values are known.
    }
}
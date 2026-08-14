package com.tictactec.ta.lib.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.tictactec.ta.lib.functions.*;
import com.tictactec.ta.lib.results.*;

public class MovingAverageVariablePeriodTest {

    @Test
    public void testExecute() {
        // This is a simple smoke test to ensure the function can be called without crashing.
        int size = 100;
        int startIdx = 0;
        int endIdx = size - 1;
        double[] inreal = new double[size];
        for(int i=0; i<size; i++) { inreal[i] = i; } // Dummy data
        double[] inperiods = new double[size];
        for(int i=0; i<size; i++) { inperiods[i] = i; } // Dummy data
        // TODO: optInoptInMinimumPeriod default: 2
        // TODO: optInoptInMaximumPeriod default: 30
        // TODO: optInoptInMAType default: 0
        Result result = MovingAverageVariablePeriod.execute(startIdx, endIdx, inreal, inperiods, (int)2, (int)30, (int)0);
        assertNotNull(result);
        // Further assertions can be added here if expected values are known.
    }
}
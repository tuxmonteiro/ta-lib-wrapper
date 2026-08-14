package com.tictactec.ta.lib.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.tictactec.ta.lib.functions.*;
import com.tictactec.ta.lib.results.*;

public class MamaTest {

    @Test
    public void testExecute() {
        // This is a simple smoke test to ensure the function can be called without crashing.
        int size = 100;
        int startIdx = 0;
        int endIdx = size - 1;
        double[] inreal = new double[size];
        for(int i=0; i<size; i++) { inreal[i] = i; } // Dummy data
        // TODO: optInoptInFastLimit default: 5.000000e-1
        // TODO: optInoptInSlowLimit default: 5.000000e-2
        Result result = Mama.execute(startIdx, endIdx, inreal, (double)5.000000e-1, (double)5.000000e-2);
        assertNotNull(result);
        // Further assertions can be added here if expected values are known.
    }
}
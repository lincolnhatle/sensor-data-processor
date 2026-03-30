package com.sensordata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SensorDataProcessorTest {

    private final String outputFileName = "RacingStatsData.txt";

    @BeforeEach
    public void setup() {
        // Clean up output file before tests if it exists
        File file = new File(outputFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    public void teardown() {
        // Clean up output file after tests
        File file = new File(outputFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testCalculateBasicContinue() {
        // Hits "else continue;" at condition end since all conditions fail
        // data=10.0, d=2.0, limit=1.0 -> data2 = 5-1 = 4.0
        double[][][] data = {{{10.0}}};
        double[][] limits = {{1.0}};

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        
        assertDoesNotThrow(() -> processor.calculate(2.0));
        
        File file = new File(outputFileName);
        assertTrue(file.exists(), "The stats output file should have been created.");
    }

    @Test
    public void testCalculateEmptyArrays_ThrowsOutsideTry() {
        // Throws ArrayIndexOutOfBoundsException during data2 initialization AT LINE 35 (outside try block)
        double[][][] data = new double[0][0][0];
        double[][] limits = new double[0][0];

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> processor.calculate(2.0));
    }

    @Test
    public void testCalculateNullArrays_ThrowsOutsideTry() {
        // Throws NullPointerException during data2 initialization AT LINE 35 (outside try block)
        SensorDataProcessor processor = new SensorDataProcessor(null, null);
        assertThrows(NullPointerException.class, () -> processor.calculate(1.0));
    }

    @Test
    public void testCalculate_ExceptionInsideTry() {
        // Trigger an exception INSIDE the try block to hit lines 73-77.
        // data[1] is null, so when i=1, it will throw NullPointerException inside the nested loops (line 46)
        double[][][] data = new double[2][][];
        data[0] = new double[][]{{1.0}};
        data[1] = null;
        
        double[][] limits = new double[][]{{1.0}, {1.0}};
        
        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        
        // The exception is caught within calculate() in the try...catch block at lines 73-77
        assertDoesNotThrow(() -> processor.calculate(1.0));
    }

    @Test
    public void testCalculateCondition1_TrueTrue() {
        // Condition 1: average(data2) > 10 && average(data2) < 50
        // Trigger both conditions to be true.
        // d=1.0, limit=0.0 -> data2 = data.
        // data = 30.0 -> average = 30.0 (> 10 and < 50 -> breaks)
        double[][][] data = {{{30.0}}};
        double[][] limits = {{0.0}};

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertDoesNotThrow(() -> processor.calculate(1.0));
    }

    @Test
    public void testCalculateCondition1_TrueFalse() {
        // Condition 1: average(data2) > 10 && average(data2) < 50
        // Trigger first to be true, second to be false.
        // data = 60.0 -> average = 60.0 (> 10 is true, < 50 is false)
        double[][][] data = {{{60.0}}};
        double[][] limits = {{0.0}};

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertDoesNotThrow(() -> processor.calculate(1.0));
    }

    @Test
    public void testCalculateCondition2_True() {
        // Condition 2: Math.max(data, data2) > data  ==> data2 > data
        // Trigger condition 1 to fail, and condition 2 to be true.
        // data = 1.0, limit = 0.0, d = 0.5 -> data2 = 2.0.
        // Average(data2) = 2.0 (Fails Cond 1). max(1.0, 2.0) = 2.0 > 1.0 -> Cond 2 True (breaks).
        double[][][] data = {{{1.0}}};
        double[][] limits = {{0.0}};

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertDoesNotThrow(() -> processor.calculate(0.5));
    }

    @Test
    public void testCalculateCondition3_AllTrue() {
        // Cond 3: abs(data)^3 < abs(data2)^3 && average(data) < data2 && (i+1)*(j+1)>0
        // Target Cond 1 Fails, Cond 2 Fails, Cond 3 all True.
        // data = {-10.0, -100.0}, d = 1.0, limit = 2.0.
        // k=0: data2 = -14.0. cond 2 fails. abs(-10)^3 < abs(-14)^3 (True). 
        // average(-55.0) < -14.0 (True). And dimensions > 0 (True).
        double[][][] data = {{{-10.0, -100.0}}};
        double[][] limits = {{2.0}};

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertDoesNotThrow(() -> processor.calculate(1.0));
    }

    @Test
    public void testCalculateCondition3_B_False() {
        // Cover branch where Condition 3 Part A is true but Part B is false explicitly
        // data = -10.0, limit = 2.0, d = 1.0 -> data2 = -14.0
        // abs(-10)^3 < abs(-14)^3 (True)
        // average(data) is -10.0. -10.0 < -14.0 is False.
        double[][][] data = {{{-10.0}}};
        double[][] limits = {{2.0}};

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertDoesNotThrow(() -> processor.calculate(1.0));
    }

    @Test
    public void testCalculate_LargeArrays() {
        // Ensures full loop coverage across multiple outer and inner dimensions without breaks logic stopping execution.
        // Uses values designed to hit the 'continue' path constantly.
        double[][][] data = {
            { {10.0, 20.0}, {30.0, 40.0} },
            { {50.0, 60.0}, {70.0, 80.0} }
        };
        double[][] limits = { {1.0, 2.0}, {3.0, 4.0} };

        SensorDataProcessor processor = new SensorDataProcessor(data, limits);
        assertDoesNotThrow(() -> processor.calculate(1.0));
    }
}

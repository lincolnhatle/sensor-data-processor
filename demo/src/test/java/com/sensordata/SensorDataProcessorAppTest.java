package com.sensordata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorDataProcessorAppTest {

    private final String outputFileName = "RacingStatsData.txt";

    @BeforeEach
    public void setup() {
        File file = new File(outputFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    public void teardown() {
        File file = new File(outputFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> {
            SensorDataProcessorApp.main(new String[]{});
        });
        
        File file = new File(outputFileName);
        assertTrue(file.exists(), "The stats output file should have been created by main().");
    }
}

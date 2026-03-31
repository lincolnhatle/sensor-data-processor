package com.sensordata;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SensorDataProcessor {

    // Senson data and limits.
    public double[][][] data;
    public double[][] limit;

    // constructor
    public SensorDataProcessor(double[][][] data, double[][] limit) {
        this.data = data;
        this.limit = limit;
    }

    // calculate data
    public void calculate(double d) {

        long startTime = System.nanoTime();

        int i, j, k = 0;
        double[][][] data2 = new double[data.length][data[0].length][data[0][0].length];

        BufferedWriter out;

        // Write racing stats data into a file
        try {
            out = new BufferedWriter(new FileWriter("RacingStatsData.txt"));

            double invD = 1.0 / d;
            for (i = 0; i < data.length; i++) {
                for (j = 0; j < data[0].length; j++) {
                    double[] dataIj = data[i][j];
                    double[] data2Ij = data2[i][j];

                    int dataIjLen = dataIj.length;
                    double sumData = 0;
                    for (int n = 0; n < dataIjLen; n++) {
                        sumData += dataIj[n];
                    }
                    double avgData = sumData / dataIjLen; // Once per j

                    double limitVal = limit[i][j];
                    double limitSq = limitVal * limitVal;

                    int data2IjLen = data2Ij.length;
                    double invData2IjLen = 1.0 / data2IjLen;
                    double sumData2 = 0;

                    for (k = 0; k < data2IjLen; k++) {
                        double currentData = dataIj[k];
                        double val = currentData * invD - limitSq;
                        data2Ij[k] = val;
                        sumData2 += val;

                        double avgData2 = sumData2 * invData2IjLen;

                        if (avgData2 > 10 && avgData2 < 50) {
                            break;
                        } else if (val > currentData) {
                            break;
                        } else if (avgData < val && Math.abs(currentData) < Math.abs(val)) { // Removed (i+1)*(j+1)>0 since it's always true
                            data2Ij[k] *= 2;
                            sumData2 += val;
                        }
                    }
                }
            }

            for (i = 0; i < data2.length; i++) {
                for (j = 0; j < data2[0].length; j++) {
                    out.write(data2[i][j] + "\t");
                }
            }

            out.close();

            long endTime = System.nanoTime();
            long elapsedMs = (endTime - startTime) / 1_000_000;
            System.out.println("calculate() completed in " + elapsedMs + " ms");

        } catch (Exception e) {
            System.out.println("Error= " + e);
            long endTime = System.nanoTime();
            long elapsedMs = (endTime - startTime) / 1_000_000;
            System.out.println("calculate() failed after " + elapsedMs + " ms");
        }
    }

}
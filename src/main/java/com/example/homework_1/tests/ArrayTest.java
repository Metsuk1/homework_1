package com.example.homework_1.tests;

import com.example.homework_1.arrayImplementation.ArrayOperations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class ArrayTest {
    public static void main(String[] args) {
        StringBuilder rep =  new StringBuilder();
        rep.append("Performance Test Array").append("\n");

        rep.append(runTest("Array shift Performance"));

        //wtire to file
        try(PrintWriter writer = new PrintWriter(new File("Performance_Test_Array.txt"))) {
            writer.write(rep.toString());
        } catch (IOException e) {
            System.out.println("Error to write to file" + e.getMessage());
        }
    }

    public static String runTest(String name){
        StringBuilder res = new StringBuilder();
        res.append("Testing ").append(name).append("\n");

        int[] sizes = {1000,10_000,100_000,1_000_000};
        int[] shifts = {1,10,100,1000};

        for(int size: sizes){
            res.append("Size ").append(size).append("\n");

            for(int shift: shifts){
                if(shift >= size)continue;

            int[] baseArr = generateArray(size);
            int[] arrForSystem = Arrays.copyOf(baseArr, baseArr.length);
            int[] arrForManual = Arrays.copyOf(baseArr, baseArr.length);

            //  System.arraycopy
                long startTimeSystem = System.nanoTime();
                ArrayOperations.shiftLeftSystemCopy(arrForSystem,shift);
                long endTimeSystem = System.nanoTime();

            // Manual loop shifts
                long startTimeManual = System.nanoTime();
                ArrayOperations.shiftLeftManualLoop(arrForManual,shift);
                long endTimeManual = System.nanoTime();

                long elapsedTimeSystem = endTimeSystem - startTimeSystem;
                long elapsedTimeManual = endTimeManual - startTimeManual;

                res.append(String.format("Shift: %-4d | System.arraycopy: %-8d ns | Manual Loop: %-8d ns\n",
                        shift, elapsedTimeSystem, elapsedTimeManual));
            }
            res.append("\n");
        }

        return res.toString();
    }

    private static int[]  generateArray(int size){
        Random rand = new Random();
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(100);
        }

        return array;
    }
}

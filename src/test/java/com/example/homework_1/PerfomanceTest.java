package com.example.homework_1;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PerfomanceTest {
    private static int bulk_size = 1_000_000;
    private static int add_remove_size = 10000;

    public static void main(String[] args) {
        StringBuilder rep = new StringBuilder();
        rep.append("PerfomanceTest\n");


        rep.append(runTest("ArrayList",new ArrayList<>()));
        rep.append(runTest("Linked List",new LinkedList<>()));
        rep.append(runTest("CustomList",new CustomList<>()));

        //записываем все в файлик
        try(PrintWriter writer = new PrintWriter(new FileWriter("performance_rep.txt"))){
            writer.write(rep.toString());
        }catch(IOException e ){
                System.out.println("Error to write to file" + e.getMessage());
        }
    }

    private static String runTest(String name,List<Integer> list){
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        StringBuilder res = new StringBuilder();
        res.append("Test ").append(name).append("\n");

        //test bulk add  million elements
        long startTime = System.nanoTime();
        long startUsedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);;

        for(int i = 0; i < bulk_size; i++){
            list.add(i);
        }

        long endTime = System.nanoTime();
        long endUsedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);;
        long totalTime = (endTime - startTime) / 1_000_000 ;
        res.append("Bulk Add - Time: ").append(totalTime).append(" ms , Memory: ")
                .append(endUsedMemory - startUsedMemory).append(" MB\n");

        //add + remove test
        list.clear();
        startTime = System.nanoTime();
        startUsedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);;

        for(int i = 0; i < add_remove_size; i++){
            list.add(i);
        }
        for(int i = 0;i < add_remove_size;i++){
            list.remove(0);
        }
        endTime = System.nanoTime();
        endUsedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);;
        totalTime = (endTime - startTime) / 1_000_000 ;
        res.append("Add_Remove - time: ").append(totalTime).append(" ms , memory: ")
                .append(endUsedMemory - startUsedMemory).append(" mb\n\n");

        return res.toString();
    }

}

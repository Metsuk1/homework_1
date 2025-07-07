package com.example.homework_1.arrayImplementation;

public class ArrayOperations {

    /**
    Time complexity: O(n)
     Space complexity: O(1)
     shifting elements with using method System.arraycopy
     */
    public static void shiftLeftSystemCopy(int[] array, int positions) {
        if(array == null || positions < 0 || positions >= array.length) return;


        System.arraycopy(array,positions,array,0,array.length - positions);

        for(int i = array.length - positions; i < array.length; i++) {
           array[i] = 0;
        }
    }

    /** Shift array elements using manual for loop
     * Time complexity:O(n)
     * Space complexity: O(1)
     * */
    public static void shiftLeftManualLoop(int[] array, int positions) {
        if(array == null || positions < 0 || positions >= array.length) return;
        if(positions == 0) return;

        for(int i = 0; i < array.length - positions; i++) {
            if(i + positions >= array.length){
                System.out.println("Out of bounds " + i + " shift " + positions);
            }
            array[i] = array[i + positions];
        }
        for(int i = array.length - positions; i < array.length; i++) {
            array[i] = 0;
        }
    }

}

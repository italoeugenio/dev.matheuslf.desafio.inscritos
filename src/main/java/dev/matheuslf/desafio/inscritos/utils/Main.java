package dev.matheuslf.desafio.inscritos.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        int[][] nums = {
                {3, 1, 2, 4, 5},
                {1, 2, 3, 4},
                {3, 4, 5, 6}
        };

        HashMap<Integer, Integer> dic = new HashMap<>();
        for(int i = 0; i < nums.length ; i++){
            for(int j = 0; j < nums[i].length; j++){
               if(!dic.containsKey(nums[i][j])){
                   dic.put(nums[i][j], 1);
               } else{
                   dic.put(nums[i][j], dic.get(nums[i][j]) + 1);
               }
            }
        }

        //Keys
        Set<Integer> allKeys = dic.keySet();
        //Values
        Collection<Integer> allValues = dic.values();
    }
}

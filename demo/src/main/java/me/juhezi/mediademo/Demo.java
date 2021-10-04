package me.juhezi.mediademo;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Demo {

    public static Map<String, int[]> demo() {
        return new ImmutableMap.Builder<String, int[]>()
                .put("demo", new int[]{
                        274, 363, 402, 444, 563, 592, 646, 816, 818, 885
                })
                .build();
    }

}

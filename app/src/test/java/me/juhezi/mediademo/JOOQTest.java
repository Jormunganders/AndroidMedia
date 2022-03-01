package me.juhezi.mediademo;

import static org.junit.Assert.assertEquals;

import org.joor.Reflect;
import org.junit.Test;

public class JOOQTest {

    @Test
    public void testJooq() {
        String substring = Reflect.on("java.lang.String")
                .create("Hello World")
                .as(StringProxy.class)
                .substring(6);
        assertEquals(substring, "World");
    }

}

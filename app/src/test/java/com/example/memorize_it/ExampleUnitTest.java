package com.example.memorize_it;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        LocalDate localDate = LocalDate.now();
        assertEquals(true, "1" == "1");
    }
}
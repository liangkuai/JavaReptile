package test;

import org.junit.Test;

/**
 * Created by liangkuai on 2017/8/20.
 */
public class TestJunit {

    private String message = "Hello world";

    @Test
    public void ToStringTest() {
        MessageUnit messageUnit = new MessageUnit("Hello World");
        org.junit.Assert.assertEquals(message, messageUnit.toString());
    }
}

package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Error;
import tiktak.serialization.ValidationException;

@DisplayName("Error Tests")
public class ErrorTests {

    @Test
    void testConstructor1(){
        final int test = 101;
        final String test2 = "Standard Message";
        Error error = new Error(test, test2);

        Assertions.assertTrue(error.getCode() == test && error.getMessage().equals(test2));
    }

    @Test
    void testConstructor2(){
        final int test = 10;
        final String test2 = "Standard Message";

        Assertions.assertThrows(ValidationException.class, () -> new Error(test, test2));
    }

    @Test
    void testConstructor3(){
        final int test = 101;
        final String test2 = "Standard Message&";

        Assertions.assertThrows(ValidationException.class, () -> new Error(test, test2));
    }

    @Test
    void testConstructor4(){
        final int test = 101;
        final String test2 = null;

        Assertions.assertThrows(ValidationException.class, () -> new Error(test, test2));
    }

    @Test
    void testHashCode(){
        final Error error = new Error(100, "StandardError");
        final Error error2 = new Error(100, "StandardError");

        Assertions.assertEquals(error.hashCode(), error2.hashCode());
    }

    @Test
    void testEqualsSimple(){
        final Error error = new Error(100, "StandardError");
        final Error error2 = new Error(100, "StandardError");

        Assertions.assertEquals(error, error2);
    }

    @Test
    void testToString(){
        final String equals = "Error: code=100 message=StandardError";
        Error error = new Error(100, "StandardError");

        Assertions.assertEquals(equals, error.toString());
    }

    @Test
    void testGetCode(){
        final String equals = "Error: code=100 message=StandardError";
        Error error = new Error(100, "StandardError");

        Assertions.assertEquals(100, error.getCode());
    }

    @Test
    void testGetMessage(){
        final String equals = "Error: code=100 message=StandardError";
        Error error = new Error(100, "StandardError");

        Assertions.assertEquals("StandardError", error.getMessage());
    }

    @Test
    void testSetCode(){
        final int test = 101;
        Error error = new Error(100, "StandardError");
        error.setCode(test);

        Assertions.assertEquals(test, error.getCode());
    }

    @Test
    void testSetCode1(){
        final int test = 01;
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setCode(test));
    }

    @Test
    void testSetCode2(){
        final int test = 001;
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setCode(test));
    }

    @Test
    void testSetCode3(){
        final int test = 1011;
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setCode(test));
    }

    @Test
    void testSetCode4(){
        final int test = 9999999;
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setCode(test));
    }

    @Test
    void testSetMessage(){
        final String test = "Message123";
        Error error = new Error(100, "StandardError");
        error.setMessage(test);

        Assertions.assertEquals(test, error.getMessage());
    }

    @Test
    void testSetMessage1(){
        final String test = " ";
        Error error = new Error(100, "StandardError");
        error.setMessage(test);

        Assertions.assertEquals(test, error.getMessage());
    }

    @Test
    void testSetMessage2(){
        final String test = null;
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setMessage(test));
    }

    @Test
    void testSetMessage3(){
        final String test = "";
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setMessage(test));
    }

    @Test
    void testSetMessage4(){
        final String test = "3kn fjds#d";
        Error error = new Error(100, "StandardError");

        Assertions.assertThrows(ValidationException.class, () -> error.setMessage(test));
    }


}

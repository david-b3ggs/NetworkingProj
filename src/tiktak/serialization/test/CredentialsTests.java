package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Credentials;
import tiktak.serialization.ValidationException;

@DisplayName("Credentials Tests")
public class CredentialsTests {

    @Test
    void testToString(){
        final String hash = "12345678901234567890abcdf";
        Assertions.assertEquals("Credentials: hash=12345678901234567890abcdf", new Credentials(hash).toString());
    }

    @Test
    void testConstructor1(){
        final String hash = "";

        Assertions.assertThrows(ValidationException.class, () -> new Credentials(hash));
    }

    @Test
    void testConstructor2(){
        final String hash = "12345678901234567890abcdf1";

        Assertions.assertThrows(ValidationException.class, () -> new Credentials(hash));
    }

    @Test
    void testConstructor3(){
        final String hash = "12345678901234567890qbcdt";

        Assertions.assertThrows(ValidationException.class, () -> new Credentials(hash));
    }

    @Test
    void testConstructor5() {
        final String hash = null;

        Assertions.assertThrows(ValidationException.class, () -> new Credentials(hash));
    }

    @Test
    void testConstructor6(){
        final String hash = "12345678901234567890";

        Assertions.assertThrows(ValidationException.class, () -> new Credentials(hash));
    }

    @Test
    void testConstructor7(){
        final String hash = "12345678901234567890abc$0";

        Assertions.assertThrows(ValidationException.class, () -> new Credentials(hash));
    }

    @Test
    void testSetHash1(){
        final String hash = "";
        Credentials credentials = new Credentials("12345678901234567890abcdf");

        Assertions.assertThrows(ValidationException.class, () -> credentials.setHash(hash));
    }

    @Test
    void testSetHash2(){
        final String hash = "12345678901234567890abcdf1";
        Credentials credentials = new Credentials("12345678901234567890abcdf");

        Assertions.assertThrows(ValidationException.class, () -> credentials.setHash(hash));
    }

    @Test
    void testSetHash3(){
        final String hash = "12345678901234567890qbcdt";
        Credentials credentials = new Credentials("12345678901234567890abcdf");

        Assertions.assertThrows(ValidationException.class, () -> credentials.setHash(hash));
    }

    @Test
    void testSetHash4(){
        final String hash = null;
        Credentials credentials = new Credentials("12345678901234567890abcdf");

        Assertions.assertThrows(ValidationException.class, () -> credentials.setHash(hash));
    }

    @Test
    void testSetHash5(){
        final String hash = "12345678901234567890";
        Credentials credentials = new Credentials("12345678901234567890abcdf");

        Assertions.assertThrows(ValidationException.class, () -> credentials.setHash(hash));
    }

    @Test
    void testSetHash6(){
        final String hash = "12345678901234567890abc$0";
        Credentials credentials = new Credentials("12345678901234567890abcdf");

        Assertions.assertThrows(ValidationException.class, () -> credentials.setHash(hash));
    }

    @Test
    void testGetHash(){
        final String test = "12345678901234567890abcdf";
        Credentials credentials = new Credentials(test);

        Assertions.assertEquals(test, credentials.getHash());
    }

    @Test
    void testEqualsSimple(){
        final String test = "12345678901234567890abcdf";
        Credentials credentials = new Credentials(test);
        Credentials credentials1 = new Credentials(test);

        Assertions.assertEquals(credentials, credentials1);
    }

    @Test
    void testHashCode(){
        final String test = "12345678901234567890abcdf";
        Credentials credentials = new Credentials(test);
        Credentials credentials1 = new Credentials(test);

        Assertions.assertEquals(credentials.getHash(), credentials1.getHash());
    }
}

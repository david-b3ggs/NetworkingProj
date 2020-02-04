package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Challenge;
import tiktak.serialization.MessageOutput;
import tiktak.serialization.ValidationException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@DisplayName("Challenge Tests")
public class ChallengeTests {

    private Challenge challenge = null;

    @Test
    void testGetNonce() throws ValidationException{
        challenge.setNonce("123");

        Assertions.assertEquals("123", challenge.getNonce());
    }

    @Test
    void testSetNonceValid(){
        final String test = "123";
        Assertions.assertDoesNotThrow(() -> challenge.setNonce(test));
    }

    @Test
    void testSetNonce1(){
        final String test= "q234";
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(test));
    }

    @Test
    void testSetNonce2(){
        final String test = "23 fds";
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(test));
    }

    @Test
    void testSetNonce3(){
        final String test = null;
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(test));
    }

    @Test
    void testSetNonce4(){
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(""));
    }

    @Test
    void testSetNonce5(){
        final String test = "s?ring";
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(test));
    }

    @Test
    void testSetNonce6(){
        final String test = "-1";
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(test));
    }

    @Test
    void testSetNonce7(){
        final String test = "null";
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(test));
    }

    @Nested
    @DisplayName("EncodeTests")
    class EncodeTests {

        private final byte[] IDENC = "CLNG 123\r\n".getBytes(StandardCharsets.ISO_8859_1);

        Challenge challenge = null;

        @Test
        void testEncodeThrowsNull(){
            Assertions.assertThrows(NullPointerException.class, () ->
                    challenge.encode(new MessageOutput(null)));
        }

        @Test
        void testEncodeThrowsIO(){
            Assertions.assertThrows(IOException.class, () ->
                    challenge.encode(new MessageOutput(new FileOutputStream(""))));
        }

        @Test
        void testEncode() throws NullPointerException, IOException, ValidationException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new Challenge("123").encode(new MessageOutput(bout));
            Assertions.assertArrayEquals(IDENC, bout.toByteArray());
        }
    }


}

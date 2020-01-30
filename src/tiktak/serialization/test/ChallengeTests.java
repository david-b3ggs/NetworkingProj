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

    private Challenge challenge = new Challenge("");

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
        Assertions.assertThrows(ValidationException.class, () ->challenge.setNonce(""));
    }

    @Nested
    @DisplayName("EncodeTests")
    class EncodeTests{

        private final byte[] IDENC = "CLNG 123\r\n".getBytes(StandardCharsets.ISO_8859_1);

        Challenge challenge = new Challenge("");

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
        void testEncode() throws NullPointerException, IOException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new Challenge("123").encode(new MessageOutput(bout));
            Assertions.assertArrayEquals(IDENC, bout.toByteArray());
        }
    }


}

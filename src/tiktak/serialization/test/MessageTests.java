package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.io.*;

import tiktak.serialization.*;

@DisplayName("All Message Tests")
public class MessageTests {


    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{

        @Test
        void testMessageDecodeNullException(){
            Assertions.assertThrows(NullPointerException.class, () -> Message.decode(null));
        }

        @Test
        void testMessageDecodeValidException(){
            final String input = "notgood";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testMessageDecodeIOException(){
            Assertions.assertThrows(IOException.class, () -> Message.decode(new MessageInput(new FileInputStream("badio"))));
        }

        @Test
        void testDecodeBadMessageStart(){
            final String input = " ID %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }


        @Test
        void testDecodeReturnID() throws IOException, ValidationException{
            final String input = "ID a1\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertTrue(Message.decode(new MessageInput(in)) instanceof  ID);
        }

        @Test
        void testDecodeEmptyID(){
            final String input = "ID \r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeBadGrammer(){
            final String input = " ID a\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeNonAlphNumeric1(){
            final String input = "ID %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeNonAlphNumeric2(){
            final String input = "ID a12)\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeBadMessageStartID(){
            final String input = "iD a12)\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeReturnVersion() throws IOException, ValidationException{
            final String input = "TIKTAK 1.0\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertTrue(Message.decode(new MessageInput(in)) instanceof  Version);
        }

        @Test
        void testDecodeVersionIncorrect1(){
            final String input = "TIKTAK 1\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () ->Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeVersionIncorrect2(){
            final String input = "TIKTAK 10\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () ->Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeBadMessageStartVersion(){
            final String input = "TikTak 1.0\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeReturnChallenge() throws IOException, ValidationException{
            final String input = "CLNG 1\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertTrue(Message.decode(new MessageInput(in)) instanceof Challenge);
        }

        @Test
        void testDecodeBadServ1(){
            final String input = "CLNG %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeBadServ2(){
            final String input = "CLNG \r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        void testDecodeChallengeMessageError(){
            final String input = "NG 123\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());

            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }
    }

    @Test
    void testGetOperationValidTIK(){
        Message id = new Version();
        Assertions.assertEquals("VERSION", id.getOperation());
    }

    @Test
    void testGetOperationValidID(){
        Message id = new ID("USER");
        Assertions.assertEquals("ID", id.getOperation());
    }

    @Test
    void testGetOperationValidChallenge(){
        Message id = new Challenge("");
        Assertions.assertEquals("CHALLENGE", id.getOperation());
    }
}

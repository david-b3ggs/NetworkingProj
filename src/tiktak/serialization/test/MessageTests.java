/********************************
 *
 * Author: Bob Rein
 * Assignment: Program 1
 * Class: CSI 4321 (Networking)
 *
 *******************************/

package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import tiktak.serialization.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Message Tests")
public class MessageTests {
    @Test
    @DisplayName("Decode Null MessageInput")
    void testDecodeNull()  throws NullPointerException, IOException, ValidationException {
        assertThrows(NullPointerException.class, () ->
                Message.decode(new MessageInput(null)));
    }

    @Test
    @DisplayName("Decode Improper Format")
    void testDecodeInproperFormat(){
        String input = "badinput";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
    }

    @Test
    @DisplayName("Decode Bad File")
    void testDecodeBadFile(){
        assertThrows(IOException.class, () -> Message.decode(new MessageInput(new FileInputStream("badio"))));
    }

}

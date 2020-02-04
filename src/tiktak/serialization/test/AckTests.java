package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Ack;

@DisplayName("ACK Tests")
public class AckTests {

    private final Ack A1 = new Ack();
    private final Ack A2 = new Ack();

    @Test
    void testHashCode(){
        Assertions.assertEquals(A1.hashCode(), A2.hashCode());
    }

    @Test
    void testEqualsSimple(){
        Assertions.assertEquals(A1, A2);
    }


    @Test
    void testTwoString(){
        Assertions.assertEquals("Ack", A1.toString() );
    }
}

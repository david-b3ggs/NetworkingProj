package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Tost;

public class TostTests {

    private final Tost T1 = new Tost();
    private final Tost T2 = new Tost();

    @Test
    void testHashCode(){
        Assertions.assertEquals(T1.hashCode(), T2.hashCode());
    }

    @Test
    void testEqualsSimple(){
        Assertions.assertEquals(T1, T2);
    }

    @Test
    void testTwoString(){
        Assertions.assertEquals("TOST", T1.toString() );
    }
}

package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoxTest {

    @Test
    void stringBoxReturnsString() {
        Box<String> text = new Box<>();
        text.set("Java");

        String s = text.get();

        assertEquals("Java", s);
    }

    @Test
    void integerBoxReturnsInteger() {
        Box<Integer> number = new Box<>();
        number.set(42);

        Integer n = number.get();

        assertEquals(42, n);
    }
}

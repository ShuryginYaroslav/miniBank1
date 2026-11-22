package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoxTest {

    @Test
    void stringValueIsReadBack() {
        Box box = new Box();
        box.set("Java");

        String value = (String) box.get();

        assertEquals("Java", value);
    }

    @Test
    void integerValueFailsOnCastToString() {
        Box box = new Box();
        box.set(123);

        assertThrows(ClassCastException.class, () -> {
            String value = (String) box.get();
        });
    }
}

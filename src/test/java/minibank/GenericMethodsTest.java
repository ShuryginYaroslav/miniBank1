package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenericMethodsTest {

    @Test
    void firstReturnsFirstElement() {
        String[] names = {"Ivan", "Petr", "Oleg"};

        assertEquals("Ivan", GenericMethods.first(names));
    }

    @Test
    void lastReturnsLastElement() {
        Integer[] numbers = {1, 2, 3};

        assertEquals(3, GenericMethods.last(numbers).intValue());
    }

    @Test
    void containsReturnsTrueForExistingElement() {
        String[] names = {"Ivan", "Petr", "Oleg"};

        assertTrue(GenericMethods.contains(names, "Petr"));
    }

    @Test
    void containsReturnsFalseForMissingElement() {
        String[] names = {"Ivan", "Petr", "Oleg"};

        assertFalse(GenericMethods.contains(names, "Anna"));
    }

    @Test
    void containsUsesEquals() {
        AccountNumber[] numbers = {
                new AccountNumber("0000000001"),
                new AccountNumber("0000000002")
        };

        assertTrue(GenericMethods.contains(numbers, new AccountNumber("0000000002")));
    }

    @Test
    void firstAndLastRejectEmptyArray() {
        String[] empty = {};

        assertThrows(IllegalArgumentException.class, () -> GenericMethods.first(empty));
        assertThrows(IllegalArgumentException.class, () -> GenericMethods.last(empty));
    }
}

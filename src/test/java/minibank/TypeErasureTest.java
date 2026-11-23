package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TypeErasureTest {

    @Test
    void genericCollectionsHaveSameRuntimeClass() {
        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        assertEquals(ArrayList.class, strings.getClass());
        assertEquals(ArrayList.class, numbers.getClass());
        assertTrue(strings.getClass() == numbers.getClass());
    }
}

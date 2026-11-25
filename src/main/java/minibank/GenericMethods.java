package minibank;

import java.util.Arrays;
import java.util.Objects;

public final class GenericMethods {
    private GenericMethods() {
    }

    public static <T> T first(T[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Массив не должен быть пустым");
        }
        return values[0];
    }

    public static <T> T last(T[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Массив не должен быть пустым");
        }
        return values[values.length - 1];
    }

    public static <T> boolean contains(T[] values, T target) {
        if (values == null) {
            throw new IllegalArgumentException("Массив не должен быть null");
        }
        return Arrays.stream(values)
                .anyMatch(value -> Objects.equals(value, target));
    }
}

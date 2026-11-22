package minibank;

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
        for (T value : values) {
            if (value == null ? target == null : value.equals(target)) {
                return true;
            }
        }
        return false;
    }
}

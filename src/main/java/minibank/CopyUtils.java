package minibank;

import java.util.List;

public final class CopyUtils {
    private CopyUtils() {
    }

    public static <T> void copy(List<? extends T> source, List<? super T> target) {
        for (T element : source) {
            target.add(element);
        }
    }
}

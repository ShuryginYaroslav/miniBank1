package minibank;

public class BoxDemo {
    public static void main(String[] args) {
        Box<String> text = new Box<>();
        text.set("Java");
        String s = text.get();
        System.out.println("String value: " + s);

        Box<Integer> number = new Box<>();
        number.set(42);
        Integer n = number.get();
        System.out.println("Integer value: " + n);

        // text.set(123); // ошибка компиляции: несовместимый тип
    }
}

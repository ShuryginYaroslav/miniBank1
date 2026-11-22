package minibank;

public class BoxDemo {
    public static void main(String[] args) {
        Box stringBox = new Box();
        stringBox.set("Java");
        String value = (String) stringBox.get();
        System.out.println("String value: " + value);

        Box numberBox = new Box();
        numberBox.set(123);
        try {
            String broken = (String) numberBox.get();
            System.out.println("String value: " + broken);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }
}

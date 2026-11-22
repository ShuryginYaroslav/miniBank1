package minibank;

public class GenericMethodsDemo {
    public static void main(String[] args) {
        String[] names = {"Ivan", "Petr", "Oleg"};
        System.out.println("first: " + GenericMethods.first(names));
        System.out.println("last: " + GenericMethods.last(names));
        System.out.println("contains Petr: " + GenericMethods.contains(names, "Petr"));
        System.out.println("contains Anna: " + GenericMethods.contains(names, "Anna"));

        Integer[] numbers = {1, 2, 3};
        System.out.println("first: " + GenericMethods.first(numbers));
        System.out.println("last: " + GenericMethods.last(numbers));
        System.out.println("contains 2: " + GenericMethods.contains(numbers, 2));
    }
}

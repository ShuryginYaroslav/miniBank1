package minibank;

public class PairDemo {
    public static void main(String[] args) {
        Pair<String, Integer> age = new Pair<>("Ivan", 20);
        System.out.println(age.key() + ": " + age.value());

        AccountNumber accountNumber = new AccountNumber("0000000001");
        Pair<AccountNumber, String> owner = new Pair<>(accountNumber, "Ivan");
        System.out.println(owner.key().value() + ": " + owner.value());
    }
}

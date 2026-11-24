package minibank;

import java.util.ArrayList;
import java.util.List;

public class StreamFilterDemo {
    public static List<BankAccount> filterWithLoop(List<BankAccount> accounts) {
        List<BankAccount> result = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                result.add(account);
            }
        }
        return result;
    }

    public static List<BankAccount> filterWithStream(List<BankAccount> accounts) {
        return accounts.stream()
                .filter(a -> a.getBalance() >= 100_000)
                .toList();
    }

    public static void main(String[] args) {
        List<BankAccount> accounts = List.of(
                new DebitAccount(new AccountNumber("0000000161"), "Ivan", 50_000),
                new DebitAccount(new AccountNumber("0000000162"), "Petr", 100_000),
                new CreditAccount(new AccountNumber("0000000163"), "Maria", 200_000, 50_000)
        );

        System.out.println("loop: " + filterWithLoop(accounts));
        System.out.println("stream: " + filterWithStream(accounts));
    }
}

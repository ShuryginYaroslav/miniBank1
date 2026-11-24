package minibank;

import java.util.List;

public class ReduceDemo {
    public static double sumWithLoop(List<BankAccount> accounts) {
        double total = 0;
        for (BankAccount a : accounts) {
            total += a.getBalance();
        }
        return total;
    }

    public static double sumWithReduce(List<BankAccount> accounts) {
        return accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);
    }

    public static void main(String[] args) {
        List<BankAccount> accounts = List.of(
                new DebitAccount(new AccountNumber("0000000251"), "Ivan", 1000),
                new DebitAccount(new AccountNumber("0000000252"), "Petr", 2000),
                new CreditAccount(new AccountNumber("0000000253"), "Maria", 3000, 5000)
        );

        System.out.println("loop: " + sumWithLoop(accounts));
        System.out.println("reduce: " + sumWithReduce(accounts));
    }
}

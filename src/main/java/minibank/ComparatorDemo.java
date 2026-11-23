package minibank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorDemo {
    public static Comparator<BankAccount> byBalanceAnonymous() {
        return new Comparator<>() {
            @Override
            public int compare(BankAccount a, BankAccount b) {
                return Double.compare(a.getBalance(), b.getBalance());
            }
        };
    }

    public static Comparator<BankAccount> byBalanceLambda() {
        return (a, b) -> Double.compare(a.getBalance(), b.getBalance());
    }

    public static Comparator<BankAccount> byBalanceFactory() {
        return Comparator.comparingDouble(BankAccount::getBalance);
    }

    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("0000000041"), "Ivan", 3000));
        accounts.add(new DebitAccount(new AccountNumber("0000000042"), "Petr", 1000));
        accounts.add(new DebitAccount(new AccountNumber("0000000043"), "Maria", 2000));

        printSorted("anonymous class", accounts, byBalanceAnonymous());
        printSorted("lambda", accounts, byBalanceLambda());
        printSorted("factory method", accounts, byBalanceFactory());
    }

    private static void printSorted(String label, List<BankAccount> accounts, Comparator<BankAccount> comparator) {
        List<BankAccount> sorted = new ArrayList<>(accounts);
        sorted.sort(comparator);
        System.out.println(label + ": " + sorted);
    }
}

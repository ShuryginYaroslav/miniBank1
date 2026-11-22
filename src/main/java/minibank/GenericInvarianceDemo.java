package minibank;

import java.util.ArrayList;
import java.util.List;

public class GenericInvarianceDemo {
    public static void main(String[] args) {
        List<DebitAccount> debitAccounts = new ArrayList<>();
        debitAccounts.add(new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000));

        // List<BankAccount> accounts = debitAccounts; // не компилируется: generic-типы инвариантны

        List<? extends BankAccount> readOnlyAccounts = debitAccounts;
        BankAccount first = readOnlyAccounts.get(0);
        System.out.println("readOnly first: " + first);

        List<BankAccount> accounts = new ArrayList<>(debitAccounts);
        accounts.add(new CreditAccount(new AccountNumber("0000000002"), "Petr", 2000, 1000));
        System.out.println("debitAccounts size: " + debitAccounts.size());
        System.out.println("accounts size: " + accounts.size());
    }
}

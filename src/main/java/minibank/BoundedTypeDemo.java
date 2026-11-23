package minibank;

import java.util.ArrayList;
import java.util.List;

public class BoundedTypeDemo {
    public static void main(String[] args) {
        List<DebitAccount> values = new ArrayList<>();
        values.add(new DebitAccount(new AccountNumber("0000000031"), "Ivan", 1000));
        values.add(new DebitAccount(new AccountNumber("0000000032"), "Petr", 2500));
        values.add(new DebitAccount(new AccountNumber("0000000033"), "Maria", 700));

        DebitAccount richestDebit = AccountUtils.richest(values);
        System.out.println("richest debit: " + richestDebit);

        List<SavingsAccount> savings = new ArrayList<>();
        savings.add(new SavingsAccount(new AccountNumber("0000000034"), "Anna", 3000, 500));
        savings.add(new SavingsAccount(new AccountNumber("0000000035"), "Oleg", 1500, 500));

        SavingsAccount richestSavings = AccountUtils.richest(savings);
        System.out.println("richest savings: " + richestSavings);

        try {
            AccountUtils.richest(new ArrayList<BankAccount>());
        } catch (IllegalArgumentException e) {
            System.out.println("empty: " + e.getMessage());
        }
    }
}

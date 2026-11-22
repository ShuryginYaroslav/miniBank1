package minibank;

import java.util.ArrayList;
import java.util.List;

public class WildcardExtendsDemo {
    public static void main(String[] args) {
        List<BankAccount> all = new ArrayList<>();
        all.add(new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000));
        all.add(new CreditAccount(new AccountNumber("0000000002"), "Petr", 500, 2000));

        List<DebitAccount> debitAccounts = new ArrayList<>();
        debitAccounts.add(new DebitAccount(new AccountNumber("0000000003"), "Oleg", 700));

        List<SavingsAccount> savingsAccounts = new ArrayList<>();
        savingsAccounts.add(new SavingsAccount(new AccountNumber("0000000004"), "Anna", 2000, 500));

        List<CreditAccount> creditAccounts = new ArrayList<>();
        creditAccounts.add(new CreditAccount(new AccountNumber("0000000005"), "Maria", 300, 1000));

        System.out.println("all: " + BalanceUtils.totalBalance(all));
        System.out.println("debit: " + BalanceUtils.totalBalance(debitAccounts));
        System.out.println("savings: " + BalanceUtils.totalBalance(savingsAccounts));
        System.out.println("credit: " + BalanceUtils.totalBalance(creditAccounts));

        // accounts.add(...) запрещено: реальный тип может быть List<SavingsAccount>.
    }
}

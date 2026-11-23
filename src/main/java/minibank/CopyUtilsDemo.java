package minibank;

import java.util.ArrayList;
import java.util.List;

public class CopyUtilsDemo {
    public static void main(String[] args) {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount(new AccountNumber("0000000021"), "Ivan", 1000));
        source.add(new DebitAccount(new AccountNumber("0000000022"), "Petr", 2000));

        List<BankAccount> target = new ArrayList<>();
        CopyUtils.copy(source, target);

        List<Object> objects = new ArrayList<>();
        CopyUtils.copy(source, objects);

        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(new CreditAccount(new AccountNumber("0000000023"), "Maria", 500, 1000));
        bankAccounts.add(new DebitAccount(new AccountNumber("0000000024"), "Oleg", 700));

        List<BankAccount> copiedBankAccounts = new ArrayList<>();
        CopyUtils.copy(bankAccounts, copiedBankAccounts);

        System.out.println("source: " + source);
        System.out.println("target: " + target);
        System.out.println("objects: " + objects);
        System.out.println("copiedBankAccounts: " + copiedBankAccounts);
    }
}

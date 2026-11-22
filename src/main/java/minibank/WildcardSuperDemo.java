package minibank;

import java.util.ArrayList;
import java.util.List;

public class WildcardSuperDemo {
    public static void main(String[] args) {
        List<DebitAccount> debits = new ArrayList<>();
        List<BankAccount> accounts = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        ConsumerUtils.addDemoDebitAccounts(debits);
        ConsumerUtils.addDemoDebitAccounts(accounts);
        ConsumerUtils.addDemoDebitAccounts(objects);

        System.out.println("debits: " + debits.size());
        System.out.println("accounts: " + accounts.size());
        System.out.println("objects: " + objects.size());

        Object value = objects.get(0);
        System.out.println("value: " + value);
    }
}

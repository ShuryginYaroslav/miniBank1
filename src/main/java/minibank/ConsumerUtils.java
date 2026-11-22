package minibank;

import java.util.List;

public final class ConsumerUtils {
    private ConsumerUtils() {
    }

    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount(new AccountNumber("0000000011"), "Ivan", 1000));
        target.add(new DebitAccount(new AccountNumber("0000000012"), "Petr", 2000));

        Object value = target.get(0);
        // BankAccount account = target.get(0); // не компилируется: гарантируется только Object
    }
}

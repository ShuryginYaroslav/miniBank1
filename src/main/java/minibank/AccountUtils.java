package minibank;

import java.util.List;

public final class AccountUtils {
    private AccountUtils() {
    }

    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("Список счетов пуст");
        }

        T richest = accounts.get(0);
        for (T account : accounts) {
            if (account.getBalance() > richest.getBalance()) {
                richest = account;
            }
        }
        return richest;
    }
}

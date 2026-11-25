package minibank;

import java.util.Comparator;
import java.util.List;

public final class AccountUtils {
    private AccountUtils() {
    }

    public static <T extends BankAccount> T richest(List<T> accounts) {
        return accounts.stream()
                .max(Comparator.comparingDouble(BankAccount::getBalance))
                .orElseThrow(() -> new IllegalArgumentException("Список счетов пуст"));
    }
}

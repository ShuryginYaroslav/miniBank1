package minibank;

import java.util.List;

public final class BalanceUtils {
    private BalanceUtils() {
    }

    public static double totalBalance(List<? extends BankAccount> accounts) {
        return accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }
}

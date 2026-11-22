package minibank;

import java.util.List;

public final class BalanceUtils {
    private BalanceUtils() {
    }

    public static double totalBalance(List<? extends BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
}

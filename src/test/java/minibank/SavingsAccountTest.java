package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void canWithdrawIfMinimumBalanceIsKept() {
        SavingsAccount account = new SavingsAccount(new AccountNumber("0000000002"),"Petr", 1000, 500);

        account.withdraw(400);

        assertEquals(600, account.getBalance());
    }

    @Test
    void cannotDropBalanceBelowMinimum() {
        SavingsAccount account = new SavingsAccount(new AccountNumber("0000000002"), "Petr", 1000, 500);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(600),
                "Нельзя опустить баланс ниже минимума");
        assertEquals(1000, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        SavingsAccount account = new SavingsAccount(new AccountNumber("0000000002"), "Petr", 1000, 500);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(2000));
        assertEquals(1000, account.getBalance(), "При неуспешном снятии баланс не меняется");
    }
}
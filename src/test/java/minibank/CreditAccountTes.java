package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreditAccountTest {

    @Test
    void accountCanGoNegative() {
        CreditAccount account = new CreditAccount(new AccountNumber("0000000003"), "Oleg", 1000, 5000);

        account.withdraw(2000);

        assertEquals(-1000, account.getBalance(), "Баланс может быть отрицательным");
    }

    @Test
    void canUseCreditLimit() {
        CreditAccount account = new CreditAccount(new AccountNumber("0000000003"), "Oleg", 1000, 5000);

        account.withdraw(5000);

        assertEquals(-4000, account.getBalance());
    }

    @Test
    void cannotExceedCreditLimit() {
        CreditAccount account = new CreditAccount(new AccountNumber("0000000003"), "Oleg", 1000, 5000);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(7000),
                "Нельзя превысить кредитный лимит");
        assertEquals(1000, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        CreditAccount account = new CreditAccount(new AccountNumber("0000000003"), "Oleg", 1000, 5000);
        account.withdraw(5000);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(2000));
        assertEquals(-4000, account.getBalance(), "При неуспешном снятии баланс не меняется");
    }
}
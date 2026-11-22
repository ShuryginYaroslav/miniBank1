package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DebitAccountTest {

    @Test
    void initialBalanceIsStored() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        assertEquals(1000, account.getBalance(), "Начальный баланс должен сохраняться");
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        account.deposit(500);

        assertEquals(1500, account.getBalance());
    }

    @Test
    void zeroDepositThrowsException() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        assertThrows(IllegalArgumentException.class, () -> account.deposit(0), "Пополнение на вызывает исключение");
    }

    @Test
    void negativeDepositThrowsException() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100), "Пополнение на отрицательную сумму вызывает исключение");
    }

    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        boolean result = account.withdraw(300);

        assertTrue(result);
        assertEquals(700, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        boolean result = account.withdraw(1500);

        assertFalse(result, "Снятие сверх баланса запрещено");
        assertEquals(1000, account.getBalance(), "Баланс не должен измениться");
    }

    @Test
    void zeroWithdrawIsForbidden() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        boolean result = account.withdraw(0);

        assertFalse(result, "Снятие нуля запрещено");
        assertEquals(1000, account.getBalance());
    }

    @Test
    void negativeWithdrawIsForbidden() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);

        boolean result = account.withdraw(-100);

        assertFalse(result, "Снятие отрицательной суммы запрещено");
        assertEquals(1000, account.getBalance());
    }
    @Test
    void toStringReturnsReadableRepresentation() {
        DebitAccount account = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 10000);

        String result = account.toString();

        assertEquals("DebitAccount{number='0000000001', owner='Ivan', balance=10000.0}", result,
                "toString() должен возвращать читаемую строку с именем класса и полями");
    }
}
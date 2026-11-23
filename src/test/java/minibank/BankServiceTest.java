package minibank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {
    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final TransferService transferService = new TransferService(
            new NoCommission(), message -> {}, new BlockedAccountRegistry(), new TransactionJournal());
    private final BankService service = new BankService(accounts, transferService);

    @Test
    void openedAccountCanBeFound() {
        AccountNumber number = new AccountNumber("0000000100");
        BankAccount account = new DebitAccount(number, "Ivan", 1000);

        service.openAccount(account);

        assertSame(account, service.findAccount(number));
        assertEquals(1, accounts.size());
    }

    @Test
    void openingAccountWithSameIdReplacesExistingValue() {
        AccountNumber number = new AccountNumber("0000000100");
        BankAccount first = new DebitAccount(number, "Ivan", 1000);
        BankAccount second = new DebitAccount(number, "Petr", 5000);

        service.openAccount(first);
        service.openAccount(second);

        assertSame(second, service.findAccount(number));
        assertEquals(1, accounts.size());
    }

    @Test
    void openAccountRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.openAccount(null));
        assertEquals(0, accounts.size());
    }

    @Test
    void closedAccountIsNoLongerFound() {
        AccountNumber number = new AccountNumber("0000000100");
        service.openAccount(new DebitAccount(number, "Ivan", 1000));

        assertTrue(service.closeAccount(number));

        assertNull(service.findAccount(number));
        assertFalse(accounts.existsById(number));
        assertEquals(0, accounts.size());
    }

    @Test
    void closingMissingAccountReturnsFalse() {
        assertFalse(service.closeAccount(new AccountNumber("0000000200")));
    }

    @Test
    void findMissingAccountReturnsNull() {
        assertNull(service.findAccount(new AccountNumber("0000000200")));
    }
}

package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {
    private static final AccountNumber ACC_FROM = new AccountNumber("1111111111");
    private static final AccountNumber ACC_TO = new AccountNumber("2222222222");

    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final TransactionJournal journal = new TransactionJournal();
    private final FakeNotificationService notificationService = new FakeNotificationService();
    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final TransferService transferService = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, journal);
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

    @Test
    void transferByNumbersChangesBothBalances() {
        service.openAccount(new DebitAccount(ACC_FROM, "Ivan", 10000));
        service.openAccount(new DebitAccount(ACC_TO, "Petr", 2000));

        service.transfer(ACC_FROM, ACC_TO, 3000);

        assertEquals(7000, service.findAccount(ACC_FROM).getBalance());
        assertEquals(5000, service.findAccount(ACC_TO).getBalance());
        assertEquals(1, journal.size());
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void transferWithMissingSenderThrowsAndChangesNothing() {
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2000);
        service.openAccount(to);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Счёт отправителя не найден", ex.getMessage());
        assertEquals(2000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferWithMissingRecipientThrowsAndChangesNothing() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 2000);
        service.openAccount(from);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Счёт получателя не найден", ex.getMessage());
        assertEquals(2000, from.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferFromBlockedSenderThrowsAndChangesNothing() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 2000);
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2000);
        service.openAccount(from);
        service.openAccount(to);
        blockedAccounts.block(ACC_FROM);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Source account is blocked", ex.getMessage());
        assertEquals(2000, from.getBalance());
        assertEquals(2000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferWithInsufficientFundsThrowsAndChangesNothing() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 1000);
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2000);
        service.openAccount(from);
        service.openAccount(to);

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 3000));

        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void findAccountsFiltersByPositiveBalance() {
        BankAccount positive = new DebitAccount(new AccountNumber("0000000111"), "Ivan", 1000);
        BankAccount empty = new DebitAccount(new AccountNumber("0000000112"), "Petr", 0);
        service.openAccount(positive);
        service.openAccount(empty);

        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;
        List<BankAccount> result = service.findAccounts(positiveBalance);

        assertEquals(1, result.size());
        assertTrue(result.contains(positive));
        assertFalse(result.contains(empty));
    }

    @Test
    void findAccountsFiltersRichAccounts() {
        BankAccount rich = new DebitAccount(new AccountNumber("0000000113"), "Ivan", 100000);
        BankAccount poor = new DebitAccount(new AccountNumber("0000000114"), "Petr", 99999);
        service.openAccount(rich);
        service.openAccount(poor);

        Predicate<BankAccount> richPredicate = account -> account.getBalance() >= 100_000;
        List<BankAccount> result = service.findAccounts(richPredicate);

        assertEquals(List.of(rich), result);
    }

    @Test
    void findAccountsFiltersDebitAccountsOnly() {
        BankAccount debit = new DebitAccount(new AccountNumber("0000000115"), "Ivan", 1000);
        BankAccount credit = new CreditAccount(new AccountNumber("0000000116"), "Petr", 1000, 5000);
        service.openAccount(debit);
        service.openAccount(credit);

        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;
        List<BankAccount> result = service.findAccounts(debitOnly);

        assertEquals(List.of(debit), result);
    }

    @Test
    void findAccountsReturnsNewListAndDoesNotExposeStorage() {
        BankAccount account = new DebitAccount(new AccountNumber("0000000117"), "Ivan", 1000);
        service.openAccount(account);

        List<BankAccount> result = service.findAccounts(value -> true);
        result.clear();

        assertSame(account, service.findAccount(new AccountNumber("0000000117")));
        assertEquals(1, service.findAccounts(value -> true).size());
        assertNotSame(service.findAccounts(value -> true), service.findAccounts(value -> true));
    }
}

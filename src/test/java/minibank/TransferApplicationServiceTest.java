package minibank;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TransferApplicationServiceTest {
    private static final AccountNumber ACC_FROM = new AccountNumber("1111111111");
    private static final AccountNumber ACC_TO = new AccountNumber("2222222222");
    private static final AccountNumber ACC_MISSING = new AccountNumber("3333333333");

    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final TransactionJournal journal = new TransactionJournal();
    private final FakeNotificationService notificationService = new FakeNotificationService();
    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final TransferService transferService = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, journal);
    private final AccountService accountService = new AccountService(accounts);
    private final TransferApplicationService service =
            new TransferApplicationService(accounts, transferService);

    @Test
    void transferChangesBothBalancesAndKeepsSideEffects() {
        accountService.open(new DebitAccount(ACC_FROM, "Ivan", 10_000));
        accountService.open(new DebitAccount(ACC_TO, "Petr", 2_000));

        service.transfer(ACC_FROM, ACC_TO, 3_000);

        assertEquals(7_000, accountService.find(ACC_FROM).orElseThrow().getBalance());
        assertEquals(5_000, accountService.find(ACC_TO).orElseThrow().getBalance());
        assertEquals(1, journal.size());
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void transferRejectsMissingAccountsWithExistingMessages() {
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2_000);
        accountService.open(to);

        IllegalArgumentException senderError = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Счёт отправителя не найден", senderError.getMessage());
        assertEquals(2_000, to.getBalance());

        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 2_000);
        accountService.open(from);

        IllegalArgumentException recipientError = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC_FROM, ACC_MISSING, 500));

        assertEquals("Счёт получателя не найден", recipientError.getMessage());
        assertEquals(2_000, from.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferRejectsBlockedSenderAndInsufficientFunds() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 1_000);
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2_000);
        accountService.open(from);
        accountService.open(to);
        blockedAccounts.block(ACC_FROM);

        IllegalStateException blockedError = assertThrows(IllegalStateException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Source account is blocked", blockedError.getMessage());
        assertEquals(1_000, from.getBalance());
        assertEquals(2_000, to.getBalance());

        blockedAccounts.unblock(ACC_FROM);

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 3_000));

        assertEquals(1_000, from.getBalance());
        assertEquals(2_000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void doesNotExposeReportOperations() {
        Set<String> publicMethods = Arrays.stream(TransferApplicationService.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .map(Method::getName)
                .collect(Collectors.toSet());

        assertEquals(Set.of("transfer"), publicMethods);
    }
}

package minibank;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {
    private static final AccountNumber ACC_A = new AccountNumber("0000000100");
    private static final AccountNumber ACC_B = new AccountNumber("0000000200");

    private final CommissionPolicy noCommission = new NoCommission();
    private final NotificationService silentNotification = message -> {};


    @Test
    void successfulTransferChangesBothBalances() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 2000);
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 3000);

        assertEquals(7000, from.getBalance(), "Баланс отправителя должен уменьшиться");
        assertEquals(5000, to.getBalance(), "Баланс получателя должен увеличиться");
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 2000);
        TransferService service = new TransferService(noCommission, silentNotification);

        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class,
                () -> service.transfer(from, to, 5000));
        assertEquals("Недостаточно средств", ex.getMessage());
        assertEquals(1000, from.getBalance(), "Баланс отправителя не должен измениться");
        assertEquals(2000, to.getBalance(), "Баланс получателя не должен измениться");
    }

    @Test
    void negativeTransferThrowsException() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransferService service = new TransferService(noCommission, silentNotification);

        InvalidAmountException ex = assertThrows(InvalidAmountException.class,
                () -> service.transfer(from, to, -500));
        assertEquals("Сумма перевода должна быть положительной", ex.getMessage());
        assertEquals(1000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void zeroTransferThrowsException() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransferService service = new TransferService(noCommission, silentNotification);

        InvalidAmountException ex = assertThrows(InvalidAmountException.class,
                () -> service.transfer(from, to, 0));
        assertEquals("Сумма перевода должна быть положительной", ex.getMessage());
        assertEquals(1000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void transferToSameAccountThrowsException() {
        DebitAccount account = new DebitAccount(ACC_A, "Ivan", 10000);
        TransferService service = new TransferService(noCommission, silentNotification);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(account, account, 1000));
        assertEquals("Нельзя перевести на тот же счёт", ex.getMessage());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void transferOverLimitThrowsException() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 100000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(noCommission, notificationService);

        TransferLimitExceededException ex = assertThrows(TransferLimitExceededException.class,
                () -> service.transfer(from, to, 50001));
        assertEquals("Превышен лимит перевода", ex.getMessage());
        assertEquals(100000, from.getBalance(), "Баланс отправителя не должен измениться");
        assertEquals(0, to.getBalance(), "Получатель не должен получить деньги");
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 2000);
        TransferService service = new TransferService(noCommission, silentNotification);

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(from, to, 5000));
        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferAtLimitIsAllowed() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 60000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 50000);

        assertEquals(10000, from.getBalance());
        assertEquals(50000, to.getBalance());
    }

    @Test
    void commissionIsChargedFromSender() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 20000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        TransferService service = new TransferService(new PercentCommission(0.01), silentNotification);

        service.transfer(from, to, 10000);

        assertEquals(9900, from.getBalance(), "С отправителя списана сумма перевода + комиссия");
    }

    @Test
    void recipientReceivesExactlyTransferAmount() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 20000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        TransferService service = new TransferService(new PercentCommission(0.01), silentNotification);

        service.transfer(from, to, 10000);

        assertEquals(10000, to.getBalance(), "Получатель получает ровно сумму перевода, без вычета комиссии");
    }

    @Test
    void transferFailsIfCannotCoverCommission() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 10050);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        TransferService service = new TransferService(new PercentCommission(0.01), silentNotification);

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 10000),
                "Перевод должен быть отклонён: не хватает на комиссию");
        assertEquals(10050, from.getBalance(), "Баланс отправителя не должен измениться");
        assertEquals(0, to.getBalance(), "Баланс получателя не должен измениться");
    }

    @Test
    void transferFromDebitToDebit() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 2000);

        assertEquals(3000, from.getBalance());
        assertEquals(3000, to.getBalance());
    }

    @Test
    void transferFromDebitToSavings() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        SavingsAccount to = new SavingsAccount(ACC_B, "Petr", 1000, 500);
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 2000);

        assertEquals(3000, from.getBalance());
        assertEquals(3000, to.getBalance());
    }

    @Test
    void transferFromCreditToDebit() {
        CreditAccount from = new CreditAccount(ACC_A, "Ivan", 1000, 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 3000);

        assertEquals(-2000, from.getBalance(), "Кредитный счёт ушёл в минус");
        assertEquals(4000, to.getBalance());
    }

    @Test
    void transferFromSavingsToDebit() {
        SavingsAccount from = new SavingsAccount(ACC_A, "Ivan", 5000, 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 3000);

        assertEquals(2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void successfulTransferSendsExactlyOneNotification() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        assertEquals(1, notificationService.getNotificationCount(),
                "После успешного перевода должно быть ровно одно уведомление");
    }

    @Test
    void failedTransferDoesNotSendNotification() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 3000));
        assertEquals(0, notificationService.getNotificationCount(),
                "При неуспешном переводе уведомление не отправляется");
    }

    @Test
    void notificationMessageContainsTransferAmount() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        String expectedMessage = "Трансфер 3000.0 завершен";
        assertEquals(expectedMessage, notificationService.getLastMessage(),
                "Текст уведомления должен соответствовать ожидаемому");
    }

    @Test
    void transferFromBlockedAccountThrowsException() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        blockedAccounts.block(ACC_A);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(noCommission, notificationService, blockedAccounts);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.transfer(from, to, 2000));

        assertEquals("Source account is blocked", ex.getMessage());
        assertEquals(5000, from.getBalance());
        assertEquals(1000, to.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferFromUnblockedAccountIsAllowedWithRegistry() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        blockedAccounts.block(new AccountNumber("0000000300"));
        TransferService service = new TransferService(noCommission, silentNotification, blockedAccounts);

        service.transfer(from, to, 2000);

        assertEquals(3000, from.getBalance());
        assertEquals(3000, to.getBalance());
    }

    @Test
    void successfulTransferIsWrittenToJournal() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
                noCommission, silentNotification, new BlockedAccountRegistry(), journal);

        service.transfer(from, to, 2000);

        assertEquals(1, journal.size());
        Transaction written = journal.findAll().get(0);
        assertEquals(TransactionType.TRANSFER, written.type());
        assertEquals(ACC_A, written.account());
        assertEquals(2000, written.amount());
        assertEquals(TransactionStatus.SUCCESS, written.status());
    }

    @Test
    void failedTransferIsNotWrittenToJournal() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 1000);
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
                noCommission, silentNotification, new BlockedAccountRegistry(), journal);

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 2000));

        assertEquals(0, journal.size());
    }

    @Test
    void transferWithoutCommissionLambda() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        CommissionPolicy noCommission = amount -> 0.0;
        TransferService service = new TransferService(noCommission, silentNotification);

        service.transfer(from, to, 1000);

        assertEquals(4000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void transferWithOnePercentCommissionLambda() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        CommissionPolicy onePercent = amount -> amount * 0.01;
        TransferService service = new TransferService(onePercent, silentNotification);

        service.transfer(from, to, 1000);

        assertEquals(3990, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void notificationThroughLambda() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        List<String> messages = new ArrayList<>();
        NotificationService notification = message -> messages.add(message);
        TransferService service = new TransferService(amount -> 0.0, notification);

        service.transfer(from, to, 1000);

        assertEquals(List.of("Трансфер 1000.0 завершен"), messages);
    }

    @Test
    void notificationThroughMethodReference() {
        DebitAccount from = new DebitAccount(ACC_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(ACC_B, "Petr", 0);
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut = new PrintStream(output)) {
            System.setOut(capturedOut);
            NotificationService console = System.out::println;
            TransferService service = new TransferService(amount -> 0.0, console);

            service.transfer(from, to, 1000);

            assertEquals("Трансфер 1000.0 завершен", output.toString().trim());
        } finally {
            System.setOut(originalOut);
        }
    }
}

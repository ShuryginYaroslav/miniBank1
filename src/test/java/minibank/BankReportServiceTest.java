package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankReportServiceTest {
    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final TransferService transferService =
            new TransferService(new NoCommission(), new FakeNotificationService());
    private final BankReportService service = new BankReportService(accounts, transferService);

    @Test
    void reportsWorkSeparatelyFromApplicationServices() {
        BankAccount rich = new DebitAccount(new AccountNumber("0000000401"), "Ivan", 10_000);
        BankAccount poor = new DebitAccount(new AccountNumber("0000000402"), "Petr", 500);
        accounts.save(rich);
        accounts.save(poor);

        assertEquals(List.of(rich), service.findRichAccounts(10_000));
        assertEquals(10_500, service.totalBalance());
        assertSame(rich, service.richestAccount().orElseThrow());
    }
}

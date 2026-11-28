package minibank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final AccountService service = new AccountService(accounts);

    @Test
    void worksWithoutTransferService() {
        AccountNumber number = new AccountNumber("0000000301");
        BankAccount account = new DebitAccount(number, "Ivan", 1000);

        service.open(account);

        assertSame(account, service.find(number).orElseThrow());
        assertTrue(service.close(number));
        assertTrue(service.find(number).isEmpty());
        assertEquals(0, accounts.size());
    }

    @Test
    void rejectsNullAccountAndMissingClose() {
        assertThrows(IllegalArgumentException.class, () -> service.open(null));
        assertFalse(service.close(new AccountNumber("0000000302")));
        assertEquals(0, accounts.size());
    }
}

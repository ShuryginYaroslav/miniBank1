package minibank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void repositoryWorksWithAccountNumberAndBankAccount() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber number = new AccountNumber("0000000001");
        BankAccount account = new DebitAccount(number, "Ivan", 1000);

        repository.save(account);

        assertEquals(1, repository.size());
        assertTrue(repository.existsById(number));
        assertEquals(account, repository.findById(number));
    }

    @Test
    void repositoryWorksWithLongAndTransaction() {
        Repository<Long, Transaction> repository = new InMemoryRepository<>();
        Transaction transaction = new Transaction(
                1L,
                TransactionType.DEPOSIT,
                new AccountNumber("0000000001"),
                5000,
                TransactionStatus.SUCCESS
        );

        repository.save(transaction);

        assertEquals(1, repository.size());
        assertTrue(repository.existsById(1L));
        assertEquals(transaction, repository.findById(1L));
    }

    @Test
    void saveRejectsNull() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
        assertEquals(0, repository.size());
    }

    @Test
    void saveReplacesEntityWithSameId() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber number = new AccountNumber("0000000001");
        BankAccount first = new DebitAccount(number, "Ivan", 1000);
        BankAccount second = new DebitAccount(number, "Petr", 2000);

        repository.save(first);
        repository.save(second);

        assertEquals(1, repository.size());
        assertSame(second, repository.findById(number));
    }

    @Test
    void findByIdReturnsNullForMissingId() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber missing = new AccountNumber("0000000009");

        assertNull(repository.findById(missing));
        assertFalse(repository.existsById(missing));
    }
}

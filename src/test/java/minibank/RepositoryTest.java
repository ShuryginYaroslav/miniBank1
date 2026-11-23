package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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
    void savingSameIdReplacesOldValue() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber id = new AccountNumber("0000000001");
        BankAccount oldAccount = new DebitAccount(id, "Ivan", 1000);
        BankAccount newAccount = new DebitAccount(id, "Petr", 5000);
        repository.save(oldAccount);
        repository.save(newAccount);

        assertEquals(1, repository.size());
        assertSame(newAccount, repository.findById(id));
    }

    @Test
    void findByIdReturnsNullForMissingId() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber missing = new AccountNumber("0000000009");

        assertNull(repository.findById(missing));
        assertFalse(repository.existsById(missing));
    }

    @Test
    void sizeIncreasesAfterSavingNewIds() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        repository.save(new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000));
        assertEquals(1, repository.size());

        repository.save(new DebitAccount(new AccountNumber("0000000002"), "Petr", 2000));
        assertEquals(2, repository.size());
    }

    @Test
    void deleteByIdRemovesExistingObject() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber id = new AccountNumber("0000000001");
        BankAccount account = new DebitAccount(id, "Ivan", 1000);
        repository.save(account);

        assertTrue(repository.deleteById(id));

        assertNull(repository.findById(id));
        assertFalse(repository.existsById(id));
        assertEquals(0, repository.size());
    }

    @Test
    void deleteByIdReturnsFalseForMissingId() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        assertFalse(repository.deleteById(new AccountNumber("0000000009")));
    }

    @Test
    void findAllReturnsAllSavedValues() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankAccount first = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000);
        BankAccount second = new DebitAccount(new AccountNumber("0000000002"), "Petr", 2000);
        BankAccount third = new CreditAccount(new AccountNumber("0000000003"), "Maria", 3000, 1000);
        repository.save(first);
        repository.save(second);
        repository.save(third);

        List<BankAccount> all = repository.findAll();

        assertEquals(3, all.size());
        assertEquals(Set.of(first, second, third), Set.copyOf(all));
    }
}

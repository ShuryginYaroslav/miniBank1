package minibank;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private static class InMemoryRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {
        private final Map<ID, T> storage = new HashMap<>();

        @Override
        public void save(T value) {
            storage.put(value.getId(), value);
        }

        @Override
        public T findById(ID id) {
            return storage.get(id);
        }

        @Override
        public boolean existsById(ID id) {
            return storage.containsKey(id);
        }

        @Override
        public int size() {
            return storage.size();
        }
    }

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
}

package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionIndexTest {

    private Transaction transactionFor(AccountNumber number) {
        return new Transaction(1L, TransactionType.TRANSFER, number, 100, TransactionStatus.SUCCESS);
    }

    @Test
    void transactionCanBeFoundByEqualAccountNumber() {
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");
        assertNotSame(stored, lookup);
        assertEquals(stored, lookup);
        TransactionIndex index = new TransactionIndex();

        index.add(transactionFor(stored));

        assertEquals(1, index.findByAccount(lookup).size());
    }

    @Test
    void transactionsAreReturnedInInsertionOrderPerAccount() {
        AccountNumber a = new AccountNumber("1111111111");
        AccountNumber b = new AccountNumber("2222222222");
        TransactionIndex index = new TransactionIndex();
        Transaction first = transactionFor(a);
        Transaction second = new Transaction(2L, TransactionType.TRANSFER, b, 200, TransactionStatus.SUCCESS);
        Transaction third = new Transaction(3L, TransactionType.TRANSFER, a, 300, TransactionStatus.SUCCESS);
        index.add(first);
        index.add(second);
        index.add(third);

        assertEquals(List.of(first, third), index.findByAccount(a));
        assertEquals(List.of(second), index.findByAccount(b));
    }

    @Test
    void unknownAccountReturnsEmptyList() {
        TransactionIndex index = new TransactionIndex();

        assertTrue(index.findByAccount(new AccountNumber("1111111111")).isEmpty());
    }

    @Test
    void returnedListIsIndependentCopy() {
        AccountNumber number = new AccountNumber("1111111111");
        TransactionIndex index = new TransactionIndex();
        index.add(transactionFor(number));

        List<Transaction> found = index.findByAccount(number);
        found.clear();

        assertEquals(1, index.findByAccount(number).size());
    }

    @Test
    void removeDeletesTransactionFromIndex() {
        AccountNumber number = new AccountNumber("1111111111");
        TransactionIndex index = new TransactionIndex();
        Transaction transaction = transactionFor(number);
        index.add(transaction);

        index.remove(transaction);

        assertTrue(index.findByAccount(number).isEmpty());
    }
}

package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionJournalTest {
    private static final AccountNumber ACC_A = new AccountNumber("0000000100");
    private static final AccountNumber ACC_B = new AccountNumber("0000000200");

    @Test
    void transactionsAreStoredInInsertionOrder() {
        TransactionJournal journal = new TransactionJournal();
        Transaction first = new Transaction(1L, TransactionType.DEPOSIT, ACC_A, 100, TransactionStatus.SUCCESS);
        Transaction second = new Transaction(2L, TransactionType.TRANSFER, ACC_B, 200, TransactionStatus.SUCCESS);

        journal.add(first);
        journal.add(second);

        assertEquals(List.of(first, second), journal.findAll());
    }

    @Test
    void equalTransactionsCanBeAddedTwice() {
        TransactionJournal journal = new TransactionJournal();
        Transaction transaction = new Transaction(1L, TransactionType.TRANSFER, ACC_A, 100, TransactionStatus.SUCCESS);

        journal.add(transaction);
        journal.add(transaction);

        assertEquals(2, journal.size());
        assertEquals(List.of(transaction, transaction), journal.findAll());
    }

    @Test
    void sizeMatchesNumberOfEntries() {
        TransactionJournal journal = new TransactionJournal();

        assertEquals(0, journal.size());

        journal.add(new Transaction(1L, TransactionType.DEPOSIT, ACC_A, 100, TransactionStatus.SUCCESS));
        journal.add(new Transaction(2L, TransactionType.WITHDRAWAL, ACC_B, 50, TransactionStatus.SUCCESS));

        assertEquals(2, journal.size());
    }

    @Test
    void returnedListDoesNotExposeInternalState() {
        TransactionJournal journal = new TransactionJournal();
        Transaction transaction = new Transaction(1L, TransactionType.TRANSFER, ACC_A, 100, TransactionStatus.SUCCESS);
        journal.add(transaction);

        List<Transaction> copy = journal.findAll();
        copy.clear();

        assertEquals(1, journal.size());
        assertEquals(List.of(transaction), journal.findAll());
    }
}

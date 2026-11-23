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

    @Test
    void removeRejectedDeletesOnlyRejectedTransactions() {
        TransactionJournal journal = new TransactionJournal();
        Transaction success1 = new Transaction(1L, TransactionType.DEPOSIT, ACC_A, 100, TransactionStatus.SUCCESS);
        Transaction rejected1 = new Transaction(2L, TransactionType.TRANSFER, ACC_B, 200, TransactionStatus.REJECTED);
        Transaction success2 = new Transaction(3L, TransactionType.WITHDRAWAL, ACC_A, 50, TransactionStatus.SUCCESS);
        Transaction rejected2 = new Transaction(4L, TransactionType.TRANSFER, ACC_B, 70, TransactionStatus.REJECTED);
        journal.add(success1);
        journal.add(rejected1);
        journal.add(success2);
        journal.add(rejected2);

        journal.removeRejected();

        assertEquals(2, journal.size());
        assertEquals(List.of(success1, success2), journal.findAll());
    }

    @Test
    void removeRejectedDoesNothingWhenNoRejectedTransactions() {
        TransactionJournal journal = new TransactionJournal();
        Transaction success = new Transaction(1L, TransactionType.DEPOSIT, ACC_A, 100, TransactionStatus.SUCCESS);
        journal.add(success);

        journal.removeRejected();

        assertEquals(1, journal.size());
        assertEquals(List.of(success), journal.findAll());
    }

    @Test
    void removeRejectedCanEmptyJournal() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(new Transaction(1L, TransactionType.TRANSFER, ACC_A, 100, TransactionStatus.REJECTED));
        journal.add(new Transaction(2L, TransactionType.TRANSFER, ACC_B, 200, TransactionStatus.REJECTED));

        journal.removeRejected();

        assertEquals(0, journal.size());
        assertTrue(journal.findAll().isEmpty());
    }
}

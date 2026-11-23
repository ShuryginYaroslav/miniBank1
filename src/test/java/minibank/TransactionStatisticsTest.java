package minibank;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatisticsTest {
    private static final AccountNumber ACC_A = new AccountNumber("1111111111");
    private static final AccountNumber ACC_B = new AccountNumber("2222222222");

    private final TransactionStatistics statistics = new TransactionStatistics();

    private Transaction transactionFor(AccountNumber number, double amount) {
        return new Transaction(1L, TransactionType.TRANSFER, number, amount, TransactionStatus.SUCCESS);
    }

    @Test
    void emptyJournalProducesEmptyMaps() {
        TransactionJournal journal = new TransactionJournal();

        assertTrue(statistics.countByAccount(journal.findAll()).isEmpty());
        assertTrue(statistics.totalAmountByAccount(journal.findAll()).isEmpty());
    }

    @Test
    void oneAccountProducesOneEntry() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(transactionFor(ACC_A, 100));

        assertEquals(Map.of(ACC_A, 1), statistics.countByAccount(journal.findAll()));
        assertEquals(Map.of(ACC_A, 100.0), statistics.totalAmountByAccount(journal.findAll()));
    }

    @Test
    void severalAccountsProduceSeparateEntries() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(transactionFor(ACC_A, 100));
        journal.add(transactionFor(ACC_B, 200));

        assertEquals(Map.of(ACC_A, 1, ACC_B, 1), statistics.countByAccount(journal.findAll()));
        assertEquals(Map.of(ACC_A, 100.0, ACC_B, 200.0), statistics.totalAmountByAccount(journal.findAll()));
    }

    @Test
    void severalOperationsOfOneAccountAreAggregated() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(transactionFor(ACC_A, 100));
        journal.add(transactionFor(ACC_A, 200));
        journal.add(transactionFor(ACC_A, 300));

        assertEquals(Map.of(ACC_A, 3), statistics.countByAccount(journal.findAll()));
        assertEquals(Map.of(ACC_A, 600.0), statistics.totalAmountByAccount(journal.findAll()));
    }

    @Test
    void equalAccountNumberValuesAreAggregatedIntoOneEntry() {
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");
        assertNotSame(stored, lookup);
        TransactionJournal journal = new TransactionJournal();
        journal.add(transactionFor(stored, 100));
        journal.add(transactionFor(lookup, 200));

        Map<AccountNumber, Integer> counts = statistics.countByAccount(journal.findAll());
        Map<AccountNumber, Double> totals = statistics.totalAmountByAccount(journal.findAll());

        assertEquals(1, counts.size());
        assertEquals(1, totals.size());
        assertEquals(2, counts.get(ACC_A));
        assertEquals(300.0, totals.get(ACC_A));
    }
}

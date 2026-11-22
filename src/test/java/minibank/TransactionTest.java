package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void transactionStoresAllFields() {
        AccountNumber acc = new AccountNumber("1234567890");
        Transaction tx = new Transaction(1L, TransactionType.DEPOSIT, acc, 5000, TransactionStatus.SUCCESS);

        assertEquals(1L, tx.getId());
        assertEquals(TransactionType.DEPOSIT, tx.type());
        assertEquals(acc, tx.account());
        assertEquals(5000, tx.amount());
        assertEquals(TransactionStatus.SUCCESS, tx.status());
    }

    @Test
    void transactionsWithSameFieldsAreEqual() {
        AccountNumber acc = new AccountNumber("1234567890");
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, acc, 5000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(1L, TransactionType.DEPOSIT, acc, 5000, TransactionStatus.SUCCESS);

        assertEquals(tx1, tx2, "record сравнивает все поля");
    }

    @Test
    void printTransaction() {
        Transaction tx = new Transaction(
                1L,
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000,
                TransactionStatus.SUCCESS
        );

        System.out.println(tx);

        assertNotNull(tx);
    }
}

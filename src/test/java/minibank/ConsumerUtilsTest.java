package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerUtilsTest {

    @Test
    void addsDebitAccountsToListOfDebitAccount() {
        List<DebitAccount> target = new ArrayList<>();

        ConsumerUtils.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
    }

    @Test
    void addsDebitAccountsToListOfBankAccount() {
        List<BankAccount> target = new ArrayList<>();

        ConsumerUtils.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
        assertTrue(target.get(1) instanceof DebitAccount);
    }

    @Test
    void addsDebitAccountsToListOfObject() {
        List<Object> target = new ArrayList<>();

        ConsumerUtils.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
    }
}

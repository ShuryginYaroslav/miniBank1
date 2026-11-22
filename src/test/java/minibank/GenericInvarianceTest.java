package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenericInvarianceTest {

    @Test
    void wildcardListCanReadDebitAccounts() {
        List<DebitAccount> debitAccounts = new ArrayList<>();
        debitAccounts.add(new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000));

        List<? extends BankAccount> accounts = debitAccounts;

        assertEquals(1, accounts.size());
        assertEquals(new AccountNumber("0000000001"), accounts.get(0).getId());
    }

    @Test
    void copiedListCanAcceptAnotherAccountType() {
        List<DebitAccount> debitAccounts = new ArrayList<>();
        debitAccounts.add(new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000));

        List<BankAccount> accounts = new ArrayList<>(debitAccounts);
        accounts.add(new CreditAccount(new AccountNumber("0000000002"), "Petr", 2000, 1000));

        assertEquals(1, debitAccounts.size());
        assertEquals(2, accounts.size());
        assertTrue(accounts.get(1) instanceof CreditAccount);
    }
}

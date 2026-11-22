package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalanceUtilsTest {

    @Test
    void totalBalanceAcceptsListOfBankAccount() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("0000000001"), "Ivan", 1000));
        accounts.add(new CreditAccount(new AccountNumber("0000000002"), "Petr", 500, 2000));

        assertEquals(1500, BalanceUtils.totalBalance(accounts));
    }

    @Test
    void totalBalanceAcceptsListOfDebitAccount() {
        List<DebitAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("0000000003"), "Oleg", 700));

        assertEquals(700, BalanceUtils.totalBalance(accounts));
    }

    @Test
    void totalBalanceAcceptsListOfSavingsAccount() {
        List<SavingsAccount> accounts = new ArrayList<>();
        accounts.add(new SavingsAccount(new AccountNumber("0000000004"), "Anna", 2000, 500));

        assertEquals(2000, BalanceUtils.totalBalance(accounts));
    }

    @Test
    void totalBalanceAcceptsListOfCreditAccount() {
        List<CreditAccount> accounts = new ArrayList<>();
        accounts.add(new CreditAccount(new AccountNumber("0000000005"), "Maria", 300, 1000));

        assertEquals(300, BalanceUtils.totalBalance(accounts));
    }

    @Test
    void totalBalanceOfEmptyListIsZero() {
        List<DebitAccount> accounts = new ArrayList<>();

        assertEquals(0, BalanceUtils.totalBalance(accounts));
    }
}

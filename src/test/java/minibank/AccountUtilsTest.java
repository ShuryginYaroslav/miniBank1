package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountUtilsTest {

    @Test
    void returnsRichestDebitAccountPreservingConcreteType() {
        List<DebitAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("0000000031"), "Ivan", 1000));
        accounts.add(new DebitAccount(new AccountNumber("0000000032"), "Petr", 2500));
        accounts.add(new DebitAccount(new AccountNumber("0000000033"), "Maria", 700));

        DebitAccount richest = AccountUtils.richest(accounts);

        assertEquals(new AccountNumber("0000000032"), richest.getId());
        assertEquals(2500, richest.getBalance());
    }

    @Test
    void worksWithAnotherAccountSubtype() {
        List<SavingsAccount> accounts = new ArrayList<>();
        accounts.add(new SavingsAccount(new AccountNumber("0000000034"), "Anna", 3000, 500));
        accounts.add(new SavingsAccount(new AccountNumber("0000000035"), "Oleg", 1500, 500));

        SavingsAccount richest = AccountUtils.richest(accounts);

        assertEquals(new AccountNumber("0000000034"), richest.getId());
    }

    @Test
    void throwsOnEmptyList() {
        List<DebitAccount> accounts = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> AccountUtils.richest(accounts));
    }

    @Test
    void returnsTheOnlyElement() {
        List<DebitAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("0000000036"), "Oleg", 800));

        DebitAccount richest = AccountUtils.richest(accounts);

        assertSame(accounts.get(0), richest);
    }

    @Test
    void returnsFirstAccountOnEqualMaximumBalance() {
        List<DebitAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("0000000037"), "Ivan", 2000));
        accounts.add(new DebitAccount(new AccountNumber("0000000038"), "Petr", 2000));

        DebitAccount richest = AccountUtils.richest(accounts);

        assertSame(accounts.get(0), richest);
    }
}

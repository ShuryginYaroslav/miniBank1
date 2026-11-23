package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CopyUtilsTest {

    @Test
    void copiesDebitAccountsToBankAccounts() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount(new AccountNumber("0000000021"), "Ivan", 1000));
        source.add(new DebitAccount(new AccountNumber("0000000022"), "Petr", 2000));

        List<BankAccount> target = new ArrayList<>();

        CopyUtils.copy(source, target);

        assertEquals(2, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
        assertEquals(source.get(0).getId(), target.get(0).getId());
        assertEquals(source.get(1).getId(), target.get(1).getId());
    }

    @Test
    void copiesDebitAccountsToObjects() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount(new AccountNumber("0000000021"), "Ivan", 1000));

        List<Object> target = new ArrayList<>();

        CopyUtils.copy(source, target);

        assertEquals(1, target.size());
        assertTrue(target.get(0) instanceof DebitAccount);
    }

    @Test
    void copiesBankAccountsToBankAccounts() {
        List<BankAccount> source = new ArrayList<>();
        source.add(new CreditAccount(new AccountNumber("0000000023"), "Maria", 500, 1000));
        source.add(new DebitAccount(new AccountNumber("0000000024"), "Oleg", 700));

        List<BankAccount> target = new ArrayList<>();

        CopyUtils.copy(source, target);

        assertEquals(2, target.size());
        assertTrue(target.get(0) instanceof CreditAccount);
        assertTrue(target.get(1) instanceof DebitAccount);
    }

    @Test
    void preservesOrder() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(new DebitAccount(new AccountNumber("0000000021"), "Ivan", 1000));
        source.add(new DebitAccount(new AccountNumber("0000000022"), "Petr", 2000));
        source.add(new DebitAccount(new AccountNumber("0000000023"), "Maria", 3000));

        List<BankAccount> target = new ArrayList<>();

        CopyUtils.copy(source, target);

        assertEquals(
                List.of(
                        new AccountNumber("0000000021"),
                        new AccountNumber("0000000022"),
                        new AccountNumber("0000000023")
                ),
                target.stream().map(BankAccount::getId).toList()
        );
    }
}

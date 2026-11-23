package minibank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparatorDemoTest {
    private List<BankAccount> accounts() {
        return List.of(
                new DebitAccount(new AccountNumber("0000000041"), "Ivan", 3000),
                new DebitAccount(new AccountNumber("0000000042"), "Petr", 1000),
                new DebitAccount(new AccountNumber("0000000043"), "Maria", 2000)
        );
    }

    private List<Double> sortedBalances(Comparator<BankAccount> comparator) {
        List<BankAccount> sorted = new ArrayList<>(accounts());
        sorted.sort(comparator);
        return sorted.stream().map(BankAccount::getBalance).toList();
    }

    @Test
    void anonymousClassSortsByBalance() {
        assertEquals(List.of(1000.0, 2000.0, 3000.0), sortedBalances(ComparatorDemo.byBalanceAnonymous()));
    }

    @Test
    void lambdaSortsByBalance() {
        assertEquals(List.of(1000.0, 2000.0, 3000.0), sortedBalances(ComparatorDemo.byBalanceLambda()));
    }

    @Test
    void factoryMethodSortsByBalance() {
        assertEquals(List.of(1000.0, 2000.0, 3000.0), sortedBalances(ComparatorDemo.byBalanceFactory()));
    }

    @Test
    void allComparatorsAreEquivalent() {
        BankAccount lower = new DebitAccount(new AccountNumber("0000000051"), "Ivan", 1000);
        BankAccount same = new DebitAccount(new AccountNumber("0000000052"), "Petr", 1000);
        BankAccount higher = new DebitAccount(new AccountNumber("0000000053"), "Maria", 3000);

        for (Comparator<BankAccount> comparator : List.of(
                ComparatorDemo.byBalanceAnonymous(),
                ComparatorDemo.byBalanceLambda(),
                ComparatorDemo.byBalanceFactory()
        )) {
            assertTrue(comparator.compare(lower, higher) < 0);
            assertTrue(comparator.compare(higher, lower) > 0);
            assertEquals(0, comparator.compare(lower, same));
        }
    }
}

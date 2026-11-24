package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReduceDemoTest {
    private List<BankAccount> accounts() {
        return List.of(
                new DebitAccount(new AccountNumber("0000000261"), "Ivan", 1000),
                new DebitAccount(new AccountNumber("0000000262"), "Petr", 2000),
                new CreditAccount(new AccountNumber("0000000263"), "Maria", 3000, 5000)
        );
    }

    @Test
    void loopAndReduceReturnSameSum() {
        assertEquals(ReduceDemo.sumWithLoop(accounts()), ReduceDemo.sumWithReduce(accounts()));
    }

    @Test
    void reduceSumsBalances() {
        assertEquals(6000, ReduceDemo.sumWithReduce(accounts()));
    }

    @Test
    void emptyListReturnsIdentityValue() {
        List<BankAccount> empty = List.of();

        assertEquals(0, ReduceDemo.sumWithLoop(empty));
        assertEquals(0, ReduceDemo.sumWithReduce(empty));
    }
}

package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StreamFilterDemoTest {
    private List<BankAccount> accounts() {
        return List.of(
                new DebitAccount(new AccountNumber("0000000171"), "Ivan", 50_000),
                new DebitAccount(new AccountNumber("0000000172"), "Petr", 100_000),
                new CreditAccount(new AccountNumber("0000000173"), "Maria", 200_000, 50_000)
        );
    }

    @Test
    void loopAndStreamReturnSameResult() {
        assertEquals(StreamFilterDemo.filterWithLoop(accounts()), StreamFilterDemo.filterWithStream(accounts()));
    }

    @Test
    void streamFiltersAccountsByMinimumBalance() {
        List<BankAccount> accounts = accounts();

        assertEquals(List.of(accounts.get(1), accounts.get(2)), StreamFilterDemo.filterWithStream(accounts));
    }

    @Test
    void streamResultIsUnmodifiable() {
        List<BankAccount> result = StreamFilterDemo.filterWithStream(accounts());

        assertThrows(UnsupportedOperationException.class, result::clear);
    }
}

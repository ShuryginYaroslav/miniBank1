package minibank;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class CollectionFunctionsTest {
    private final List<BankAccount> accounts = List.of(
            new DebitAccount(new AccountNumber("0000000061"), "Ivan", 1000),
            new DebitAccount(new AccountNumber("0000000062"), "Petr", 0),
            new CreditAccount(new AccountNumber("0000000063"), "Maria", 500, 1000),
            new SavingsAccount(new AccountNumber("0000000064"), "Oleg", 200000, 500)
    );

    @Test
    void filtersByPositiveBalance() {
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        assertEquals(
                List.of(accounts.get(0), accounts.get(2), accounts.get(3)),
                CollectionFunctions.filter(accounts, positiveBalance)
        );
    }

    @Test
    void filtersByRichBalance() {
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;

        assertEquals(List.of(accounts.get(3)), CollectionFunctions.filter(accounts, rich));
    }

    @Test
    void filtersDebitAccountsOnly() {
        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;

        assertEquals(List.of(accounts.get(0), accounts.get(1)), CollectionFunctions.filter(accounts, debitOnly));
    }

    @Test
    void returnsIndependentListAndAcceptsSupertypePredicate() {
        Predicate<Object> alwaysTrue = value -> true;
        List<BankAccount> result = CollectionFunctions.filter(accounts, alwaysTrue);

        result.clear();

        assertEquals(accounts, CollectionFunctions.filter(accounts, alwaysTrue));
    }
}

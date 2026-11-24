package minibank;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class CollectionFunctionsTest {
    private final List<BankAccount> accounts = List.of(
            new DebitAccount(new AccountNumber("0000000061"), "Ivan", 1000),
            new DebitAccount(new AccountNumber("0000000062"), "Petr", 0),
            new CreditAccount(new AccountNumber("0000000063"), "Maria", 500, 1000),
            new SavingsAccount(new AccountNumber("0000000064"), "Oleg", 200000, 500)
    );

    private List<AccountSummary> summaries() {
        return List.of(
                new AccountSummary(new AccountNumber("0000000061"), "DebitAccount", 1000),
                new AccountSummary(new AccountNumber("0000000062"), "DebitAccount", 0),
                new AccountSummary(new AccountNumber("0000000063"), "CreditAccount", 500),
                new AccountSummary(new AccountNumber("0000000064"), "SavingsAccount", 200000)
        );
    }

    private String expectedSummaryOutput() {
        String lineSeparator = System.lineSeparator();
        return "AccountSummary[number=AccountNumber[value=0000000061], type=DebitAccount, balance=1000.0]" + lineSeparator
                + "AccountSummary[number=AccountNumber[value=0000000062], type=DebitAccount, balance=0.0]" + lineSeparator
                + "AccountSummary[number=AccountNumber[value=0000000063], type=CreditAccount, balance=500.0]" + lineSeparator
                + "AccountSummary[number=AccountNumber[value=0000000064], type=SavingsAccount, balance=200000.0]";
    }

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

    @Test
    void mapsAccountsToSummaries() {
        Function<BankAccount, AccountSummary> toSummary = account -> new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );

        assertEquals(
                List.of(
                        new AccountSummary(new AccountNumber("0000000061"), "DebitAccount", 1000),
                        new AccountSummary(new AccountNumber("0000000062"), "DebitAccount", 0),
                        new AccountSummary(new AccountNumber("0000000063"), "CreditAccount", 500),
                        new AccountSummary(new AccountNumber("0000000064"), "SavingsAccount", 200000)
                ),
                CollectionFunctions.map(accounts, toSummary)
        );
    }

    @Test
    void mapsAccountsToLabels() {
        Function<BankAccount, String> label =
                account -> account.getId() + " : " + account.getBalance();

        assertEquals(
                List.of(
                        "AccountNumber[value=0000000061] : 1000.0",
                        "AccountNumber[value=0000000062] : 0.0",
                        "AccountNumber[value=0000000063] : 500.0",
                        "AccountNumber[value=0000000064] : 200000.0"
                ),
                CollectionFunctions.map(accounts, label)
        );
    }

    @Test
    void mapReturnsIndependentListAndAcceptsSupertypeFunction() {
        Function<Object, String> stringValue = value -> String.valueOf(value);
        List<String> result = CollectionFunctions.map(accounts, stringValue);

        result.clear();

        assertEquals(accounts.size(), CollectionFunctions.map(accounts, stringValue).size());
    }

    @Test
    void forEachAppliesLambdaPrinter() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut = new PrintStream(output)) {
            System.setOut(capturedOut);
            Consumer<AccountSummary> printer = summary -> System.out.println(summary);

            CollectionFunctions.forEach(summaries(), printer);

            assertEquals(expectedSummaryOutput(), output.toString().trim());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void forEachAcceptsMethodReferencePrinter() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut = new PrintStream(output)) {
            System.setOut(capturedOut);

            CollectionFunctions.forEach(summaries(), System.out::println);

            assertEquals(expectedSummaryOutput(), output.toString().trim());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void forEachAcceptsSupertypeConsumer() {
        List<Object> seen = new ArrayList<>();
        Consumer<Object> collector = seen::add;

        CollectionFunctions.forEach(accounts, collector);

        assertEquals(accounts, seen);
    }
}

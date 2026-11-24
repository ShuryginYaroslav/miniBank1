package minibank;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {
    private static final AccountNumber ACC_FROM = new AccountNumber("1111111111");
    private static final AccountNumber ACC_TO = new AccountNumber("2222222222");

    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final TransactionJournal journal = new TransactionJournal();
    private final FakeNotificationService notificationService = new FakeNotificationService();
    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final TransferService transferService = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, journal);
    private final BankService service = new BankService(accounts, transferService);
    private final Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;
    private final Predicate<BankAccount> notBlocked =
            account -> !blockedAccounts.isBlocked(account.getId());
    private final Predicate<BankAccount> debit = DebitAccount.class::isInstance;
    private final Predicate<BankAccount> credit = CreditAccount.class::isInstance;

    @Test
    void openedAccountCanBeFound() {
        AccountNumber number = new AccountNumber("0000000100");
        BankAccount account = new DebitAccount(number, "Ivan", 1000);

        service.openAccount(account);

        assertSame(account, service.findAccount(number));
        assertEquals(1, accounts.size());
    }

    @Test
    void openingAccountWithSameIdReplacesExistingValue() {
        AccountNumber number = new AccountNumber("0000000100");
        BankAccount first = new DebitAccount(number, "Ivan", 1000);
        BankAccount second = new DebitAccount(number, "Petr", 5000);

        service.openAccount(first);
        service.openAccount(second);

        assertSame(second, service.findAccount(number));
        assertEquals(1, accounts.size());
    }

    @Test
    void openAccountRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.openAccount(null));
        assertEquals(0, accounts.size());
    }

    @Test
    void closedAccountIsNoLongerFound() {
        AccountNumber number = new AccountNumber("0000000100");
        service.openAccount(new DebitAccount(number, "Ivan", 1000));

        assertTrue(service.closeAccount(number));

        assertNull(service.findAccount(number));
        assertFalse(accounts.existsById(number));
        assertEquals(0, accounts.size());
    }

    @Test
    void closingMissingAccountReturnsFalse() {
        assertFalse(service.closeAccount(new AccountNumber("0000000200")));
    }

    @Test
    void findMissingAccountReturnsNull() {
        assertNull(service.findAccount(new AccountNumber("0000000200")));
    }

    @Test
    void transferByNumbersChangesBothBalances() {
        service.openAccount(new DebitAccount(ACC_FROM, "Ivan", 10000));
        service.openAccount(new DebitAccount(ACC_TO, "Petr", 2000));

        service.transfer(ACC_FROM, ACC_TO, 3000);

        assertEquals(7000, service.findAccount(ACC_FROM).getBalance());
        assertEquals(5000, service.findAccount(ACC_TO).getBalance());
        assertEquals(1, journal.size());
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void transferWithMissingSenderThrowsAndChangesNothing() {
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2000);
        service.openAccount(to);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Счёт отправителя не найден", ex.getMessage());
        assertEquals(2000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferWithMissingRecipientThrowsAndChangesNothing() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 2000);
        service.openAccount(from);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Счёт получателя не найден", ex.getMessage());
        assertEquals(2000, from.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferFromBlockedSenderThrowsAndChangesNothing() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 2000);
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2000);
        service.openAccount(from);
        service.openAccount(to);
        blockedAccounts.block(ACC_FROM);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 500));

        assertEquals("Source account is blocked", ex.getMessage());
        assertEquals(2000, from.getBalance());
        assertEquals(2000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void transferWithInsufficientFundsThrowsAndChangesNothing() {
        BankAccount from = new DebitAccount(ACC_FROM, "Ivan", 1000);
        BankAccount to = new DebitAccount(ACC_TO, "Petr", 2000);
        service.openAccount(from);
        service.openAccount(to);

        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(ACC_FROM, ACC_TO, 3000));

        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
        assertEquals(0, journal.size());
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void findAccountsFiltersByPositiveBalance() {
        BankAccount positive = new DebitAccount(new AccountNumber("0000000111"), "Ivan", 1000);
        BankAccount empty = new DebitAccount(new AccountNumber("0000000112"), "Petr", 0);
        service.openAccount(positive);
        service.openAccount(empty);

        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;
        List<BankAccount> result = service.findAccounts(positiveBalance);

        assertEquals(1, result.size());
        assertTrue(result.contains(positive));
        assertFalse(result.contains(empty));
    }

    @Test
    void findAccountsFiltersRichAccounts() {
        BankAccount rich = new DebitAccount(new AccountNumber("0000000113"), "Ivan", 100000);
        BankAccount poor = new DebitAccount(new AccountNumber("0000000114"), "Petr", 99999);
        service.openAccount(rich);
        service.openAccount(poor);

        Predicate<BankAccount> richPredicate = account -> account.getBalance() >= 100_000;
        List<BankAccount> result = service.findAccounts(richPredicate);

        assertEquals(List.of(rich), result);
    }

    @Test
    void findAccountsFiltersDebitAccountsOnly() {
        BankAccount debit = new DebitAccount(new AccountNumber("0000000115"), "Ivan", 1000);
        BankAccount credit = new CreditAccount(new AccountNumber("0000000116"), "Petr", 1000, 5000);
        service.openAccount(debit);
        service.openAccount(credit);

        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;
        List<BankAccount> result = service.findAccounts(debitOnly);

        assertEquals(List.of(debit), result);
    }

    @Test
    void findAccountsReturnsNewListAndDoesNotExposeStorage() {
        BankAccount account = new DebitAccount(new AccountNumber("0000000117"), "Ivan", 1000);
        service.openAccount(account);

        List<BankAccount> result = service.findAccounts(value -> true);
        result.clear();

        assertSame(account, service.findAccount(new AccountNumber("0000000117")));
        assertEquals(1, service.findAccounts(value -> true).size());
        assertNotSame(service.findAccounts(value -> true), service.findAccounts(value -> true));
    }

    @Test
    void findAccountsByMinimumBalance() {
        BankAccount richEnough = new DebitAccount(new AccountNumber("0000000121"), "Ivan", 100000);
        BankAccount tooPoor = new DebitAccount(new AccountNumber("0000000122"), "Petr", 99999);
        service.openAccount(richEnough);
        service.openAccount(tooPoor);
        double threshold = 100_000;
        Predicate<BankAccount> minimumBalance = account -> account.getBalance() >= threshold;

        assertEquals(List.of(richEnough), service.findAccounts(minimumBalance));
    }

    @Test
    void findAccountsIgnoreBlocked() {
        BankAccount unblocked = new DebitAccount(new AccountNumber("0000000123"), "Ivan", 1000);
        BankAccount blocked = new DebitAccount(new AccountNumber("0000000124"), "Petr", 1000);
        service.openAccount(unblocked);
        service.openAccount(blocked);
        blockedAccounts.block(blocked.getId());

        assertEquals(List.of(unblocked), service.findAccounts(notBlocked));
    }

    @Test
    void findAccountsOnlyDebit() {
        BankAccount debitAccount = new DebitAccount(new AccountNumber("0000000125"), "Ivan", 1000);
        BankAccount creditAccount = new CreditAccount(new AccountNumber("0000000126"), "Petr", 1000, 5000);
        service.openAccount(debitAccount);
        service.openAccount(creditAccount);

        assertEquals(List.of(debitAccount), service.findAccounts(debit));
    }

    @Test
    void findAccountsRichAndUnblockedDebit() {
        BankAccount richUnblockedDebit = new DebitAccount(new AccountNumber("0000000127"), "Ivan", 100000);
        BankAccount richBlockedDebit = new DebitAccount(new AccountNumber("0000000128"), "Petr", 200000);
        BankAccount richUnblockedCredit = new CreditAccount(new AccountNumber("0000000129"), "Maria", 300000, 5000);
        BankAccount poorUnblockedDebit = new DebitAccount(new AccountNumber("0000000130"), "Oleg", 1000);
        service.openAccount(richUnblockedDebit);
        service.openAccount(richBlockedDebit);
        service.openAccount(richUnblockedCredit);
        service.openAccount(poorUnblockedDebit);
        blockedAccounts.block(richBlockedDebit.getId());
        Predicate<BankAccount> candidates = rich.and(notBlocked).and(debit);

        assertEquals(List.of(richUnblockedDebit), service.findAccounts(candidates));
    }

    @Test
    void findAccountsNotDebit() {
        BankAccount debitAccount = new DebitAccount(new AccountNumber("0000000131"), "Ivan", 1000);
        BankAccount creditAccount = new CreditAccount(new AccountNumber("0000000132"), "Petr", 1000, 5000);
        BankAccount savingsAccount = new SavingsAccount(new AccountNumber("0000000133"), "Maria", 1000, 500);
        service.openAccount(debitAccount);
        service.openAccount(creditAccount);
        service.openAccount(savingsAccount);

        List<BankAccount> result = service.findAccounts(debit.negate());

        assertEquals(2, result.size());
        assertTrue(result.contains(creditAccount));
        assertTrue(result.contains(savingsAccount));
        assertFalse(result.contains(debitAccount));
    }

    @Test
    void findAccountsRichOrCredit() {
        BankAccount richDebit = new DebitAccount(new AccountNumber("0000000134"), "Ivan", 100000);
        BankAccount poorCredit = new CreditAccount(new AccountNumber("0000000135"), "Petr", 1000, 5000);
        BankAccount richSavings = new SavingsAccount(new AccountNumber("0000000136"), "Maria", 200000, 500);
        BankAccount poorSavings = new SavingsAccount(new AccountNumber("0000000137"), "Oleg", 1000, 500);
        service.openAccount(richDebit);
        service.openAccount(poorCredit);
        service.openAccount(richSavings);
        service.openAccount(poorSavings);

        List<BankAccount> result = service.findAccounts(rich.or(credit));

        assertEquals(3, result.size());
        assertTrue(result.contains(richDebit));
        assertTrue(result.contains(poorCredit));
        assertTrue(result.contains(richSavings));
        assertFalse(result.contains(poorSavings));
    }

    @Test
    void sortedAccountsByBalanceAscending() {
        BankAccount middle = new DebitAccount(new AccountNumber("0000000141"), "Ivan", 2000);
        BankAccount rich = new DebitAccount(new AccountNumber("0000000142"), "Petr", 3000);
        BankAccount poor = new DebitAccount(new AccountNumber("0000000143"), "Maria", 1000);
        service.openAccount(middle);
        service.openAccount(rich);
        service.openAccount(poor);
        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);

        assertEquals(List.of(poor, middle, rich), service.sortedAccounts(byBalance));
    }

    @Test
    void sortedAccountsByBalanceDescending() {
        BankAccount middle = new DebitAccount(new AccountNumber("0000000144"), "Ivan", 2000);
        BankAccount rich = new DebitAccount(new AccountNumber("0000000145"), "Petr", 3000);
        BankAccount poor = new DebitAccount(new AccountNumber("0000000146"), "Maria", 1000);
        service.openAccount(middle);
        service.openAccount(rich);
        service.openAccount(poor);
        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
        Comparator<BankAccount> byBalanceDesc = byBalance.reversed();

        assertEquals(List.of(rich, middle, poor), service.sortedAccounts(byBalanceDesc));
    }

    @Test
    void sortedAccountsByNumber() {
        BankAccount second = new DebitAccount(new AccountNumber("0000000148"), "Ivan", 1000);
        BankAccount first = new DebitAccount(new AccountNumber("0000000147"), "Petr", 2000);
        BankAccount third = new DebitAccount(new AccountNumber("0000000149"), "Maria", 3000);
        service.openAccount(second);
        service.openAccount(first);
        service.openAccount(third);
        Comparator<BankAccount> byNumber = Comparator.comparing(a -> a.getId().value());

        assertEquals(List.of(first, second, third), service.sortedAccounts(byNumber));
    }

    @Test
    void sortedAccountsByTypeThenBalance() {
        BankAccount credit = new CreditAccount(new AccountNumber("0000000150"), "Ivan", 500, 1000);
        BankAccount richDebit = new DebitAccount(new AccountNumber("0000000151"), "Petr", 3000);
        BankAccount poorDebit = new DebitAccount(new AccountNumber("0000000152"), "Maria", 1000);
        BankAccount savings = new SavingsAccount(new AccountNumber("0000000153"), "Oleg", 2000, 500);
        service.openAccount(richDebit);
        service.openAccount(savings);
        service.openAccount(poorDebit);
        service.openAccount(credit);
        Comparator<BankAccount> byTypeThenBalance =
                Comparator
                        .comparing((BankAccount a) -> a.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance);

        assertEquals(
                List.of(credit, poorDebit, richDebit, savings),
                service.sortedAccounts(byTypeThenBalance)
        );
    }

    @Test
    void sortingDoesNotChangeBankStorage() {
        BankAccount first = new DebitAccount(new AccountNumber("0000000154"), "Ivan", 3000);
        BankAccount second = new DebitAccount(new AccountNumber("0000000155"), "Petr", 1000);
        service.openAccount(first);
        service.openAccount(second);
        List<BankAccount> before = accounts.findAll();

        List<BankAccount> sorted = service.sortedAccounts(
                Comparator.comparingDouble(BankAccount::getBalance));

        assertNotSame(before, sorted);
        assertEquals(before, accounts.findAll());
        assertEquals(1000, service.findAccount(second.getId()).getBalance());
    }

    @Test
    void findRichAccountsPreservesOrderAndDoesNotChangeStorage() {
        BankAccount poor = new DebitAccount(new AccountNumber("0000000181"), "Ivan", 1000);
        BankAccount rich = new DebitAccount(new AccountNumber("0000000182"), "Petr", 100_000);
        BankAccount richest = new CreditAccount(new AccountNumber("0000000183"), "Maria", 200_000, 50_000);
        service.openAccount(poor);
        service.openAccount(rich);
        service.openAccount(richest);
        List<BankAccount> before = accounts.findAll();
        List<BankAccount> expected = before.stream()
                .filter(a -> a.getBalance() >= 100_000)
                .toList();

        assertEquals(expected, service.findRichAccounts(100_000));
        assertEquals(before, accounts.findAll());
    }

    @Test
    void findDebitAccountsPreservesOrderAndDoesNotChangeStorage() {
        BankAccount firstDebit = new DebitAccount(new AccountNumber("0000000184"), "Ivan", 1000);
        BankAccount credit = new CreditAccount(new AccountNumber("0000000185"), "Petr", 2000, 5000);
        BankAccount secondDebit = new DebitAccount(new AccountNumber("0000000186"), "Maria", 3000);
        service.openAccount(firstDebit);
        service.openAccount(credit);
        service.openAccount(secondDebit);
        List<BankAccount> before = accounts.findAll();
        List<BankAccount> expected = before.stream()
                .filter(DebitAccount.class::isInstance)
                .toList();

        assertEquals(expected, service.findDebitAccounts());
        assertEquals(before, accounts.findAll());
    }

    @Test
    void findAccountsByOwnerPreservesOrderAndDoesNotChangeStorage() {
        BankAccount first = new DebitAccount(new AccountNumber("0000000187"), "Ivan", 1000);
        BankAccount other = new DebitAccount(new AccountNumber("0000000188"), "Petr", 2000);
        BankAccount second = new SavingsAccount(new AccountNumber("0000000189"), "Ivan", 3000, 500);
        service.openAccount(first);
        service.openAccount(other);
        service.openAccount(second);
        List<BankAccount> before = accounts.findAll();
        List<BankAccount> expected = before.stream()
                .filter(a -> "Ivan".equals(a.getOwner()))
                .toList();

        assertEquals(expected, service.findAccountsByOwner("Ivan"));
        assertEquals(before, accounts.findAll());
    }

    @Test
    void streamQueriesReturnEmptyListsForEmptyRepository() {
        assertTrue(service.findRichAccounts(100_000).isEmpty());
        assertTrue(service.findDebitAccounts().isEmpty());
        assertTrue(service.findAccountsByOwner("Ivan").isEmpty());
    }

    @Test
    void streamQueriesReturnUnmodifiableLists() {
        service.openAccount(new DebitAccount(new AccountNumber("0000000190"), "Ivan", 1000));

        assertThrows(UnsupportedOperationException.class, () -> service.findRichAccounts(0).clear());
        assertThrows(UnsupportedOperationException.class, () -> service.findDebitAccounts().clear());
        assertThrows(UnsupportedOperationException.class, () -> service.findAccountsByOwner("Ivan").clear());
    }

    @Test
    void getAccountSummariesMapsDomainModelToDto() {
        service.openAccount(new DebitAccount(new AccountNumber("0000000191"), "Ivan", 1000));
        service.openAccount(new CreditAccount(new AccountNumber("0000000192"), "Petr", 2000, 5000));
        service.openAccount(new SavingsAccount(new AccountNumber("0000000193"), "Maria", 3000, 500));
        List<BankAccount> before = accounts.findAll();

        List<AccountSummary> summaries = service.getAccountSummaries();

        assertEquals(before.size(), summaries.size());
        for (int i = 0; i < before.size(); i++) {
            BankAccount account = before.get(i);
            assertEquals(
                    new AccountSummary(
                            account.getId(),
                            account.getClass().getSimpleName(),
                            account.getBalance()
                    ),
                    summaries.get(i)
            );
        }
    }

    @Test
    void getAccountSummariesReturnsEmptyListForEmptyRepository() {
        assertTrue(service.getAccountSummaries().isEmpty());
    }

    @Test
    void getAccountSummariesDoesNotChangeStorage() {
        service.openAccount(new DebitAccount(new AccountNumber("0000000194"), "Ivan", 1000));
        List<BankAccount> before = accounts.findAll();

        List<AccountSummary> summaries = service.getAccountSummaries();

        assertEquals(before, accounts.findAll());
        assertThrows(UnsupportedOperationException.class, summaries::clear);
    }

    @Test
    void richestFirstSortsByDescendingBalance() {
        BankAccount middle = new DebitAccount(new AccountNumber("0000000201"), "Ivan", 2000);
        BankAccount richest = new DebitAccount(new AccountNumber("0000000202"), "Petr", 3000);
        BankAccount poorest = new DebitAccount(new AccountNumber("0000000203"), "Maria", 1000);
        service.openAccount(middle);
        service.openAccount(richest);
        service.openAccount(poorest);

        assertEquals(List.of(richest, middle, poorest), service.richestFirst());
    }

    @Test
    void sortedByNumberSortsByAccountNumber() {
        BankAccount second = new DebitAccount(new AccountNumber("0000000205"), "Ivan", 1000);
        BankAccount first = new DebitAccount(new AccountNumber("0000000204"), "Petr", 2000);
        BankAccount third = new DebitAccount(new AccountNumber("0000000206"), "Maria", 3000);
        service.openAccount(second);
        service.openAccount(first);
        service.openAccount(third);

        assertEquals(List.of(first, second, third), service.sortedByNumber());
    }

    @Test
    void sortedByTypeThenBalanceSortsByTypeAndBalance() {
        BankAccount credit = new CreditAccount(new AccountNumber("0000000207"), "Ivan", 500, 1000);
        BankAccount richDebit = new DebitAccount(new AccountNumber("0000000208"), "Petr", 3000);
        BankAccount poorDebit = new DebitAccount(new AccountNumber("0000000209"), "Maria", 1000);
        BankAccount savings = new SavingsAccount(new AccountNumber("0000000210"), "Oleg", 2000, 500);
        service.openAccount(richDebit);
        service.openAccount(savings);
        service.openAccount(poorDebit);
        service.openAccount(credit);

        assertEquals(
                List.of(credit, poorDebit, richDebit, savings),
                service.sortedByTypeThenBalance()
        );
    }

    @Test
    void sortedByOwnerThenNumberSortsByOwnerAndNumber() {
        BankAccount petr = new DebitAccount(new AccountNumber("0000000212"), "Petr", 1000);
        BankAccount ivanLater = new DebitAccount(new AccountNumber("0000000214"), "Ivan", 2000);
        BankAccount ivanEarlier = new DebitAccount(new AccountNumber("0000000213"), "Ivan", 3000);
        BankAccount maria = new DebitAccount(new AccountNumber("0000000211"), "Maria", 4000);
        service.openAccount(petr);
        service.openAccount(ivanLater);
        service.openAccount(ivanEarlier);
        service.openAccount(maria);

        assertEquals(
                List.of(ivanEarlier, ivanLater, maria, petr),
                service.sortedByOwnerThenNumber()
        );
    }

    @Test
    void sortingResultsDoNotChangeRepositoryOrder() {
        service.openAccount(new DebitAccount(new AccountNumber("0000000215"), "Ivan", 1000));
        service.openAccount(new CreditAccount(new AccountNumber("0000000216"), "Petr", 2000, 5000));
        service.openAccount(new SavingsAccount(new AccountNumber("0000000217"), "Maria", 3000, 500));
        List<BankAccount> before = accounts.findAll();

        service.richestFirst();
        service.sortedByNumber();
        service.sortedByTypeThenBalance();
        service.sortedByOwnerThenNumber();

        assertEquals(before, accounts.findAll());
    }

    @Test
    void streamSortingResultsAreUnmodifiable() {
        service.openAccount(new DebitAccount(new AccountNumber("0000000218"), "Ivan", 1000));

        assertThrows(UnsupportedOperationException.class, () -> service.richestFirst().clear());
        assertThrows(UnsupportedOperationException.class, () -> service.sortedByNumber().clear());
        assertThrows(UnsupportedOperationException.class, () -> service.sortedByTypeThenBalance().clear());
        assertThrows(UnsupportedOperationException.class, () -> service.sortedByOwnerThenNumber().clear());
    }

    @Test
    void hasBlockedAccountsDetectsBlockedAccount() {
        BankAccount first = new DebitAccount(new AccountNumber("0000000221"), "Ivan", 1000);
        BankAccount second = new DebitAccount(new AccountNumber("0000000222"), "Petr", 2000);
        service.openAccount(first);
        service.openAccount(second);

        assertFalse(service.hasBlockedAccounts());

        blockedAccounts.block(second.getId());
        assertTrue(service.hasBlockedAccounts());

        blockedAccounts.unblock(second.getId());
        assertFalse(service.hasBlockedAccounts());
    }

    @Test
    void allAccountsPositiveChecksEveryBalance() {
        assertTrue(service.allAccountsPositive());

        service.openAccount(new DebitAccount(new AccountNumber("0000000223"), "Ivan", 1000));
        service.openAccount(new SavingsAccount(new AccountNumber("0000000224"), "Petr", 500, 500));
        assertTrue(service.allAccountsPositive());

        service.openAccount(new DebitAccount(new AccountNumber("0000000225"), "Maria", 0));
        assertFalse(service.allAccountsPositive());
    }

    @Test
    void noCreditAccountsChecksAccountTypes() {
        assertTrue(service.noCreditAccounts());

        service.openAccount(new DebitAccount(new AccountNumber("0000000226"), "Ivan", 1000));
        service.openAccount(new SavingsAccount(new AccountNumber("0000000227"), "Petr", 2000, 500));
        assertTrue(service.noCreditAccounts());

        service.openAccount(new CreditAccount(new AccountNumber("0000000228"), "Maria", 3000, 5000));
        assertFalse(service.noCreditAccounts());
    }

    @Test
    void anyMatchStopsAfterFirstMatchingElement() {
        List<Integer> values = List.of(1, 2, 3, 4, 5);
        AtomicInteger calls = new AtomicInteger();

        boolean found = values.stream().anyMatch(value -> {
            calls.incrementAndGet();
            return value == 2;
        });

        assertTrue(found);
        assertEquals(2, calls.get());
    }
}

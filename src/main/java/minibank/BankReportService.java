package minibank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class BankReportService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public BankReportService(Repository<AccountNumber, BankAccount> accounts,
                             TransferService transferService) {
        if (accounts == null) {
            throw new IllegalArgumentException("Репозиторий счетов не может быть нулевым");
        }
        if (transferService == null) {
            throw new IllegalArgumentException("Сервис переводов не может быть нулевым");
        }
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public List<BankAccount> findAccounts(Predicate<? super BankAccount> condition) {
        return CollectionFunctions.filter(accounts.findAll(), condition);
    }

    public List<BankAccount> findRichAccounts(double minBalance) {
        return accounts.findAll().stream()
                .filter(a -> a.getBalance() >= minBalance)
                .toList();
    }

    public List<BankAccount> findDebitAccounts() {
        return accounts.findAll().stream()
                .filter(DebitAccount.class::isInstance)
                .toList();
    }

    public List<BankAccount> findAccountsByOwner(String owner) {
        return accounts.findAll().stream()
                .filter(a -> Objects.equals(a.getOwner(), owner))
                .toList();
    }

    public List<AccountSummary> getAccountSummaries() {
        return accounts.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    public List<BankAccount> sortedAccounts(Comparator<? super BankAccount> comparator) {
        List<BankAccount> result = new ArrayList<>(accounts.findAll());
        result.sort(comparator);
        return result;
    }

    public List<BankAccount> richestFirst() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparingDouble(BankAccount::getBalance)
                        .reversed())
                .toList();
    }

    public List<BankAccount> sortedByNumber() {
        return accounts.findAll().stream()
                .sorted(Comparator.comparing((BankAccount a) -> a.getId().value()))
                .toList();
    }

    public List<BankAccount> sortedByTypeThenBalance() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparing((BankAccount a) -> a.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance))
                .toList();
    }

    public List<BankAccount> sortedByOwnerThenNumber() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparing((BankAccount a) -> a.getOwner())
                        .thenComparing(a -> a.getId().value()))
                .toList();
    }

    public boolean hasBlockedAccounts() {
        return accounts.findAll().stream()
                .anyMatch(a -> transferService.isBlocked(a.getNumber()));
    }

    public boolean allAccountsPositive() {
        return accounts.findAll().stream()
                .allMatch(a -> a.getBalance() > 0);
    }

    public boolean noCreditAccounts() {
        return accounts.findAll().stream()
                .noneMatch(CreditAccount.class::isInstance);
    }

    public Optional<BankAccount> findFirstRichAccount(double minBalance) {
        return accounts.findAll().stream()
                .filter(a -> a.getBalance() >= minBalance)
                .findFirst();
    }

    public double totalBalance() {
        return accounts.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    public OptionalDouble averageBalance() {
        return accounts.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .average();
    }

    public Optional<BankAccount> richestAccount() {
        return accounts.findAll().stream()
                .max(Comparator.comparingDouble(BankAccount::getBalance));
    }

    private AccountSummary toSummary(BankAccount account) {
        return new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );
    }
}

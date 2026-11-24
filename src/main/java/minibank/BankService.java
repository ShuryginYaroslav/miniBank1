package minibank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class BankService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public BankService(Repository<AccountNumber, BankAccount> accounts,
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

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Счёт не может быть null");
        }
        accounts.save(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        return accounts.findById(number);
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

    public boolean closeAccount(AccountNumber number) {
        return accounts.deleteById(number);
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        BankAccount from = accounts.findById(fromNumber);
        if (from == null) {
            throw new IllegalArgumentException("Счёт отправителя не найден");
        }
        BankAccount to = accounts.findById(toNumber);
        if (to == null) {
            throw new IllegalArgumentException("Счёт получателя не найден");
        }
        transferService.transfer(from, to, amount);
    }

    private AccountSummary toSummary(BankAccount account) {
        return new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );
    }
}

package minibank;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class BankService {
    private final AccountService accountService;
    private final TransferApplicationService transferApplicationService;
    private final BankReportService bankReportService;

    public BankService(Repository<AccountNumber, BankAccount> accounts,
                       TransferService transferService) {
        this(new AccountService(accounts),
                new TransferApplicationService(accounts, transferService),
                new BankReportService(accounts, transferService));
    }

    public BankService(AccountService accountService,
                       TransferApplicationService transferApplicationService,
                       BankReportService bankReportService) {
        if (accountService == null) {
            throw new IllegalArgumentException("Сервис счетов не может быть нулевым");
        }
        if (transferApplicationService == null) {
            throw new IllegalArgumentException("Сервис переводов не может быть нулевым");
        }
        if (bankReportService == null) {
            throw new IllegalArgumentException("Сервис отчётности не может быть нулевым");
        }
        this.accountService = accountService;
        this.transferApplicationService = transferApplicationService;
        this.bankReportService = bankReportService;
    }

    public void openAccount(BankAccount account) {
        accountService.open(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        return accountService.find(number).orElse(null);
    }

    public List<BankAccount> findAccounts(Predicate<? super BankAccount> condition) {
        return bankReportService.findAccounts(condition);
    }

    public List<BankAccount> findRichAccounts(double minBalance) {
        return bankReportService.findRichAccounts(minBalance);
    }

    public List<BankAccount> findDebitAccounts() {
        return bankReportService.findDebitAccounts();
    }

    public List<BankAccount> findAccountsByOwner(String owner) {
        return bankReportService.findAccountsByOwner(owner);
    }

    public List<AccountSummary> getAccountSummaries() {
        return bankReportService.getAccountSummaries();
    }

    public List<BankAccount> sortedAccounts(Comparator<? super BankAccount> comparator) {
        return bankReportService.sortedAccounts(comparator);
    }

    public List<BankAccount> richestFirst() {
        return bankReportService.richestFirst();
    }

    public List<BankAccount> sortedByNumber() {
        return bankReportService.sortedByNumber();
    }

    public List<BankAccount> sortedByTypeThenBalance() {
        return bankReportService.sortedByTypeThenBalance();
    }

    public List<BankAccount> sortedByOwnerThenNumber() {
        return bankReportService.sortedByOwnerThenNumber();
    }

    public boolean hasBlockedAccounts() {
        return bankReportService.hasBlockedAccounts();
    }

    public boolean allAccountsPositive() {
        return bankReportService.allAccountsPositive();
    }

    public boolean noCreditAccounts() {
        return bankReportService.noCreditAccounts();
    }

    public Optional<BankAccount> findFirstRichAccount(double minBalance) {
        return bankReportService.findFirstRichAccount(minBalance);
    }

    public double totalBalance() {
        return bankReportService.totalBalance();
    }

    public OptionalDouble averageBalance() {
        return bankReportService.averageBalance();
    }

    public Optional<BankAccount> richestAccount() {
        return bankReportService.richestAccount();
    }

    public boolean closeAccount(AccountNumber number) {
        return accountService.close(number);
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        transferApplicationService.transfer(fromNumber, toNumber, amount);
    }
}

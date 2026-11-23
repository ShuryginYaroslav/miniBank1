package minibank;

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

    public boolean closeAccount(AccountNumber number) {
        return accounts.deleteById(number);
    }
}

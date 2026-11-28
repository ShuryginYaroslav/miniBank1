package minibank;

import java.util.Optional;

public class AccountService {
    private final Repository<AccountNumber, BankAccount> accounts;

    public AccountService(Repository<AccountNumber, BankAccount> accounts) {
        if (accounts == null) {
            throw new IllegalArgumentException("Репозиторий счетов не может быть нулевым");
        }
        this.accounts = accounts;
    }

    public void open(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Счёт не может быть null");
        }
        accounts.save(account);
    }

    public Optional<BankAccount> find(AccountNumber number) {
        return Optional.ofNullable(accounts.findById(number));
    }

    public boolean close(AccountNumber number) {
        return accounts.deleteById(number);
    }
}

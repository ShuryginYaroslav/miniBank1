package minibank;

public class TransferApplicationService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public TransferApplicationService(Repository<AccountNumber, BankAccount> accounts,
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
}

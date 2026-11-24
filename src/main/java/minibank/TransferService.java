package minibank;

public class TransferService {
    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;
    private final TransactionJournal journal;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this(commissionPolicy, notificationService, new BlockedAccountRegistry(), new TransactionJournal());
    }

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService,
                           BlockedAccountRegistry blockedAccounts) {
        this(commissionPolicy, notificationService, blockedAccounts, new TransactionJournal());
    }

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService,
                           BlockedAccountRegistry blockedAccounts, TransactionJournal journal) {
        if (commissionPolicy == null) {
            throw new IllegalArgumentException("Политика комиссии не может быть нулевой");
        }
        if (notificationService == null) {
            throw new IllegalArgumentException("Cервис уведомлений не может быть нулевым");
        }
        if (blockedAccounts == null) {
            throw new IllegalArgumentException("Реестр заблокированных счетов не может быть нулевым");
        }
        if (journal == null) {
            throw new IllegalArgumentException("Журнал транзакций не может быть нулевым");
        }
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
    }

    public boolean isBlocked(AccountNumber number) {
        return blockedAccounts.isBlocked(number);
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Сумма перевода должна быть положительной");
        }

        if (blockedAccounts.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException("Нельзя перевести на тот же счёт");
        }

        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Превышен лимит перевода");
        }

        double commission = commissionPolicy.calculate(amount);
        double totalToWithdraw = amount + commission;

        from.withdraw(totalToWithdraw);
        to.deposit(amount);

        notificationService.notify("Трансфер " + amount + " завершен");

        journal.add(new Transaction(
                (long) (journal.size() + 1),
                TransactionType.TRANSFER,
                from.getNumber(),
                amount,
                TransactionStatus.SUCCESS
        ));
    }
}



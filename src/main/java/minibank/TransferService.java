package minibank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        if (commissionPolicy == null) {
            throw new IllegalArgumentException("Политика комиссии не может быть нулевой");
        }
        if (notificationService == null) {
            throw new IllegalArgumentException("Cервис уведомлений не может быть нулевым");
        }
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            return false;
        }

        if (from == to) {
            return false;
        }

        double commission = commissionPolicy.calculate(amount);
        double totalToWithdraw = amount + commission;

        try {
            from.withdraw(totalToWithdraw);
        } catch (InsufficientFundsException e) {
            return false;
        }
        to.deposit(amount);

        notificationService.notify("Трансфер " + amount + " завершен");
        return true;
    }
}



package minibank;

public class Demo {
    public static void main(String[] args) {
        CommissionPolicy noCommission = amount -> 0.0;
        CommissionPolicy onePercent = amount -> amount * 0.01;
        NotificationService console = message -> System.out.println(message);
        NotificationService consoleReference = System.out::println;

        TransferService transferService = new TransferService(noCommission, console);
        TransferService onePercentService = new TransferService(onePercent, consoleReference);

        BankAccount from = new DebitAccount(new AccountNumber("0000000001"), "Ivan", 10000);
        BankAccount to = new DebitAccount(new AccountNumber("0000000002"), "Petr", 2000);

        try {
            transferService.transfer(from, to, 5000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        try {
            transferService.transfer(from, to, 6000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        try {
            onePercentService.transfer(to, from, 1000);
            System.out.println("Transfer with commission completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer with commission failed: " + e.getMessage());
        }

        System.out.println("from balance: " + from.getBalance());
        System.out.println("to balance: " + to.getBalance());
    }
}

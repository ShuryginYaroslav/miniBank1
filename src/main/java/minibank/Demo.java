package minibank;

public class Demo {
    public static void main(String[] args) {
        TransferService transferService = new TransferService(new NoCommission(), message -> {});

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

        System.out.println("from balance: " + from.getBalance());
        System.out.println("to balance: " + to.getBalance());
    }
}

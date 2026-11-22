package minibank;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TransferService transferService = new TransferService(new PercentCommission(0.01), new ConsoleNotificationService());

        System.out.println("=== МИНИ-БАНК ===");
        System.out.print("Введите номер счёта: ");
        String numberStr = scanner.nextLine();
        AccountNumber number = new AccountNumber(numberStr);

        System.out.print("Введите владельца: ");
        String owner = scanner.nextLine();

        System.out.print("Начальный баланс: ");
        double balance = readDouble(scanner);

        System.out.println("Выберите тип счёта:");
        System.out.println("1 - Дебетовый (DebitAccount)");
        System.out.println("2 - Сберегательный (SavingsAccount)");
        System.out.println("3 - Кредитный (CreditAccount)");
        int type = readInt(scanner);

        BankAccount account;
        if (type == 2) {
            System.out.print("Введите минимальный остаток: ");
            double minBalance = readDouble(scanner);
            account = new SavingsAccount(number, owner, balance, minBalance);
        } else {
            account = new DebitAccount(number, owner, balance);
        }
        if (type == 3) {
            System.out.print("Введите кредитный лимит: ");
            double creditLimit = readDouble(scanner);
            account = new CreditAccount(number, owner, balance, creditLimit);
        }

        System.out.println("\nСчёт создан: " + account.getOwner()
                + " | Баланс: " + account.getBalance());


        while (true) {
            System.out.println("\n--- МЕНЮ ---");
            System.out.println("1. Пополнить (deposit)");
            System.out.println("2. Снять (withdraw)");
            System.out.println("3. Показать баланс");
            System.out.println("4. Перевести на другой счёт");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Сумма для пополнения: ");
                    double dep = readDouble(scanner);
                    account.deposit(dep);
                    System.out.println("Баланс после пополнения: " + account.getBalance());
                    break;

                case 2:
                    System.out.print("Сумма для снятия: ");
                    double wd = readDouble(scanner);
                    try {
                        account.withdraw(wd);
                        System.out.println("✅ Успешно. Новый баланс: " + account.getBalance());
                    } catch (IllegalArgumentException | InsufficientFundsException e) {
                        System.out.println("❌ Отказано: " + e.getMessage() + ". Баланс: " + account.getBalance());
                    }
                    break;

                case 3:
                    System.out.println("Текущий баланс: " + account.getBalance());
                    break;
                case 4:
                    System.out.println("=== Перевод ===");
                    System.out.print("Введите номер счёта получателя: ");
                    String toNumberStr = scanner.nextLine();
                    AccountNumber toNumber = new AccountNumber(toNumberStr);


                    System.out.print("Введите имя владельца получателя: ");
                    String toOwner = scanner.nextLine();

                    System.out.print("Начальный баланс получателя: ");
                    double toBalance = readDouble(scanner);

                    BankAccount recipient = new DebitAccount(toNumber, toOwner, toBalance);

                    System.out.print("Сумма перевода: ");
                    double transferAmount = readDouble(scanner);

                    CommissionPolicy displayPolicy = new PercentCommission(0.01);
                    double commission = displayPolicy.calculate(transferAmount);
                    System.out.println("Комиссия: " + commission + ", Итого к списанию: " + (transferAmount + commission));

                    boolean transferOk = transferService.transfer(account, recipient, transferAmount);
                    if (transferOk) {
                        System.out.println("✅ Перевод успешен.");
                        System.out.println("   Баланс отправителя: " + account.getBalance());
                        System.out.println("   Баланс получателя:  " + recipient.getBalance());
                    } else {
                        System.out.println("❌ Перевод отклонён.");
                        System.out.println("   Баланс отправителя: " + account.getBalance());
                        System.out.println("   Баланс получателя:  " + recipient.getBalance());
                    }
                    break;


                case 0:
                    System.out.println("Выход...");
                    return;

                default:
                    System.out.println("Нет такого пункта.");
            }
        }
    }

    private static double readDouble(Scanner scanner) {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }

    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }
}

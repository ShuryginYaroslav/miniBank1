package minibank;

import java.util.Objects;

public abstract class BankAccount {
    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        if (number == null) {
            throw new IllegalArgumentException("Номер счета не может быть null");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        if (amount > getAvailableAmount()) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        setBalance(this.balance - amount);
    }

    protected abstract double getAvailableAmount();

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountNumber getNumber() { return number; }

    public String getOwner() { return owner; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "number='" + number.value() + '\'' +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof BankAccount)) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
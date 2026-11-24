package minibank;

@FunctionalInterface
public interface CommissionPolicy {
    double calculate(double amount);
}
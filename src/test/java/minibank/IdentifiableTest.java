package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdentifiableTest {

    @Test
    void bankAccountExposesItsNumberAsId() {
        AccountNumber number = new AccountNumber("0000000001");
        BankAccount account = new DebitAccount(number, "Ivan", 1000);

        assertEquals(number, account.getId());
    }

    @Test
    void identifiableReferenceWorksPolymorphically() {
        AccountNumber number = new AccountNumber("0000000002");
        Identifiable<AccountNumber> entity = new SavingsAccount(number, "Petr", 1000, 500);

        assertEquals(number, entity.getId());
    }
}

package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void stringIntegerPairStoresValues() {
        Pair<String, Integer> age = new Pair<>("Ivan", 20);

        assertEquals("Ivan", age.key());
        assertEquals(20, age.value().intValue());
    }

    @Test
    void accountNumberStringPairStoresValues() {
        AccountNumber accountNumber = new AccountNumber("0000000001");
        Pair<AccountNumber, String> owner = new Pair<>(accountNumber, "Ivan");

        assertEquals(accountNumber, owner.key());
        assertEquals("Ivan", owner.value());
    }

    @Test
    void pairsWithSameValuesAreEqual() {
        Pair<String, Integer> first = new Pair<>("Ivan", 20);
        Pair<String, Integer> second = new Pair<>("Ivan", 20);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}

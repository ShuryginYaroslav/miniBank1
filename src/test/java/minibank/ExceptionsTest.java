package minibank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void insufficientFundsExceptionHasMessage() {
        String message = "Недостаточно денег";
        InsufficientFundsException ex = new InsufficientFundsException(message);

        assertEquals(message, ex.getMessage());
    }

    @Test
    void transferLimitExceededExceptionHasMessage() {
        String message = "Лимит превышен";
        TransferLimitExceededException ex = new TransferLimitExceededException(message);

        assertEquals(message, ex.getMessage());
    }

    @Test
    void invalidAmountExceptionHasMessage() {
        String message = "Неверная сумма";
        InvalidAmountException ex = new InvalidAmountException(message);

        assertEquals(message, ex.getMessage());
    }
}
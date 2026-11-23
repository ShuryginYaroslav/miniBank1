package minibank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferQueueTest {
    private static final AccountNumber ACC_A = new AccountNumber("0000000100");
    private static final AccountNumber ACC_B = new AccountNumber("0000000200");
    private static final AccountNumber ACC_C = new AccountNumber("0000000300");

    @Test
    void requestsAreReturnedInInsertionOrder() {
        TransferQueue queue = new TransferQueue();
        TransferRequest a = new TransferRequest(ACC_A, ACC_B, 100);
        TransferRequest b = new TransferRequest(ACC_B, ACC_C, 200);
        TransferRequest c = new TransferRequest(ACC_C, ACC_A, 300);
        queue.add(a);
        queue.add(b);
        queue.add(c);

        assertSame(a, queue.next());
        assertSame(b, queue.next());
        assertSame(c, queue.next());
    }

    @Test
    void peekDoesNotRemoveFirstElement() {
        TransferQueue queue = new TransferQueue();
        TransferRequest first = new TransferRequest(ACC_A, ACC_B, 100);
        TransferRequest second = new TransferRequest(ACC_B, ACC_C, 200);
        queue.add(first);
        queue.add(second);

        assertSame(first, queue.peek());
        assertEquals(2, queue.size());
        assertSame(first, queue.next());
    }

    @Test
    void nextAndPeekOnEmptyQueueReturnNull() {
        TransferQueue queue = new TransferQueue();

        assertNull(queue.next());
        assertNull(queue.peek());
        assertTrue(queue.isEmpty());
    }

    @Test
    void sizeDecreasesAfterRetrieval() {
        TransferQueue queue = new TransferQueue();
        queue.add(new TransferRequest(ACC_A, ACC_B, 100));
        queue.add(new TransferRequest(ACC_B, ACC_C, 200));
        assertEquals(2, queue.size());

        queue.next();
        assertEquals(1, queue.size());

        queue.next();
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }
}

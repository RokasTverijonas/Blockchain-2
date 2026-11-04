package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import model.Transaction;
import service.MerkleTree;

public class MerkleTreeTest {
    
    static Transaction tx(long sequence, long amount) {
        String id = Transaction.computeId("af2", "b3e4", amount, sequence);

        return new Transaction("af2", "b3e4", amount, sequence, id);
    }

    static String root(Transaction... txs) {
        List<String> ids = new ArrayList<>();
        for (Transaction t : txs) {
            ids.add(t.getId());
        }
        return MerkleTree.calculateRoot(ids);
    }

    static Transaction t1 = tx(1, 100), t2 = tx(2,100), t3 = tx(3, 100),
    t4 = tx(4, 100), t5 = tx(5, 100);

    @Test
    void testDifferentTransactionCounts() {
        String r1 = root(t1);
        String r2 = root(t1, t2);
        String r3 = root(t1, t2, t3);
        String r5 = root(t1, t2, t3, t4, t5);

        assertEquals(t1.getId(), r1);
        assertEquals(4, Set.of(r1, r2, r3, r5).size());
    }

    @Test
    void changedTransactionChangesRoot() {
        Transaction changed = tx(2, 101);
        assertNotEquals(root(t1, t2), root(t1, changed));
    }

    @Test
    void swappedOrderChangesRoot() {
        assertNotEquals(root(t1, t2), root(t2, t1));
    }
}

package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Balances;
import model.Block;
import model.Blockchain;
import model.Transaction;
import service.MiningService;
import service.TransactionPool;

public class ValidationTest {
    Balances initial;
    Blockchain chain;
    
    @BeforeEach 
    void setUp() {
        initial = new Balances();
        initial.setBalance("a1", 100);
        initial.setBalance("b2", 0);
        initial.setBalance("c3", 0);
        chain = new Blockchain(initial, 3);
    }

    static Transaction tx(String from, String to, long amount, long sequence) {
        return new Transaction(from, to, amount, sequence, Transaction.computeId(from, to, amount, sequence));
    }

    Block minedBlock(Transaction... txs) {
        String prevHash = chain.last().calculateHash();
        List<Transaction> list = List.of(txs);

        Block block = new Block(prevHash, 1000L, Blockchain.VERSION, 0, chain.getDifficulty(), list);
        MiningService.mine(block);
        return block;
    }

    @Test
    void validPaymentIsIncludedOnce() {
        assertNull(chain.addBlock(minedBlock(tx("a1", "b2", 70, 1))));
        assertEquals(30, chain.getBalances().getBalance("a1"));
        assertEquals(70, chain.getBalances().getBalance("b2"));
        assertEquals(100, chain.getBalances().total());
    }

    @Test 
    void poolRejectsUnknownReceiverZeroNegativeAndChangedId() {
        TransactionPool pool = new TransactionPool();
        assertNotNull(pool.add(tx("a1", "zz", 10, 1), initial, new HashSet<>()));
        assertNotNull(pool.add(tx("a1", "b2", 0, 2), initial, new HashSet<>()));
        assertNotNull(pool.add(tx("a1", "b2", -5, 3), initial, new HashSet<>()));
        assertNotNull(pool.add(new Transaction("a1", "b2", 10, 4, "wrongId"), initial, new HashSet<>()));
        assertEquals(0, pool.size());
        assertEquals(4, pool.getRejected().size());
    }
    
    @Test
    void twoPaymentsExceedingBalanceAreRejected() {
        Block block = minedBlock(tx("a1", "b2", 70,1), tx("a1", "c3", 70,2));
        assertNotNull(chain.addBlock(block));
        assertEquals(100, chain.getBalances().getBalance("a1"));

    }

    @Test 
    void duplicateAndAlreadyConfirmedAreRejected() {
        Transaction t = tx("a1", "b2", 10, 1);
        chain.addBlock(minedBlock(t));

        TransactionPool pool = new TransactionPool();
        assertNotNull(pool.add(t, chain.getBalances(), chain.getConfirmedIds()));

        assertNotNull(chain.addBlock(minedBlock(t)));
        assertEquals(90, chain.getBalances().getBalance("a1"));
    }

    @Test
    void badPrevHashOrPowIsRejectedAndStateUnchanged() {
        Transaction t = tx("a1", "b2", 10, 1);
        //blogas prev
        Block badPrev = new Block("0".repeat(64), 1000L, Blockchain.VERSION, 0, 3, List.of(t));
        MiningService.mine(badPrev);
        assertNotNull(chain.addBlock(badPrev));

        //blogas pow
        Block badPow = new Block(chain.last().calculateHash(), 1000L, Blockchain.VERSION, 0, 3, List.of(t));
        long nonce = 0;
        do {
            badPow.setNonce(nonce++);
        } while (badPow.calculateHash().startsWith("000"));
        assertNotNull(chain.addBlock(badPow));

        assertEquals(100, chain.getBalances().getBalance("a1"));
        //tik genesis
        assertEquals(1, chain.size());

    }
}

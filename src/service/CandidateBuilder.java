package service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.Balances;
import model.Block;
import model.Blockchain;
import model.Transaction;

public class CandidateBuilder {
    
    public static List<Transaction> select(TransactionPool pool, Balances confirmed, Random random) {
        List<Transaction> all = pool.getPending();
        Collections.shuffle(all, random);

        Balances temp = confirmed.copy();
        List<Transaction> selected = new ArrayList<>();

        for (Transaction tx : all) {
            if (selected.size() == 100) {
                break;
            }
            if (temp.canApply(tx)) {
                temp.apply(tx);
                selected.add(tx);
            }
        }
        return selected;
    }

    public static List<Block> buildCandidates(Blockchain chain, TransactionPool pool, Random random, int count) {
        String prevHash = chain.last().calculateHash();
        long timestamp = System.currentTimeMillis() / 1000;

        List<Block> candidates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<Transaction> selected = select(pool, chain.getBalances(), random);
            if (selected.isEmpty()) {
                break;
            }

            candidates.add(new Block(prevHash, timestamp, Blockchain.VERSION, 0, chain.getDifficulty(), selected));
        }
        return candidates;
    }
}

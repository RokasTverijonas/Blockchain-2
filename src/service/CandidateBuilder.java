package service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.Balances;
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
}

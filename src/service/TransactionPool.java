package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Balances;
import model.Transaction;

public class TransactionPool {
    
    private final Map<String, Transaction> pending = new HashMap<>();
    private final Map<String, String> rejected = new HashMap<>();

    public String add(Transaction tx, Balances balances, Set<String> confirmedIds) {
        String reason = null;
        if (!balances.hasAccount(tx.getSender()) || !balances.hasAccount(tx.getReceiver())) {
            reason = "unknown participant";
        } else if (tx.getSender().equals(tx.getReceiver())) {
            reason = "sender equals receiver";
        } else if (tx.getAmount() <= 0) {
            reason = "non-positive amount";
        } else if (pending.containsKey(tx.getId()) || confirmedIds.contains(tx.getId())) {
            reason = "duplicate id";
        }

        if (reason != null) {
            rejected.put(tx.getId(), reason);
            return reason;
        }
        pending.put(tx.getId(), tx);
        return null;
    }

    public List<Transaction> getPending() {
        List<Transaction> list = new ArrayList<>(pending.values());
        list.sort(Comparator.comparingLong(Transaction::getSequence));
        return list;
    }

    public void removeAll(List<Transaction> included) {
        for (Transaction tx : included) {
            pending.remove(tx.getId());
        }
    }

    public boolean isEmpty() {
        return pending.isEmpty();
    }
    public int size() {
        return pending.size();
    }
    public Map<String, String> getRejected() {
        return rejected;
    }
}

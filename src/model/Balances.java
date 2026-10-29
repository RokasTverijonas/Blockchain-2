package model;

import java.util.HashMap;
import java.util.Map;

public class Balances {
    private final Map<String, Long> balances = new HashMap<>();

    public void setBalance(String publicKey, long balance) {
        balances.put(publicKey, balance);
    }

    public long getBalance(String publicKey) {
        return balances.get(publicKey);
    }

    public boolean hasAccount(String publicKey) {
        return balances.containsKey(publicKey);
    }

    public Balances copy() {
        Balances copy = new Balances();
        copy.balances.putAll(this.balances);
        return copy;
    }

    public long total() {
        long sum = 0;
        for (long b : balances.values()) {
            sum += b;
        }
        return sum;
    }

    public boolean canApply(Transaction tx) {
        if (!hasAccount(tx.getSender()) || !hasAccount(tx.getReceiver())) {
            return false;
        }
        long senderBalance = balances.get(tx.getSender());
        long receiverBalance = balances.get(tx.getReceiver());
        if (tx.getAmount() <= 0 || senderBalance < tx.getAmount()) {
            return false;
        }
        return true;
    }

    public void apply(Transaction tx) {
        balances.put(tx.getSender(), balances.get(tx.getSender()) - tx.getAmount());
        balances.put(tx.getReceiver(), balances.get(tx.getReceiver()) + tx.getAmount());
    }
}

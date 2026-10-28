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

    public long Total() {
        long sum = 0;
        for (long b : balances.values()) {
            sum += b;
        }
        return sum;
    }
}

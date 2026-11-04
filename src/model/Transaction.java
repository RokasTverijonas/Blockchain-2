package model;

import hash.HashFunction;

public class Transaction {
    private final String sender;
    private final String receiver;
    private final long amount;
    private final long sequence;
    private final String id;


    public Transaction(String sender, String receiver, long amount, long sequence, String id) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.sequence = sequence;
        this.id = id;
    }

    public static String serialize(String sender, String receiver, long amount, long sequence) {
        return sender + "|" + receiver + "|" + amount + "|" + sequence;
    }

    public static String computeId(String sender, String receiver, long amount, long sequence) {
        return HashFunction.hash(serialize(sender, receiver, amount, sequence));
    }

    public boolean hasValidId() {
        return id.equals(computeId(sender, receiver, amount, sequence));
    }

    public String getSender() {
        return sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public long getAmount() {
        return amount;
    }
    public long getSequence() {
        return sequence;
    }
    public String getId() {
        return id;  
    }

    @Override 
    public String toString() {
        return serialize(sender, receiver, amount, sequence) + " -> " + id;
    }
}

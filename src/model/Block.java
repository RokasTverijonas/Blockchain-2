package model;

import java.util.ArrayList;
import java.util.List;

import hash.HashFunction;
import service.MerkleTree;

public class Block {
    private final String prevHash;
    private final long timestamp;
    private final int version;
    private final String txRoot;
    private long nonce;
    private final int difficulty;

    private final String headerStart;
    private final String headerEnd;

    private final List<Transaction> transactions;

    public Block(String prevHash, long timestamp, int version, long nonce, int difficulty, List<Transaction> transactions) {
        this.prevHash = prevHash;
        this.timestamp = timestamp;
        this.version = version;
        this.transactions = List.copyOf(transactions);
        this.txRoot = computeTxRoot(this.transactions);
        this.nonce = nonce;
        this.difficulty = difficulty;
        this.headerStart = prevHash + "|" + timestamp + "|" + version + "|" + txRoot + "|";
        this.headerEnd = "|" + difficulty;
    }

    public static String computeTxRoot(List<Transaction> txs) {
        List<String> ids = new ArrayList<>();

        for (Transaction tx : txs) {
            ids.add(tx.getId());
        }

        return MerkleTree.calculateRoot(ids);
    }

    public String calculateHash() {
        return HashFunction.hash(headerStart + nonce + headerEnd);
    }

    public String getPrevHash() {
        return prevHash;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public int getVersion() {
        return version;
    }
    public String getTxRoot() {
        return txRoot;
    }
    public long getNonce() {
        return nonce;
    }
    public int getDifficulty() {
        return difficulty;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }



}

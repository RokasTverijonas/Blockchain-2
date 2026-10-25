package model;

public class User {
    private int id;
    private String name;
    private String publicKey;
    private long balance;

    public User(int id, String name, String publicKey, long balance) {
        this.id = id;
        this.name = name;
        this.publicKey = publicKey;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPublicKey() {
        return publicKey;
    }
    public long getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    public void setBalance(long balance) {
        this.balance = balance;
    }
}

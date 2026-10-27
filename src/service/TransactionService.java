package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Transaction;
import model.User;

public class TransactionService {
    private static final long MIN_AMOUNT = 1;
    private static final long MAX_AMOUNT = 100000;
    public final static long seed = 283;

    public List<Transaction> generateTransactions(List<User> users,  int count) {
        if (users.size() < 2 ) {
             throw new IllegalArgumentException("Reikia bent dviejų vartotojų.");
        }
        Random random = new Random(seed);
        List<Transaction> transactions = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            User sender = users.get(random.nextInt(users.size()));
            User receiver = users.get(random.nextInt(users.size()));

            while (sender.getId() == receiver.getId()) {
                receiver = users.get(random.nextInt(users.size()));
            }

            String senderKey = sender.getPublicKey();
            String receiverKey = receiver.getPublicKey();

            long amount = random.nextLong(MIN_AMOUNT, MAX_AMOUNT + 1);

            long sequence = i + 1;

            String id = Transaction.computeId(senderKey, receiverKey, amount, sequence);

            transactions.add(new Transaction(senderKey, receiverKey, amount, sequence, id));
            
        }
        return transactions;
    }
}

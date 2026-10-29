import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.Balances;
import model.Block;
import model.Blockchain;
import model.Transaction;
import model.User;
import service.CandidateBuilder;
import service.MiningService;
import service.TransactionPool;
import service.TransactionService;
import service.UserService;

public class Main {
    public static void main(String[] args) throws Exception {
        long startAll = System.nanoTime();
        int difficulty = 3;
        
        //generate users and balances
        UserService userService = new UserService();
        List<User> users = userService.generateUsers(1000);
        
        Balances initial = new Balances();
        for (User u : users) {
            initial.setBalance(u.getPublicKey(), u.getBalance());
        }
        long initialTotal = initial.total();

        //transactions
        List<Transaction> all = new TransactionService().generateTransactions(users, 10000);

        //blockchain (the genesis is inside)
        Blockchain chain = new Blockchain(initial, difficulty);
        TransactionPool pool = new TransactionPool();
        for (Transaction tx : all) {
            pool.add(tx, initial, chain.getConfirmedIds());
        }

        //ciklas
        Random random = new Random(TransactionService.seed);
        int included = 0;

        while (!pool.isEmpty()) {
            List<Transaction> selected = CandidateBuilder.select(pool, chain.getBalances(), random);
            if (selected.isEmpty()) {
                break;
            }

            Block block = new Block(chain.last().calculateHash(), System.currentTimeMillis() / 1000, 1, 0, difficulty, selected);
            MiningService.Result result = MiningService.mine(block);

            String error = chain.addBlock(block);
            if (error != null) {
                System.out.println("Block rejected: " + error);
                break;
            }

            pool.removeAll(selected);
            included += selected.size();

            System.out.println("Block " + (chain.size() - 1)
                    + " | tx=" + selected.size()
                    + " | prev=" + block.getPrevHash()
                    + " | root=" + block.getTxRoot()
                    + " | hash=" + result.hash()
                    + " | diff=" + difficulty
                    + " | nonce=" + block.getNonce()
                    + " | attempts=" + result.attempts()
                    + " | " + result.millis() + " ms");
        }

        System.out.println("Users: " + users.size() + ", generated tx: " + all.size());
        System.out.println("Blocks: " + (chain.size() - 1) + ", included: " + included + ", rejected: " + pool.getRejected().size() + ", left: " + pool.size());
        System.out.println("Initial total: " + initialTotal + " -> " + chain.getBalances().total());
        System.out.println("Time: " + (System.nanoTime() - startAll) / 1000000 + " ms");

    }
}

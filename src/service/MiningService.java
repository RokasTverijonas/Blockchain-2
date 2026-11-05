package service;

import java.util.List;
import java.util.Random;


import model.Block;
import model.Blockchain;

public class MiningService {

    public static final int CANDIDATES = 5;
    public static final long FIRST_BUDGET = 5000;
    public static final long MAX_BUDGET = 1000000;
    public record Result(
        String hash,
        long attempts,
        long millis
    ) {}

    public static Result mine(Block block) {
        String hashPrefix = "0".repeat(block.getDifficulty());

        long start = System.nanoTime();
        long nonce = 0;

        while (true) {
            block.setNonce(nonce);
            String hash = block.calculateHash();
            if (hash.startsWith(hashPrefix)) {
                long millis = (System.nanoTime() - start) / 1000000;
                return new Result(hash, nonce + 1, millis);
            }
            nonce++;
        }
    }

    public static Block mineNext(Blockchain chain,  TransactionPool pool, Random random) {
        List<Block> candidates = CandidateBuilder.buildCandidates(chain, pool, random, CANDIDATES);
        if (candidates.isEmpty()) {
            return null;
        }

        String prefix = "0".repeat(chain.getDifficulty());
        long[] attempts = new long[candidates.size()];
        long[] millis = new long[candidates.size()];
        int winner = -1;
        
        for (long budget = FIRST_BUDGET; budget <= MAX_BUDGET && winner == -1; budget *= 2) {

            for (int i = 0; i < candidates.size() && winner == -1; i++) {
                Block block = candidates.get(i);
                long start = System.nanoTime();
                long end = attempts[i] + budget;

                while (attempts[i] < end && winner == -1) {
                    block.setNonce(attempts[i]);
                    attempts[i]++;
                    if (block.calculateHash().startsWith(prefix)) {
                        winner = i;
                    }
                }
                millis[i] += (System.nanoTime() - start) / 1000000;
            }
        }

        for (int i = 0; i < candidates.size(); i++) {
            System.out.println("Candidate " + (i + 1)
                    + " | tx=" + candidates.get(i).getTransactions().size()
                    + " | attempts= " + attempts[i]
                    + " | " + millis[i] + " ms"
                    + (i == winner ? " | Winner" : ""));

        }

        if (winner == -1) {
            System.out.println("No candidate found a solution within the limit.");
            return null;
        }

        Block winnerBlock = candidates.get(winner);
        String error = chain.addBlock(winnerBlock);
        if (error != null) {
            System.out.println("Block rejected: " + error);
            return null;
        }
        pool.removeAll(winnerBlock.getTransactions());
        return winnerBlock;
    }
}

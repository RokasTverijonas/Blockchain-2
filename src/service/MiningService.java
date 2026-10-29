package service;

import model.Block;

public class MiningService {
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
}

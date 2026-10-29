package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Blockchain {

    private final List<Block> chain = new ArrayList<>();
    private final Set<String> confirmedIds = new HashSet<>();
    private final int difficulty;
    private Balances balances;

    public Blockchain(Balances initial, int difficulty) {
        this.balances = initial.copy();
        this.difficulty = difficulty;

        chain.add(new Block("0".repeat(64), 0L, 1, 0, difficulty, List.of()));
    }

    public String addBlock(Block block) {
        String error = validate(block);
        if (error != null) {
            return error;
        }

        for (Transaction tx : block.getTransactions()) {
            balances.apply(tx);
            confirmedIds.add(tx.getId());
        }
        chain.add(block);
        return null;
    }

    public String validate(Block block) {
        Block lastBlock = last();
        String lastHash = lastBlock.calculateHash();
        if (!block.getPrevHash().equals(lastHash)) {
            return "bad prev_hash";
        }

        String expectedRoot = Block.computeTxRoot(block.getTransactions());
        if (!block.getTxRoot().equals(expectedRoot)) {
            return "bad tx_root";
        }

        String hash = block.calculateHash();
        String requiredPrefix = "0".repeat(difficulty);
        if (!hash.startsWith(requiredPrefix)) {
            return "Invalid PoW";
        }

        Balances temp = balances.copy();
        for (Transaction tx : block.getTransactions()) {
            if (confirmedIds.contains(tx.getId())) {
                return "duplicate transaction";
            }
            if (!temp.canApply(tx)) {
                return "transaction cannot be applied";
            }
            temp.apply(tx);
        }
        return null;
    }

    //helpers
    public Block last() {
        return chain.get(chain.size() - 1);
    }

    public Set<String> getConfirmedIds() {
        return confirmedIds;
    }

    public Balances getBalances() {
        return balances;
    }

    public int size() {
        return chain.size();
    }
}

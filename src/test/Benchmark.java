package test;

import java.util.ArrayList;
import java.util.List;

import model.Block;
import model.Transaction;
import service.MerkleTree;

public class Benchmark {
    static long sink = 0;

    public static void main(String[] args) {
        List<Transaction> txs = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Transaction t = new Transaction("a1", "b2", 10 + i, i,Transaction.computeId("a1", "b2", 10 + i, i));
            txs.add(t);
            ids.add(t.getId());
        }

        Block block = new Block("0".repeat(64), 1000L, 1, 0, 3, txs);

        System.out.println("root: " + MerkleTree.calculateRoot(ids));
        block.setNonce(12345);
        System.out.println("hash: " + block.calculateHash());

        runMerkle(ids);
        runMining(block);

        long[] merkle = new long[5];
        long[] mining = new long[5];
        for (int i = 0; i < 5; i++) {
            merkle[i] = runMerkle(ids);
            mining[i] = runMining(block);
        }

        print("Merkle", merkle);
        print("Mining", mining);
    }

    static long runMerkle(List<String> ids) {
        long start = System.nanoTime();
        for (int i = 0; i < 2000; i++) {
            sink += MerkleTree.calculateRoot(ids).length();
        }

        return (System.nanoTime() - start) / 1000000;
    }
    static long runMining(Block block) {
        long start = System.nanoTime();
        for (long i = 0; i < 200000; i++) {
            block.setNonce(i);
            if (block.calculateHash().startsWith("000")) {
                sink++;
            }

        }
        return (System.nanoTime() - start) / 1000000;
    }

    static void print(String name, long[] ms) {
        long sum = 0;
        String all = "";
        for (long v : ms) {
            sum += v;
            all += v + " ";
        }
        System.out.println(name + " (ms) " + all + " | avg " + (sum / ms.length));
    }
}

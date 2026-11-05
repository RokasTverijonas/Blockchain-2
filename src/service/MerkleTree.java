package service;

import java.util.ArrayList;
import java.util.List;

import hash.HashFunction;

public class MerkleTree {
    
    public static String calculateRoot(List<String> transactionsIds) {
        //genesis atvejis
        if (transactionsIds.isEmpty()) {
            return HashFunction.hash(new byte[0]);
        }

        List<String> currentLevel = new ArrayList<>(transactionsIds);
        
        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();

            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);

                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;

                int leftLen = left.length() / 2;
                byte[] combined = new byte[leftLen + right.length() / 2];
                decodeInto(left, combined, 0);
                decodeInto(right, combined, leftLen);

                nextLevel.add(HashFunction.hash(combined));
            }

            currentLevel = nextLevel;
        }
        return currentLevel.get(0);
    }

    private static void decodeInto(String hex, byte[] dest, int offset) {
        for (int i = 0; i < hex.length() / 2; i++) {
            dest[offset + i] = (byte) ((hexValue(hex.charAt(2 * i)) << 4)
        | hexValue(hex.charAt(2 * i + 1)));
        }
    }

    private static int hexValue(char c) {
        return c <= '9' ? c - '0' : c - 'a' + 10;
    }
}

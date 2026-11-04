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

                String right;

                if (i + 1 < currentLevel.size()) {
                    right = currentLevel.get(i + 1);
                } else {
                    right = left;
                }

                byte[] leftBytes = hexToBytes(left);
                byte[] rightBytes = hexToBytes(right);
                byte[] combinedBytes = combineBytes(leftBytes, rightBytes);

                nextLevel.add(HashFunction.hash(combinedBytes));
            }

            currentLevel = nextLevel;
        }
        return currentLevel.get(0);
    }

    private static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++ ) {
            
            int index = i * 2;

            bytes[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        } 
        return bytes;
    }

    private static byte[] combineBytes(byte[] left, byte[] right) {
        byte[] combined = new byte[left.length + right.length];

        System.arraycopy(left, 0, combined, 0, left.length);
        System.arraycopy(right, 0, combined, left.length, right.length);

        return combined;
    }
}

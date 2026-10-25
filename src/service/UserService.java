package service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.User;

public class UserService {
    public final static long seed = 283;

    public List<User> generateUsers(int count) {
        Random rand = new Random(seed);
        List<User> users = new ArrayList<>();
        Set<String> usedKeys = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String key;
            do {
                key = randomHex(rand, 16);
            } while (!usedKeys.contains(key));

            long balance = rand.nextInt(100, 1000001);
            users.add(new User(i, "User" + i, key, balance));
        }
        return users;
    }

    private String randomHex(Random rand, int len) {
        String chars = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rand.nextInt(16)));
        }
        return sb.toString();
    }
}

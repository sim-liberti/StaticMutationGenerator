package org.unina.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSelector {
    private static RandomSelector instance;
    private final Random random;

    private RandomSelector() {
        this.random = new Random();
    }

    private RandomSelector(long seed){
        this.random = new Random(seed);
    }

    public static void initialize() {
        if (instance == null) {
            instance = new RandomSelector();
        }
    }

    public static void initialize(long seed) {
        if (instance == null) {
            instance = new RandomSelector(seed);
        }
    }

    public static RandomSelector GetInstance() {
        if (instance == null) {
            throw new IllegalStateException("RandomSelector has not been initialized");
        }
        return instance;
    }

    public int GetRandomInt(int bound){
        return random.nextInt(bound);
    }

    public <T> T GetRandomItemFromCollection(Iterable<T> collection){
        if (collection == null){
            throw new IllegalArgumentException("Collection cannot be null");
        }

        List<T> items = new ArrayList<>();
        for (T item : collection){
            items.add(item);
        }

        if (items.isEmpty()){
            throw new IllegalArgumentException("Collection cannot be empty");
        }

        return items.get(random.nextInt(items.size()));
    }
}

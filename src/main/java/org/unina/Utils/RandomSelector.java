package org.unina.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSelector {
    private static RandomSelector instance;
    private final Random random;

    public RandomSelector() {
        this.random = new Random();
    }

    public RandomSelector(long seed){
        this.random = new Random(seed);
    }

    public static RandomSelector GetInstance() {
        if (instance == null) {
            throw new IllegalStateException("RandomSelector has not been initialized");
        }
        return instance;
    }

    public <T> T GetRandomItemFromCollection(Iterable<T> collection){
        if (collection == null){
            throw new IllegalArgumentException("Collection cannot be null");
        }

        List<T> items = new ArrayList<T>();
        for (T item : collection){
            items.add(item);
        }

        if (items.isEmpty()){
            throw new IllegalArgumentException("Collection cannot be empty");
        }

        return items.get(random.nextInt(items.size()));
    }
}

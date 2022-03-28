package net.andreinc.jperhash;
import java.util.*;
import java.util.function.Function;

public class ReadOnlyMap<K,V> {

    protected static final double LOAD_FACTOR = 1.0;
    protected static final int KEYS_PER_BUCKET = 1;
    protected static final int MAX_SEED = Integer.MAX_VALUE;

    private PHF phf;
    private ArrayList<V> values;
    private Function<K,byte[]> mapper;

    public static final <K,V> ReadOnlyMap<K,V> snapshot(Map<K, V> map, Function<K, byte[]> mapper, double loadFactor, int keysPerBucket, int maxSeed) {
        ReadOnlyMap<K,V> result = new ReadOnlyMap<>();
        result.phf = new PHF(loadFactor, keysPerBucket, maxSeed);
        result.phf.build(map.keySet(), mapper);
        result.values = new ArrayList<>(map.keySet().size());
        for (int i = 0; i < map.keySet().size(); i++) {
            result.values.add(null);
        }
        result.mapper = mapper;
        map.forEach((k, v) -> {
            int hash = result.phf.hash(mapper.apply(k));
            if (hash<=0) hash = -hash;
            result.values.set(hash, v);
        });
        return result;
    }

    public static final <K,V> ReadOnlyMap<K,V> snapshot(Map<K,V> map, Function<K, byte[]> mapper) {
        return snapshot(map, mapper, LOAD_FACTOR, KEYS_PER_BUCKET, MAX_SEED);
    }

    public static final <V> ReadOnlyMap<String, V> snapshot(Map<String, V> map) {
        return snapshot(map, String::getBytes);
    }

    public V get(K key) {
        int hash = phf.hash(mapper.apply(key));
        return values.get(hash);
    }

}

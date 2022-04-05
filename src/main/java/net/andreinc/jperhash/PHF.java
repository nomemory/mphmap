package net.andreinc.jperhash;

import org.apache.commons.codec.digest.MurmurHash3;

import java.util.*;
import java.util.function.Function;
import static java.util.Collections.sort;

public class PHF {

    private static int INIT_SEED = 0;
    private static int SIGN_MASK = 0xfffffff;

    protected double loadFactor; // If set to 1.0 -> perfect hash function
    protected int keysPerBucket;
    protected int maxSeed;
    protected int numBuckets;
    public int[] seeds;

    public PHF(double loadFactor, int keysPerBucket, int maxSeed) {
        if (loadFactor>1.0) {
            throw new IllegalArgumentException("Load factor should be <= 1.0");
        }
        this.loadFactor = loadFactor;
        this.keysPerBucket = keysPerBucket;
        this.maxSeed = maxSeed;
    }

    public <T> void build(Set<T> inputElements, Function<T, byte[]> objToByteArrayMapper) {

        int seedsLength = inputElements.size() / keysPerBucket;
        int numBuckets = (int) (inputElements.size() / loadFactor);
        this.numBuckets = numBuckets;

        // The seeds have to be calculated
        this.seeds = new int[seedsLength];

        // Fill the buckets with empty values initially
        ArrayList<byte[]> buckets[] = new ArrayList[seedsLength];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }

        // Adding elements to buckets
        inputElements.stream().map(objToByteArrayMapper).forEach(el -> {
            int index = (internalHash(el, INIT_SEED) % seedsLength);
            buckets[index].add(el);
        });

        // Sorting so we can start with buckets with the most items
        ArrayList<PHFBucket> sortedBuckets = new ArrayList<>();
        for (int i = 0; i < buckets.length; i++) {
            sortedBuckets.add(PHFBucket.from(buckets[i], i));
        }
        sort(sortedBuckets);

        // For each bucket we try to find a function for which the seed has no collisions
        BitSet occupied = new BitSet(numBuckets);
        int sortedBucketIdx = 0;
        PHFBucket bucket;
        Integer originalIndex;
        ArrayList<byte[]> bucketElements;
        Set<Integer> occupiedBucket;
        for(; sortedBucketIdx < sortedBuckets.size(); sortedBucketIdx++) {
            bucket = sortedBuckets.get(sortedBucketIdx);
            originalIndex = bucket.originalBucketIndex;
            bucketElements = bucket.elements;
            // If the buckets start to have a single element we don't have
            // to do any additional computation, we can break the loop
            if (bucketElements.size()==1) {
                break;
            }
            // For each seed
            int seedTry = INIT_SEED + 1;
            for (; seedTry < maxSeed; seedTry++) {
                occupiedBucket = new HashSet<>();
                // For each element in the bucket
                int eIdx = 0;
                for (; eIdx < bucketElements.size(); eIdx++) {
                    int hash = internalHash(bucketElements.get(eIdx), seedTry) % numBuckets;
                    if (occupied.get(hash) || occupiedBucket.contains(hash)) {
                        // Trying with this seed is not successful, we break the loop
                        // So we can try with another seed
                        break;
                    }
                    occupiedBucket.add(hash);
                }
                if (eIdx == bucketElements.size()) {
                    // In thise case elements per bucket displace well,
                    // we can add them to occupied and the seed to 'seeds'
                    occupiedBucket.forEach(occupied::set);
                    this.seeds[originalIndex] = seedTry;
                    break;
                }
            }
            // If the seed == SEED_MAX then we've failed constructing a Perfect Hash Function
            // This means we've exhausted the possible seeds
            if (seedTry==maxSeed) {
                throw new IllegalStateException("Cannot construct perfect hash function");
            }
        }
        // At this point only the buckets with one element remain, we need to add them
        // to seed, we continue the iteration
        int occupiedIdx = 0; // start from the first position
        for(; sortedBucketIdx < sortedBuckets.size(); sortedBucketIdx++) {
            bucket = sortedBuckets.get(sortedBucketIdx);
            originalIndex = bucket.originalBucketIndex;
            bucketElements = bucket.elements;
            if (bucketElements.size()==0) {
                break;
            }
            while(occupied.get(occupiedIdx)) {
                // increase position so we can find an empty slot
                occupiedIdx++;
            }
            occupied.set(occupiedIdx);
            this.seeds[originalIndex] = -(occupiedIdx)-1;
        }
    }

    public int hash(byte[] obj) {
        int seed = internalHash(obj, INIT_SEED) % seeds.length;
        if (seeds[seed]<0) {
            return -seeds[seed]-1;
        }
        int finalHash = internalHash(obj, seeds[seed]) % this.numBuckets;
        return finalHash;
    }

    protected static int internalHash(byte[] obj, int val) {
        return MurmurHash3.hash32x86(obj, 0, obj.length, val) & SIGN_MASK;
    }

    private static class PHFBucket implements Comparable<PHFBucket> {
        ArrayList<byte[]> elements;
        int originalBucketIndex;

        static PHFBucket from(ArrayList<byte[]> bucket, int originalIndex) {
            PHFBucket result = new PHFBucket();
            result.elements = bucket;
            result.originalBucketIndex = originalIndex;
            return result;
        }

        @Override
        public int compareTo(PHFBucket o) {
            return o.elements.size() - this.elements.size();
        }

        @Override
        public String toString() {
            return "Bucket{" +
                    "elements.size=" + elements.size() +
                    ", originalBucketIndex=" + originalBucketIndex +
                    '}';
        }
    }
}


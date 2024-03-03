package com.barribob.mm.util;

import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class ModRandom {
    private static Random rand = new Random();

    /**
     * Gets a random float value with expected value 0 and the min and max range
     *
     * @param range The range of the min and max value
     * @return
     */
    public static float getFloat(float range) {
        return rand.nextFloat() * randSign() * range;
    }

    /**
     * Chooses a random integer between the min [inclusive] and the max [exclusive]
     *
     * @param min
     * @param max
     * @return
     */
    public static int range(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("minimum is greater than maximum");
        }
        return min + rand.nextInt(max - min);
    }

    /**
     * Returns a vector where each value is a random float between -0.5 and 0.5
     */
    public static Vec3 randVec() {
        return new Vec3(getFloat(0.5f), getFloat(0.5f), getFloat(0.5f));
    }

    public static Vec3 randFlatVec(Vec3 plane) {
        return randVec().cross(plane).normalize();
    }

    /**
     * Returns a vector where each value is a gaussian of mean 0 and std dev 1
     */
    public static Vec3 gaussVec() {
        return new Vec3(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
    }

    /**
     * Produces 1 or -1 with equal probablity
     */
    public static int randSign() {
        return rand.nextInt(2) == 0 ? 1 : -1;
    }

    /**
     * Choose a random element in the array
     *
     * @param array
     * @return
     */
    public static <T> T choice(T[] array) {
        Random rand = new Random();
        return choice(array, rand);
    }

    public static <T> T choice(T[] array, Random rand) {
        int i = rand.nextInt(array.length);
        return array[i];
    }

    public static <T> RandomCollection<T> choice(List<T> items, RandomSource rand, double[] weights) {
        return (RandomCollection<T>) choice(items.toArray(), rand, weights);
    }

    public static <T> RandomCollection<T> choice(T[] array, RandomSource rand, double[] weights) {
        if (array.length != weights.length) {
            throw new IllegalArgumentException("Lengths of items and weights arrays inequal");
        }

        RandomCollection<T> weightedRandom = new RandomCollection<T>(rand);
        for (int i = 0; i < array.length; i++) {
            weightedRandom.add(weights[i], array[i]);
        }

        return weightedRandom;
    }

    public static <T> RandomCollection<T> choice(T[] array, RandomSource rand, int[] weights) {
        double[] converted = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            converted[i] = weights[i];
        }
        return choice(array, rand, converted);
    }

    /**
     * Weighted random collection taken from
     * https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
     */
    public static class RandomCollection<E> {
        private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
        private final RandomSource random;
        private double total = 0;

        public RandomCollection() {
            this(RandomSource.create());
        }

        public RandomCollection(RandomSource random) {
            this.random = random;
        }

        public RandomCollection<E> add(double weight, E result) {
            if(weight <= 0) return this;
            if (Double.isNaN(weight) || Double.isInfinite(weight))
                throw new IllegalArgumentException("The weight for random collection is invalid: " + weight);
            total += weight;
            map.put(total, result);
            return this;
        }

        public E next() {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }
}

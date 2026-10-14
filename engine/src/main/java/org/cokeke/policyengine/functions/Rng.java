package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.Stream;

import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

/** The only source of randomness. Derived, never stored: (seed, month, stream) fixes the sequence. */
public final class Rng {
    private Rng() {}

    public static RandomGenerator rng(long seed, int month, Stream stream) {
        long h = mix64(seed);
        h = mix64(h ^ (0x9E3779B97F4A7C15L * (month + 1L)));
        h = mix64(h ^ (0xD1B54A32D192ED03L * (stream.ordinal() + 1L)));
        return new SplittableRandom(h);
    }

    static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}

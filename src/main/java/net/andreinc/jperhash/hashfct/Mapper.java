package net.andreinc.jperhash.hashfct;

import java.util.function.Function;

public class Mapper {
    public static <T> Function<T, byte[]> objectMap() {
        return (o) -> {
            int hash = o.hashCode();
            return new byte[] {
                    (byte)((hash >> 24) & 0xff),
                    (byte)((hash >> 16) & 0xff),
                    (byte)((hash >> 8) & 0xff),
                    (byte)((hash >> 0) & 0xff),
            };
        };
    }
}

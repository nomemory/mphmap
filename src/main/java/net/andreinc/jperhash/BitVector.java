package net.andreinc.jperhash;

import java.util.Set;

class BitVector {

    private static final Integer BITS_IN_INT = 32;
    private int[] memory;

    public BitVector(int numBits) {
        int memorySize = numBits / BITS_IN_INT;
        if (numBits%BITS_IN_INT!=0) {
            memorySize++;
        }
        this.memory = new int[memorySize];
    }

    public boolean get(int index) {
        int intOffset = index / BITS_IN_INT;
        int bitOffset = index & (BITS_IN_INT-1);
        return ((this.memory[intOffset]>>bitOffset) & 1) == 1 ? true : false;
    }

    public void set(int index, boolean val) {
        int intOffset = index / BITS_IN_INT;
        int bitOffset = index & (BITS_IN_INT-1);
        if (val) {
            this.memory[intOffset] |= 1 << bitOffset;
        } else {
            this.memory[intOffset] &= ~(1<<bitOffset);
        }
    }

    public void setTrue(int index) {
        int intOffset = index / BITS_IN_INT;
        int bitOffset = index & (BITS_IN_INT-1);
        this.memory[intOffset] |= 1 << bitOffset;
    }

    public void setTrue(Set<Integer> indexes) {
        indexes.forEach(this::setTrue);
    }
}

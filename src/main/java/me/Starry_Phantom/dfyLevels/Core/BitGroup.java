package me.Starry_Phantom.dfyLevels.Core;

import org.jetbrains.annotations.NotNull;

public class BitGroup {
    private final String id, prefix;
    private final int weight, bitAmount;

    public BitGroup(@NotNull String id, @NotNull String prefix, int weight, int bitAmount) {
        this.id = id;
        this.prefix = prefix;
        this.weight = weight;
        this.bitAmount = bitAmount;
    }

    public String getID() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getWeight() {
        return weight;
    }

    public int getBitAmount() {
        return bitAmount;
    }
}

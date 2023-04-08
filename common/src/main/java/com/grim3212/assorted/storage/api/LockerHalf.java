package com.grim3212.assorted.storage.api;

import net.minecraft.util.StringRepresentable;

public enum LockerHalf implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom"),
    SINGLE("single");

    private final String name;

    private LockerHalf(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public LockerHalf getOpposite() {
        LockerHalf half;
        switch (this) {
            case SINGLE:
                half = SINGLE;
                break;
            case TOP:
                half = BOTTOM;
                break;
            case BOTTOM:
                half = TOP;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return half;
    }
}

package com.evil.waveanimationview;

public enum WaveDensity {
    //只支持3个档
    HIGH(4), MID(3), LOW(2);

    private int value;

    WaveDensity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
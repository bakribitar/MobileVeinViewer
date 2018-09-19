package com.serenegiant.opencvwithuvc;

public class ParamsDto {
    private int AdaptiveThresholdC;
    private int AdaptiveThresholdKernelSize;
    private int AdaptiveThresholdBlockSize;


    public int getAdaptiveThresholdC() {
        return AdaptiveThresholdC;
    }

    public void setAdaptiveThresholdC(int adaptiveThresholdC) {
        AdaptiveThresholdC = adaptiveThresholdC;
    }

    public int getAdaptiveThresholdKernelSize() {
        return AdaptiveThresholdKernelSize;
    }

    public void setAdaptiveThresholdKernelSize(int adaptiveThresholdKernelSize) {
        AdaptiveThresholdKernelSize = adaptiveThresholdKernelSize;
    }

    public int getAdaptiveThresholdBlockSize() {
        return AdaptiveThresholdBlockSize;
    }

    public void setAdaptiveThresholdBlockSize(int adaptiveThresholdBlockSize) {
        AdaptiveThresholdBlockSize = adaptiveThresholdBlockSize;
    }
}
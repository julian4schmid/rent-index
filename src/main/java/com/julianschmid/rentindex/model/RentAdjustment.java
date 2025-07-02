package com.julianschmid.rentindex.model;

public class RentAdjustment {

    private VpiRecord oldVpi;
    private VpiRecord newVpi;
    private boolean willAdjustRent;
    private double selfSetRentLimit;

    public RentAdjustment(VpiRecord oldVpi, VpiRecord newVpi) {
        this.oldVpi = oldVpi;
        this.newVpi = newVpi;
    }

    public void setWillAdjustRent(boolean willAdjustRent) {
        this.willAdjustRent = willAdjustRent;
    }

    public VpiRecord getOldVpi() {
        return oldVpi;
    }

    public VpiRecord getNewVpi() {
        return newVpi;
    }

    public boolean isWillAdjustRent() {
        return willAdjustRent;
    }

    public double getSelfSetRentLimit() {
        return selfSetRentLimit;
    }

    public void setSelfSetRentLimit(double limit) {
        this.selfSetRentLimit = limit;
    }

    @Override
    public String toString() {
        return "RentAdjustment{" +
                "newVpi=" + newVpi +
                ", oldVpi=" + oldVpi +
                '}';
    }


}

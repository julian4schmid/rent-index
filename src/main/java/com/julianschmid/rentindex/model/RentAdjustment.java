package com.julianschmid.rentindex.model;

public class RentAdjustment {

    private VpiRecord oldVpi;
    private VpiRecord newVpi;
    private double newRent;

    public RentAdjustment(VpiRecord oldVpi, VpiRecord newVpi) {
        this.oldVpi = oldVpi;
        this.newVpi = newVpi;
    }
}

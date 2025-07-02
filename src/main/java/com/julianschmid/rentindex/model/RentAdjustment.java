package com.julianschmid.rentindex.model;

import com.julianschmid.rentindex.util.MathUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RentAdjustment {

    private final VpiRecord oldVpi;
    private final VpiRecord newVpi;

    private boolean willAdjustRent;
    private double selfSetRentLimit;
    private final String percentPossible;
    private String percentIncrease;
    private double NewRentPerSqm;
    private double newRent;
    private double rentDifference;

    public RentAdjustment(VpiRecord oldVpi, VpiRecord newVpi) {
        this.oldVpi = oldVpi;
        this.newVpi = newVpi;
        this.percentPossible = MathUtil.formatPercentChange(newVpi.value() / oldVpi.value());
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

    public double getNewRentPerSqm() {
        return NewRentPerSqm;
    }

    public double getNewRent() {
        return newRent;
    }

    public void setNewRentPerSqm(double newRentPerSqm) {
        NewRentPerSqm = newRentPerSqm;
    }

    public void setNewRent(double newRent) {
        this.newRent = newRent;
    }

    public void setPercentIncrease(String percentIncrease) {
        this.percentIncrease = percentIncrease;
    }

    public String getPercentPossible() {
        return percentPossible;
    }

    public String getPercentIncrease() {
        return percentIncrease;
    }

    public double getRentDifference() {
        return rentDifference;
    }

    public void setRentDifference(double rentDifference) {
        this.rentDifference = rentDifference;
    }

    @Override
    public String toString() {
        return "RentAdjustment{" +
                "oldVpi=" + oldVpi +
                ", newVpi=" + newVpi +
                ", willAdjustRent=" + willAdjustRent +
                ", selfSetRentLimit=" + selfSetRentLimit +
                ", percentPossible='" + percentPossible + '\'' +
                ", percentIncrease='" + percentIncrease + '\'' +
                ", NewRentPerSqm=" + NewRentPerSqm +
                ", newRent=" + newRent +
                '}';
    }


}

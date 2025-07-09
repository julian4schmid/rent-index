package com.julianschmid.rentindex.model;

import com.julianschmid.rentindex.util.MathUtil;

public class RentAdjustment {

    private final VpiRecord oldVpi;
    private final VpiRecord newVpi;

    private boolean willAdjustRent;
    private double selfSetRentLimit;
    private final double percentPossible;
    private double percentIncrease;
    private double NewRentPerSqm;
    private int newRent;
    private double rentDifference;
    private String reason;

    public RentAdjustment(VpiRecord oldVpi, VpiRecord newVpi) {
        this.oldVpi = oldVpi;
        this.newVpi = newVpi;
        this.percentPossible = MathUtil.calculatePercentChange(newVpi.value() / oldVpi.value());
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

    public void setNewRent(int newRent) {
        this.newRent = newRent;
    }

    public void setPercentIncrease(double percentIncrease) {
        this.percentIncrease = percentIncrease;
    }

    public double getPercentPossible() {
        return percentPossible;
    }

    public double getPercentIncrease() {
        return percentIncrease;
    }

    public double getRentDifference() {
        return rentDifference;
    }

    public void setRentDifference(double rentDifference) {
        this.rentDifference = rentDifference;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
                ", rentDifference=" + rentDifference +
                ", reason='" + reason + '\'' +
                '}';
    }


}

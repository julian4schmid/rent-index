package com.julianschmid.rentindex.model;

import java.util.List;

public class Renter {
    private final List<Tenant> tenants;
    private final Apartment apartment;
    private final String suspended;
    private final PreviousRentAdjustment previousIncrease;

    private final double operatingCosts;
    private final double heatingCosts;

    private double newRent;

    public Renter(List<Tenant> tenants,
                  Apartment apartment,
                  String suspended,
                  PreviousRentAdjustment previousIncrease,
                  double operatingCosts,
                  double heatingCosts) {
        this.tenants = tenants;
        this.apartment = apartment;
        this.suspended = suspended;
        this.previousIncrease = previousIncrease;
        this.operatingCosts = operatingCosts;
        this.heatingCosts = heatingCosts;
        this.newRent = previousIncrease.rent(); // default to last known
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public String getSuspended() {
        return suspended;
    }

    public PreviousRentAdjustment getPreviousIncrease() {
        return previousIncrease;
    }

    public double getOperatingCosts() {
        return operatingCosts;
    }

    public double getHeatingCosts() {
        return heatingCosts;
    }

    public double getNewRent() {
        return newRent;
    }

    public void setNewRent(double newRent) {
        this.newRent = newRent;
    }

    @Override
    public String toString() {
        return "Renter{" +
                "tenants=" + tenants +
                ", apartment=" + apartment +
                ", suspended='" + suspended + '\'' +
                ", previousIncrease=" + previousIncrease +
                ", operatingCosts=" + operatingCosts +
                ", heatingCosts=" + heatingCosts +
                ", newRent=" + newRent +
                '}';
    }
}
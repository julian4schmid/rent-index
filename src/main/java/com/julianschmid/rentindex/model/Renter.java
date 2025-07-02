package com.julianschmid.rentindex.model;

import java.util.List;

public class Renter {
    private final List<Tenant> tenants;
    private final Apartment apartment;
    private final String suspended;
    private final PreviousRentAdjustment previousAdjustment;

    private final double operatingCosts;
    private final double heatingCosts;

    private RentAdjustment rentAdjustment;

    public Renter(List<Tenant> tenants,
                  Apartment apartment,
                  String suspended,
                  PreviousRentAdjustment previousIncrease,
                  double operatingCosts,
                  double heatingCosts) {
        this.tenants = tenants;
        this.apartment = apartment;
        this.suspended = suspended;
        this.previousAdjustment = previousIncrease;
        this.operatingCosts = operatingCosts;
        this.heatingCosts = heatingCosts;
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

    public PreviousRentAdjustment getPreviousAdjustment() {
        return previousAdjustment;
    }

    public double getOperatingCosts() {
        return operatingCosts;
    }

    public double getHeatingCosts() {
        return heatingCosts;
    }

    public void setRentAdjustment(RentAdjustment rentAdjustment) {
        this.rentAdjustment = rentAdjustment;
    }

    @Override
    public String toString() {
        return "Renter{" +
                "tenants=" + tenants +
                ", apartment=" + apartment +
                ", suspended='" + suspended + '\'' +
                ", previousAdjustment=" + previousAdjustment +
                ", operatingCosts=" + operatingCosts +
                ", heatingCosts=" + heatingCosts +
                '}';
    }
}
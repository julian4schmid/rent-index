package com.julianschmid.rentindex.model;

import java.util.List;

public class Renter {
    private final List<Person> renters;
    private final Apartment apartment;
    private final PreviousRentIncrease previousIncrease;

    private final double operatingCosts;
    private final double heatingCosts;

    private double newRent;

    public Renter(List<Person> renters,
                  Apartment apartment,
                  PreviousRentIncrease previousIncrease,
                  double operatingCosts,
                  double heatingCosts) {
        this.renters = renters;
        this.apartment = apartment;
        this.previousIncrease = previousIncrease;
        this.operatingCosts = operatingCosts;
        this.heatingCosts = heatingCosts;
        this.newRent = previousIncrease.rent(); // default to last known
    }

    public List<Person> getRenters() {
        return renters;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public PreviousRentIncrease getPreviousIncrease() {
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

}
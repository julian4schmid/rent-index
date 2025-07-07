package com.julianschmid.rentindex.service;

import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.util.*;

import java.util.List;
import java.util.Properties;

public final class RentAdjustService {

    public static boolean adjustmentReasonable(Renter renter) {
        VpiRecord oldVpi = renter.getRentAdjustment().getOldVpi();
        VpiRecord newVpi = renter.getRentAdjustment().getNewVpi();
        final List<String> MONTH_ORDER = DateUtil.MONTH_ORDER;
        int oldMonth = MONTH_ORDER.indexOf(oldVpi.month());
        int newMonth = MONTH_ORDER.indexOf(newVpi.month());
        RentAdjustment adjustment = renter.getRentAdjustment();

        // last adjustment at least 12 months ago
        if (oldVpi.year() >= newVpi.year()
                || (oldVpi.year() + 1 == newVpi.year() && oldMonth > newMonth)) {
            adjustment.setReason("< 12 Monate");
            return false;
        }

        // adjust always if flagged as MAX
        if (renter.getSuspended().equals("MAX") && oldVpi.value() < newVpi.value()) {
            renter.getRentAdjustment().setSelfSetRentLimit(1000);
            return true;
        }

        // rent adjustment suspended
        if (!renter.getSuspended().isEmpty()) {
            adjustment.setReason("ausgesetzt");
            return false;
        }

        // at least a positive adjustment of 2%
        if (newVpi.value() / oldVpi.value() < 1.02) {
            adjustment.setReason("< 2 %");
            return false;
        }

        // at least a positive adjustment of 2% considering the limit
        double limit = calculateSelfSetRentLimit(renter);
        if (limit / renter.getPreviousAdjustment().rentPerSqm() < 1.02) {
            adjustment.setReason("limit");
            return false;
        }

        return true;
    }

    public static double calculateSelfSetRentLimit(Renter renter) {
        Apartment apartment = renter.getApartment();
        Properties props = PropertiesUtil.getProperties("limits.properties");

        double limit = Double.parseDouble(props.getProperty("building." + apartment.building()));
        limit += Double.parseDouble(props.getProperty(apartment.condition()));
        if (apartment.heating().equals("Gasöfen")) {
            limit += Double.parseDouble(props.getProperty("gasofen"));
        }
        limit += Double.parseDouble(props.getProperty("balkon." + apartment.balcony()));

        renter.getRentAdjustment().setSelfSetRentLimit(limit);
        return limit;
    }


    public static void adjustRent(Renter renter) {
        if (adjustmentReasonable(renter)) {
            RentAdjustment adjustment = renter.getRentAdjustment();
            adjustment.setWillAdjustRent(true);

            double oldVpiVal = adjustment.getOldVpi().value();
            double newVpiVal = adjustment.getNewVpi().value();
            double newRentPerSqm = renter.getPreviousAdjustment().rentPerSqm();
            newRentPerSqm *= newVpiVal / oldVpiVal;
            newRentPerSqm = Math.min(newRentPerSqm, adjustment.getSelfSetRentLimit());

            adjustment.setNewRentPerSqm(MathUtil.roundWithDecimals(newRentPerSqm, 2));
            adjustment.setNewRent((int) Math.floor(newRentPerSqm * renter.getApartment().sqm()));
            adjustment.setRentDifference(adjustment.getNewRent() - renter.getPreviousAdjustment().rent());
            adjustment.setPercentIncrease(MathUtil.formatPercentChange(
                    adjustment.getNewRent() / renter.getPreviousAdjustment().rent()));
        }
    }

    public static void adjustRent(List<Renter> renters) {
        for (Renter renter : renters) {
            adjustRent(renter);
        }
    }

}

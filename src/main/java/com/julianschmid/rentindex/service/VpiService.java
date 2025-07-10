package com.julianschmid.rentindex.service;

import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.util.DateUtil;

import java.util.*;

public final class VpiService {
    private VpiService() {
    }

    public static VpiRecord getCurrentVpi(List<VpiRecord> sortedRecords) {
        return sortedRecords.getLast();
    }

    public static VpiRecord getPreviousVpi(List<VpiRecord> sortedRecords, PreviousRentAdjustment previousAdj) {
        List<String> MONTH_ORDER = DateUtil.MONTH_ORDER;
        Comparator<VpiRecord> comp = Comparator
                .comparingInt(VpiRecord::year)
                .thenComparing(record -> MONTH_ORDER.indexOf(record.month()));

        VpiRecord target = new VpiRecord(previousAdj.year(), previousAdj.month(), 0.0);
        int index = Collections.binarySearch(sortedRecords, target, comp);

        return sortedRecords.get(index);
    }

    public static void addVpiToRenters(List<Renter> renters, List<VpiRecord> sortedRecords) {
        System.out.println("attach VPI to renter");

        VpiRecord curVpi = getCurrentVpi(sortedRecords);
        for (Renter renter : renters) {
            VpiRecord oldVpi = getPreviousVpi(sortedRecords, renter.getPreviousAdjustment());
            renter.setRentAdjustment(new RentAdjustment(oldVpi, curVpi));
        }
    }
}

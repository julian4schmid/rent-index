package com.julianschmid.rentindex;

import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.util.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {

            /*
            List<VpiRecord> records = VpiDataLoader.loadSortedVpiRecords("vpi.csv");
            records.forEach(System.out::println);
            */
        List<Renter> renters = RenterDataLoader.loadRenters("real/Indexmieten_Übersicht.xlsx");
        renters.forEach(System.out::println);

    }
}
package com.julianschmid.rentindex;

import com.julianschmid.rentindex.util.VpiDataLoader;
import com.julianschmid.rentindex.model.VpiRecord;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<VpiRecord> records = VpiDataLoader.loadSortedVpiRecords("vpi.csv");
            records.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package com.julianschmid.rentindex.service;

import com.julianschmid.rentindex.model.VpiRecord;
import com.julianschmid.rentindex.util.DateUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class VpiDataLoader {

    private static final List<String> MONTH_ORDER = DateUtil.MONTH_ORDER;

    private static final char SEPARATOR = ';';
    private static final String VALUE_UNIT_COLUMN = "value_unit";
    private static final String VALUE_COLUMN = "value";
    private static final String YEAR_COLUMN = "time";
    private static final String MONTH_COLUMN = "1_variable_attribute_label";

    private VpiDataLoader() {
    }

    public static List<VpiRecord> load(String csvResourcePath) throws IOException {
        List<VpiRecord> records = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setDelimiter(SEPARATOR)
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        try (
                InputStream is = VpiDataLoader.class.getClassLoader().getResourceAsStream(csvResourcePath);
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, csvFormat)
        ) {
            for (CSVRecord csvRecord : parser) {
                if ("%".equals(csvRecord.get(VALUE_UNIT_COLUMN)) || "...".equals(csvRecord.get(VALUE_COLUMN))) {
                    continue; // Skip percentage rows
                }

                int year = Integer.parseInt(csvRecord.get(YEAR_COLUMN));
                String month = csvRecord.get(MONTH_COLUMN);
                double value = Double.parseDouble(csvRecord.get(VALUE_COLUMN).replace(',', '.'));

                records.add(new VpiRecord(year, month, value));
            }
        }

        return records.stream()
                .sorted(Comparator
                        .comparingInt(VpiRecord::year)
                        .thenComparing(record -> MONTH_ORDER.indexOf(record.month())))
                .collect(Collectors.toList());
    }
}

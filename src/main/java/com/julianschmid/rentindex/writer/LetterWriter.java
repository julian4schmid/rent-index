package com.julianschmid.rentindex.writer;

import com.julianschmid.rentindex.model.Landlord;
import com.julianschmid.rentindex.model.RentAdjustment;
import com.julianschmid.rentindex.model.Renter;
import com.julianschmid.rentindex.model.Tenant;
import com.julianschmid.rentindex.util.DateUtil;
import com.julianschmid.rentindex.util.ExcelUtil;
import com.julianschmid.rentindex.util.MathUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LetterWriter {
    public static void createLetters(String filename, List<Renter> renters) throws IOException {
        String path = "template";
        String templateFile = "Anschreiben_Vorlage.xlsx";

        for (Renter renter : renters) {
            // create letter only if we adjust the rent
            if (renter.getRentAdjustment().isWillAdjustRent()) {
                try (InputStream is = LetterWriter.class.getClassLoader().getResourceAsStream(path + filename);
                     Workbook workbook = new XSSFWorkbook(is)) {

                    Sheet sheet = workbook.getSheetAt(0);

                    // general information
                    List<Landlord> landlords = renter.getApartment().building().landlords();
                    Landlord landlord1 = landlords.getFirst();
                    Landlord landlord2 = landlords.size() > 1 ? landlords.get(1) : null;
                    Tenant tenant1 = renter.getTenants().getFirst();
                    Tenant tenant2 = renter.getTenants().size() > 1 ? renter.getTenants().get(1) : null;
                    RentAdjustment adjustment = renter.getRentAdjustment();

                    String outputFilename = String.format(
                            "Mieterhöhung_%d_%s_%s",
                            DateUtil.getCurrentYear(),
                            renter.getApartment().building().id(),
                            tenant1.salutation()
                    );
                    if (tenant2 != null) {
                        outputFilename += String.format("%s.xlsx", tenant2.salutation());
                    }


                    // landlord information
                    ExcelUtil.setValueByCellRef(sheet, "A1", landlord1.name());
                    ExcelUtil.setValueByCellRef(sheet, "A2", landlord1.street());
                    ExcelUtil.setValueByCellRef(sheet, "A3", landlord1.zipcode());
                    ExcelUtil.setValueByCellRef(sheet, "A4", landlord1.email());
                    ExcelUtil.setValueByCellRef(sheet, "A6",
                            landlord1.name() + ", " + landlord1.street() + ", " + landlord1.zipcode());

                    if (landlord2 != null) {
                        ExcelUtil.setValueByCellRef(sheet, "D1", landlord2.name());
                        ExcelUtil.setValueByCellRef(sheet, "D2", landlord2.street());
                        ExcelUtil.setValueByCellRef(sheet, "D3", landlord2.zipcode());
                        ExcelUtil.setValueByCellRef(sheet, "D4", landlord2.email());
                    }

                    // tenants
                    String letterhead = tenant1.woman() ? "Frau " : "Herrn ";
                    if (tenant2 != null) {
                        letterhead += tenant2.woman() ? "u. Frau" : "u. Herrn";
                    }
                    ExcelUtil.setValueByCellRef(sheet, "A8", letterhead);
                    ExcelUtil.setValueByCellRef(sheet, "A9", tenant1.fullNames());
                    ExcelUtil.setValueByCellRef(sheet, "A10", renter.getApartment().building().street());
                    ExcelUtil.setValueByCellRef(sheet, "A12", renter.getApartment().building().zipcode());

                    // location and date
                    ExcelUtil.setValueByCellRef(sheet, "G14",
                            landlords.getFirst().zipcode().split(" ")[1]);
                    ExcelUtil.setValueByCellRef(sheet, "H14", DateUtil.getDateToday());

                    // salutation
                    StringBuilder salutation = new StringBuilder(tenant1.woman() ? "Sehr geehrte Frau " : "Sehr geehrter Herr ");
                    salutation.append(tenant1.salutation());
                    if (tenant2 != null) {
                        salutation.append(tenant2.woman() ? ", Sehr geehrte Frau " : ", Sehr geehrter Herr ");
                        salutation.append(tenant2.salutation());
                    }
                    ExcelUtil.setValueByCellRef(sheet, "A18", salutation.toString());

                    // calculations
                    ExcelUtil.setValueByCellRef(sheet, "G27", adjustment.getOldVpi().value());
                    ExcelUtil.setValueByCellRef(sheet, "G30", adjustment.getNewVpi().value());
                    String sentenceOldVpi = String.format(
                            "Der Preisindex im Monat %s %s (letzter veröffentlichter Indexstand)",
                            adjustment.getOldVpi().month(),
                            adjustment.getOldVpi().year());
                    ExcelUtil.setValueByCellRef(sheet, "A29", sentenceOldVpi);
                    ExcelUtil.setValueByCellRef(sheet, "A36", adjustment.getNewVpi().value());
                    ExcelUtil.setValueByCellRef(sheet, "C36", adjustment.getOldVpi().value());

                    ExcelUtil.setValueByCellRef(sheet, "E36", adjustment.getPercentPossible());

                    // not maximum
                    if (adjustment.getPercentIncrease() < adjustment.getPercentPossible()) {
                        String notMaximumIncrease = String.format(
                                "Wir haben beschlossen, die Miete stattdessen um %s zu erhöhen.",
                                MathUtil.formatPercentChange(adjustment.getPercentIncrease()));
                        ExcelUtil.setValueByCellRef(sheet, "A37", notMaximumIncrease);
                    }
                    ExcelUtil.setValueByCellRef(sheet, "H39", renter.getPreviousAdjustment().rent());
                    ExcelUtil.setValueByCellRef(sheet, "C41", adjustment.getPercentIncrease());
                    ExcelUtil.setValueByCellRef(sheet, "F41", adjustment.getRentDifference());
                    ExcelUtil.setValueByCellRef(sheet, "H41", adjustment.getNewRent());

                    // additional costs
                    double costs = renter.getOperatingCosts() + renter.getHeatingCosts();
                    if (renter.getHeatingCosts() > 0) {
                        String mentionHeating = "Zuzüglich Heiz- und Betriebskostenvorauszahlung";
                        String mentionHeating2 = "Ihre künftige Miete inkl. Heiz - und Betriebskostenvorauszahlung:";
                        ExcelUtil.setValueByCellRef(sheet, "A42", mentionHeating);
                        ExcelUtil.setValueByCellRef(sheet, "A44", mentionHeating2);
                    }
                    ExcelUtil.setValueByCellRef(sheet, "H44", adjustment.getNewRent() + costs);

                    // new rent start
                    String newMonth = DateUtil.getMonth(2);
                    int newYear = DateUtil.getYear(2);
                    String newRentStart = String.format(
                            "entrichten (§ 557b Abs. 3 S. 3 BGB). Das bedeutet ab %s %d.",
                            newMonth,
                            newYear);
                    ExcelUtil.setValueByCellRef(sheet, "A47", newRentStart);

                    String landlordsString = landlord1.name();
                    if (landlord2 != null) {
                        landlordsString += " und " + landlord2.name();
                    }
                    ExcelUtil.setValueByCellRef(sheet, "A52", landlordsString);
                }
            }
        }

    }
}
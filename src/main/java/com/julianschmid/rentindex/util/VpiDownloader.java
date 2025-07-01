package com.julianschmid.rentindex.util;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class VpiDownloader {

    private static final String BASE_URL = "https://www-genesis.destatis.de/genesisWS/rest/2020/";
    private final String username;  // API token
    private final HttpClient httpClient;

    public VpiDownloader(String username) {
        this.username = username;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Downloads the zipped flat CSV file for the given table and start year,
     * saves the zip to disk, unzips it and returns the CSV filename.
     *
     * @param tableName Genesis table code, e.g. "61111-0002"
     * @param startYear start year for data, e.g. 1991
     * @param outputDir directory where zip and extracted CSV will be saved
     * @return filename of the extracted CSV file (including path)
     * @throws IOException          on I/O errors
     * @throws InterruptedException if HTTP request is interrupted
     */
    public String downloadAndExtractCsv(String tableName, int startYear, String outputDir)
            throws IOException, InterruptedException {

        // Prepare POST data parameters with flat CSV format and compression
        String postData = "name=" + URLEncoder.encode(tableName, StandardCharsets.UTF_8)
                + "&startyear=" + startYear
                + "&format=ffcsv"
                + "&compress=true"
                + "&language=de";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "data/tablefile"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("username", username)
                .header("password", "")
                .POST(BodyPublishers.ofString(postData))
                .build();

        // Send request and get response as bytes
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to download data. HTTP status: " + response.statusCode()
                    + " Response: " + new String(response.body(), StandardCharsets.UTF_8));
        }


        // Save ZIP file
        Path outDir = Paths.get(outputDir);
        String zipFileName = tableName + ".zip";
        Path zipFilePath = outDir.resolve(zipFileName);
        Files.write(zipFilePath, response.body());

        // Unzip and get extracted CSV filename
        String extractedCsv = unzipSingleCsv(zipFilePath.toString(), outputDir);

        // Delete the zip file after extraction
        Files.delete(zipFilePath);

        return extractedCsv;
    }

    /**
     * Unzips the given ZIP file and returns the path of the extracted CSV file.
     * Assumes exactly one CSV file inside the ZIP.
     *
     * @param zipFilePath full path to ZIP file
     * @param destDir     directory to extract into
     * @return extracted CSV file path
     * @throws IOException if an I/O error occurs
     */
    private String unzipSingleCsv(String zipFilePath, String destDir) throws IOException {
        String extractedCsvPath = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zis.getNextEntry(); // Just get the first entry

            if (entry == null) {
                throw new IOException("ZIP archive is empty.");
            }
            if (entry.isDirectory()) {
                throw new IOException("Expected a CSV file but found a directory in ZIP.");
            }

            if (!entry.getName().toLowerCase().endsWith(".csv")) {
                throw new IOException("Expected a CSV file but found: " + entry.getName());
            }

            File extractedFile = new File(destDir, "vpi.csv");  // Rename here
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(extractedFile))) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = zis.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                }
            }

            extractedCsvPath = extractedFile.getAbsolutePath();
            zis.closeEntry();
        }
        return extractedCsvPath;
    }

}

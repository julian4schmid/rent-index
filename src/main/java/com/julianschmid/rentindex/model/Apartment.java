package com.julianschmid.rentindex.model;

public record Apartment(double sqm, String balcony, String position, Building building, String condition,
                        String heating) {
}

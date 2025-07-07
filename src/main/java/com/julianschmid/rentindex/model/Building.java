package com.julianschmid.rentindex.model;

import java.util.List;

public record Building(String id, String street, String zipcode, List<Landlord> landlords) {
}

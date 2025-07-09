# Index Rent Adjustment Tool

## Overview
This Java application automates the process of:
- Downloading VPI (consumer price index) data from the GENESIS API (Destatis)
- Loading renter and landlord data from Excel and property files
- Calculating rent adjustments based on VPI and self set limits
- Generating an Excel summary 
- Generating letters based on an Excel template
- Hiding real data locally, providing sample data

---


## Project Structure

```
src/main/java/com.julianschmid.rentindex/  
├── api/             VPI data download  
├── loader/          Data loader from Excel, CSV or properties  
├── model/           Data structures  
├── service/         Business logic  
├── util/            Helpers  
├── writer/          Excel Writer  
└── Main.java        Entry point

src/main/resources/  
├── download/        Downloaded VPI data   
├── real/            Real data (ignored in git)  
├── sample/          Sample data  
├── template.xlsx    Excel template used for output

target/  
├── real/            Real output (ignored in git)  
├── sample/          Sample output  
```


---

## Author
Julian Schmid

https://github.com/julian4schmid
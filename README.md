תודה על ההערה! הנה הגרסה המעודכנת עם ההתייחסות לקורס באוניברסיטת אריאל:


**# Ex2 - Advanced Spreadsheet Application**

## **Author**  
**ID:** 325310142  

## **Overview**  
This project is part of the **I2CS course** at **Ariel University** (2025A).  
It implements a **spreadsheet application** that supports managing data, computations, and formulas within a two-dimensional table.

## **Features**  

- **Formula Parsing:** Supports arithmetic operations (`+`, `-`, `*`, `/`), parentheses, and cell references.  
- **Error Handling:** Detects invalid formulas and circular dependencies, marking cells with appropriate error codes (`ERR_FORM!`, `ERR_CYCLE!`).  
- **Dynamic Recalculation:** Automatically updates dependent cells when values or formulas change.  
- **Persistence:** Save and load the spreadsheet state for continued work.  

## **Components**  

- **`SCell`**: Represents individual cells, managing data types and dependencies.  
- **`Ex2Sheet`**: Core spreadsheet logic, including cell management and formula evaluations.  
- **`CellEntry`**: Processes and validates cell references (e.g., `A1`, `B2`).  
- **`Ex2Utils`**: Utility constants and helper functions for parsing and error handling.  
- **`StdDrawEx2`**: Provides graphical rendering for the spreadsheet.  

## **Usage**  

### **Spreadsheet Operations**  

**Set Cell Value:**  
```java  
Ex2Sheet sheet = new Ex2Sheet();  
sheet.set(0, 0, "=A1+5");  
```  

**Get Cell Value:**  
```java  
String value = sheet.value(0, 0);  
```  

**Save/Load:**  
```java  
sheet.save("spreadsheet.txt");  
sheet.load("spreadsheet.txt");  
```  

## **Testing**  
Comprehensive tests are provided in `Ex2Tests.java`, covering:  
- Valid inputs (e.g., numbers, valid formulas).  
- Invalid inputs (e.g., syntax errors, circular references).  
- Edge cases to ensure robust error handling and performance.  



![Project Screenshot](photo.png)


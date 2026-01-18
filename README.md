# Vechimo - Seniority Certificate Generator

## About the Application

**Vechimo** is a modern desktop application that automates the process of generating employment seniority certificates. The application scans employment contract documents (PDF) and automatically extracts all necessary information to create a complete seniority certificate.

### What does the application do?

The **Vechimo** application dramatically simplifies the process of creating seniority certificates, transforming a manual task that could take hours into an automated process of just a few minutes.

**Traditional (manual) process:**
1. Manual reading of employment contract documents
2. Recording important data (name, SSN, salary, position, etc.)
3. Finding and recording all contractual changes (salary increases, promotions, etc.)
4. Manual completion of the certificate form
5. Checking data for errors

**With the Vechimo application:**
1. Take a photo or scan the contract document
2. Enter the identification number
3. Press the "Generate Document" button
4. The document is automatically generated and saved!

## Main Features

✅ **Automatic Scanning** - Uses advanced OCR (Optical Character Recognition) technology through Amazon Textract to read text from images with high accuracy

✅ **Intelligent Data Extraction** - Automatically identifies:
- Personal data (name, SSN)
- Contract details (contract number, start date)
- Salary history
- Promotions and position changes
- Salary increases
- Other contractual modifications

✅ **Automatic Document Generation** - Creates a professional Word document using a predefined template

✅ **User-Friendly Interface** - Modern design with pleasant visual effects (colored gradient, shadow effects, "glass" appearance)

✅ **Organized Saving** - Generated documents are automatically saved in the "archive" folder with the employee's name

## How to Use

### Step 1: Document Preparation
- Make sure you have the individual employment contract in physical or PDF format
- If it's in physical format, take a clear photo or scan the document
- Accepted format: images (PNG, JPG, JPEG)

### Step 2: Launching the Application
- Open the Vechimo application
- You will see a window with a pleasant design in orange-yellow colors

### Step 3: Document Selection
- Press the "Choose Photo" button
- Navigate and select the image with the employment contract
- The file path will appear in the text field

### Step 4: Entering the Identification Number
- In the "Identification Number" field, enter the unique number of the document or employee
- This number will be included in the generated certificate

### Step 5: Document Generation
- Press the "Generate Document" button
- The application will:
  - Scan the image
  - Extract all necessary information
  - Create the Word document
  - Save the document in the "archive" folder
- You will receive a success notification when the document is ready

### Step 6: Finding the Document
- Go to the "archive" folder in the application directory
- You will find the document named: "[Employee Name] SeniorityCertificate.docx"

## Technical Information (for the Curious)

### Technologies Used
- **JavaFX** - For the modern graphical interface
- **Amazon AWS Textract** - For scanning and text recognition from images
- **Apache POI** - For generating Word documents
- **Maven** - For dependency management

### Supported Functions
The application can identify and extract the following types of information:

1. **Contract Registration** - Initial hiring (date, initial salary, position)
2. **Salary Increases** - All salary raises over time
3. **Promotions** - Position/role changes
4. **Contract Termination** - If applicable

## Certificate Template

The application uses a predefined Word template (`template.docx`) that contains special zones marked with unique symbols. These zones are automatically filled with data extracted from the contract.

## Placeholders Used in the Template

The following table describes all special placeholders used in the certificate template:

| Placeholder | Symbol | Description | Example Value |
|-------------|--------|-------------|---------------|
| `CNP` | ɞ | Personal Identification Number of the employee | 1234567890123 |
| `today` | ſ | Current date (certificate generation date) | 01/18/2026 |
| `number` | ƀ | Document identification number | 12345 |
| `contractNumber` | ě | Individual employment contract number | 1039/138 |
| `contractDate` | Ĝ | Contract signing date | 02/15/2018 |
| `name` | ɛ | Full name of the employee | John Smith |
| `currentJob` | Ĺ | Current position of the employee | SALES REPRESENTATIVE |
| `currentSalary` | ĺ | Current gross salary | 5000 |
| `intervention` | Ŕ | Type of contractual modification | Salary increase |
| `date` | ŕ | Date of a contractual modification | 03/01/2020 |
| `act` | ś | Legal document (Decision, Contract Amendment) | DECISION |
| `job` | ū | Position at a given time | REGIONAL MANAGER |
| `salary` | ų | Salary at a given time | 4500 |

### Recognized Positions

The application automatically recognizes the following occupation codes (Romanian Classification of Occupations - COR):

| COR Code | Position Title |
|----------|----------------|
| 522101 | SALES REPRESENTATIVE |
| 142008 | REGIONAL MANAGER |
| 112018 | SALES DIRECTOR |
| 311519 | MECHANICAL TECHNICIAN |
| 243103 | MARKETING SPECIALIST |
| Others | WAREHOUSE MANAGER (default) |

## Project Structure

```
Vechimo/
├── src/main/java/
│   └── org/example/
│       ├── screens/          # Graphical interface
│       │   ├── WindowController.java    # Main window control
│       │   └── MainWindow.java         # UI components
│       └── vechimo/          # Application logic
│           ├── VechimoApplication.java # Entry point
│           ├── User.java              # User data management
│           ├── DetectText.java        # OCR scanning with AWS Textract
│           ├── Certificate.java       # Word document generation
│           ├── Parsing.java           # Processing coordination
│           ├── InterventionRecord.java # Contractual modification records
│           ├── PlaceHolders.java      # Placeholder definitions
│           ├── YearComparator.java    # Chronological sorting
│           ├── DocumentExtractor.java # Interface for extractors
│           ├── ProcessorFactory.java  # Extractor instance creation
│           ├── UnitTest.java         # Test extractor (without OCR)
│           └── EnvLoader.java        # Configuration loading
├── src/main/resources/
│   └── template.docx         # Certificate template
├── arhiva/                   # Generated documents
└── pom.xml                   # Maven configuration

```

## Application Workflow

1. **Image Selection** → User chooses an image with the contract
2. **OCR Scanning** → AWS Textract extracts text from the image
3. **Data Extraction** → Application identifies and extracts:
   - Personal data (SSN, name)
   - Contract information (number, date)
   - Salary history (all increases)
   - Promotions (all position changes)
4. **Chronological Sorting** → All events are sorted by date
5. **Document Generation** → Word template is filled with extracted data
6. **Saving** → Final document is saved in the "archive" folder

## System Requirements

- **Operating System:** Windows 10/11, macOS, Linux
- **Java:** Version 17 or newer
- **Internet Connection:** Required for AWS Textract service
- **Disk Space:** Minimum 100 MB

## AWS Configuration (For Administrators)

For the application to work, a `.env` file is required in the application directory with AWS credentials:

```
AWS_ACCESS_KEY_ID=your_access_key_here
AWS_SECRET_ACCESS_KEY=your_secret_key_here
AWS_REGION=us-east-1
```

**Note:** These credentials must be obtained from an AWS administrator who has access to the Textract service.

## Troubleshooting Common Issues

### "Please select a file first"
- **Cause:** No image has been selected
- **Solution:** Press "Choose Photo" and select a valid image

### "Please enter the identification number"
- **Cause:** The identification number field is empty
- **Solution:** Enter a number in the "Identification Number" field

### "An error occurred while processing the file"
- **Possible cause 1:** The image is not clear or the text cannot be read
- **Solution:** Use a clearer image of the document

- **Possible cause 2:** Connection issues with AWS Textract
- **Solution:** Check your internet connection and AWS credentials

- **Possible cause 3:** Document format is not recognized
- **Solution:** Make sure the document contains information in the standard employment contract format

## Data Security

- The application processes data **locally** on your computer
- Images are sent to AWS Textract only for OCR scanning
- Generated documents are saved **locally** in the "archive" folder
- No data is stored in the cloud outside of the scanning process

## Benefits of Using the Application

✅ **Time Savings:** Reduces processing time from hours to minutes
✅ **Accuracy:** Eliminates manual transcription errors
✅ **Consistency:** All certificates have the same professional format
✅ **Automation:** Automatically identifies all contractual modifications
✅ **Organization:** Automatically saves documents with descriptive names

## Known Limitations

⚠️ **Image Quality:** The application requires clear images for optimal results
⚠️ **Document Format:** Works optimally with contracts in standard Romanian format
⚠️ **Internet Connection:** Required for OCR functionality
⚠️ **Language:** Optimized for documents in Romanian

## Support and Assistance

For technical issues or questions:
- Check the "Troubleshooting Common Issues" section
- Consult the technical documentation (for advanced users)
- Contact your organization's system administrator

## Version History

### Version 1.0-SNAPSHOT (Current)
- Initial release
- OCR scanning with AWS Textract
- Automatic certificate generation
- Modern graphical interface
- Support for multiple types of contractual modifications

## Conclusion

**Vechimo** is a complete and modern solution for automatic generation of seniority certificates. By combining advanced OCR technology with a user-friendly interface, the application makes the process simple, fast, and accurate.

---

*Application developed for automating human resources processes*
*Version: 1.0-SNAPSHOT*
*Last updated: January 2026*

package abels.qa;

import abels.qa.model.Person;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ZIP File Content Tests")
public class FileReadingTest {

    ClassLoader classLoader = FileReadingTest.class.getClassLoader();
    String zipName = "files/test-files.zip";

    @Test
    @DisplayName("Reading and verifying a PDF file from a ZIP archive")
    void zipPdfReadingTest() throws Exception {
        try (InputStream inputStream = classLoader.getResourceAsStream(zipName);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().contains("phones_prices.pdf")) {
                        PDF pdf = new PDF(zipInputStream);
                        assertThat(pdf.text).containsIgnoringCase("Samsung Galaxy S24 Ultra");
                    }
                }
        }
    }

    @Test
    @DisplayName("Reading and verifying a XLSX file from a ZIP archive")
    void zipXlsxReadingTest() throws Exception {
        try (InputStream inputStream = classLoader.getResourceAsStream(zipName);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().contains("jobs_hours.xlsx")) {
                    XLS xls = new XLS(zipInputStream);
                    Assertions.assertEquals("Scrum Master",
                            xls.excel.getSheet("Suspended")
                                    .getRow(1)
                                    .getCell(0)
                                    .getStringCellValue());
                }
            }
        }
    }

    @Test
    @DisplayName("Reading and verifying a CSV file from a ZIP archive")
    void zipCsvReadingTest() throws Exception {
        try (InputStream inputStream = classLoader.getResourceAsStream(zipName);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().contains("fruits_prices.csv")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zipInputStream));
                    List<String[]> content = csvReader.readAll();
                    Assertions.assertArrayEquals(new String[] {"Orange", "12"}, content.get(1));
                }
            }
        }
    }

    @Test
    @DisplayName("Reading and verifying a JSON file")
    void jsonReadingTest() throws Exception {
        try (InputStream inputStream = classLoader.getResourceAsStream("files/person.json");
             Reader reader = new InputStreamReader(inputStream)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Person person = objectMapper.readValue(reader, Person.class);
            assertThat(person.getLastName()).containsIgnoringCase("Autotestov");
            assertThat(person.getFirstName()).containsIgnoringCase("Autotest");
        }
    }
}
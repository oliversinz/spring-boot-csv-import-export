package de.cloudwards.spring.csvimport.person;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;

public class PersonCSVHelper {

    public static String TYPE = "text/csv";

    static String[] HEADERs = {"Id", "firstName", "lastName", "email"};

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType())
                || file.getContentType().equals("application/vnd.ms-excel");
    }

    public static List<Person> csvToPersons(InputStream is) {
        try ( BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));  CSVParser csvParser = new CSVParser(fileReader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Person> persoonList = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Person person = new Person(
                        Long.parseLong(csvRecord.get("Id")),
                        csvRecord.get("firstName"),
                        csvRecord.get("lastName"),
                        csvRecord.get("email")
                );

                persoonList.add(person);
            }

            return persoonList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream personsToCSV(List<Person> personList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try ( ByteArrayOutputStream out = new ByteArrayOutputStream();  CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (Person person : personList) {
                List<String> data = Arrays.asList(
                        String.valueOf(person.getId()),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getEmail()
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}

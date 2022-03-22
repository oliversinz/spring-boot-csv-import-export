package de.cloudwards.spring.csvimport.person;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PersonCSVService {

    @Autowired
    PersonRepository repository;

    public void save(MultipartFile file) {
        try {
            List<Person> persons = PersonCSVHelper.csvToPersons(file.getInputStream());
            repository.saveAll(persons);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load() {
        List<Person> persons = repository.findAll();
        ByteArrayInputStream in = PersonCSVHelper.personsToCSV(persons);
        return in;
    }

    public List<Person> getAllPersons() {
        return repository.findAll();
    }
}

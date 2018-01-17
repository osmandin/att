package edu.mit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import edu.mit.entity.*;
import edu.mit.repository.*;

import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Logger;

@Component
public class DepartmentsFormFormatter implements Formatter<DepartmentsForm> {
    private final static Logger LOGGER = Logger.getLogger(DepartmentsFormFormatter.class.getCanonicalName());

    @Autowired
    DepartmentsFormRepository departmentsFormRepository;

    public String print(DepartmentsForm object, Locale locale) {
        return (object != null ? Integer.toString(object.getId()) : "");
    }

    public DepartmentsForm parse(String text, Locale locale) throws ParseException {
        //LOGGER.log(Level.INFO, "parse: text={0}", new Object[]{text});
        if (text.equals("")) {
            return null;
        }
        int id = -1;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
        }
        if (id == -1) {
            return null;
        }
        return departmentsFormRepository.findById(id);
    }
}

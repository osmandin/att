package edu.mit.att.service;

import edu.mit.att.entity.Department;
import edu.mit.att.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Logger;

@Component
public class DepartmentsFormFormatter implements Formatter<Department> {
    private final static Logger LOGGER = Logger.getLogger(DepartmentsFormFormatter.class.getCanonicalName());

    @Autowired
    DepartmentRepository departmentRepository;

    public String print(Department object, Locale locale) {
        return (object != null ? Integer.toString(object.getId()) : "");
    }

    public Department parse(String text, Locale locale) throws ParseException {
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
        return departmentRepository.findById(id);
    }
}

package edu.mit.controllers;

import edu.mit.entity.UsersForm;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


@Controller
public class AddController {

    private final Logger logger = getLogger(this.getClass());


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public Model greetingForm(final Model model) {

        logger.info("Adding page");

        final UsersForm item = new UsersForm();

        ModelAndView modelAndView = new ModelAndView("add");

        // Page<Item> items = itemService.findByItemId(id, new PageRequest(0, 1));

        // TODO: decide where things like go:


        model.addAttribute("usersForm", item);
        return model;

    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView greetingForm(final UsersForm usersForm, BindingResult result) {
        final List<UsersForm> itemList = new ArrayList<>();
        itemList.add(usersForm);

        final ModelAndView modelAndView = new ModelAndView("redirect:/results");
        return modelAndView;

    }

}
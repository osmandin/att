package edu.mit.controllers;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static org.slf4j.LoggerFactory.getLogger;


@Controller
public class HomeController {

    private final Logger logger = getLogger(this.getClass());

    /**
     * Handles all request
     * @return model and view
     */
    @RequestMapping(value={"/"}, method = RequestMethod.GET)
    public ModelAndView showItemsPage() {
        final ModelAndView modelAndView = new ModelAndView("Home");
        return modelAndView;
    }

}

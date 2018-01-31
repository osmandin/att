package edu.mit.controllers;

import edu.mit.entity.OrgInfo;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

import static org.slf4j.LoggerFactory.getLogger;


@Controller
public class HomeController {

    private final Logger logger = getLogger(this.getClass());

    @Resource
    private Environment env;

    /**
     * Handles all request
     * @return model and view
     */
    @RequestMapping(value={"/"}, method = RequestMethod.GET)
    public ModelAndView showItemsPage() {
        final ModelAndView modelAndView = new ModelAndView("Home");
        return modelAndView;
    }

    /**
     * Handles all request
     * @return model and view
     */
    @RequestMapping(value={"/Faq"}, method = RequestMethod.GET)
    public ModelAndView show() {
        final ModelAndView model = new ModelAndView("Faq");
        OrgInfo org = new OrgInfo();
        org.setEmail(env.getRequiredProperty("org.email"));
        org.setPhone(env.getRequiredProperty("org.phone"));
        org.setName(env.getRequiredProperty("org.name"));
        org.setNamefull(env.getRequiredProperty("org.namefull"));
        model.addObject("org", org);
        return model;
    }

}

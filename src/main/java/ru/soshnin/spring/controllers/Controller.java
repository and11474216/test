package ru.soshnin.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.soshnin.spring.dao.PersonDao;
import ru.soshnin.spring.models.Person;

@org.springframework.stereotype.Controller
@RequestMapping("/people")
public class Controller {

    private final PersonDao personDao;

    @Autowired
    public Controller(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", personDao.index());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id,
                       Model model) {
        model.addAttribute("person", personDao.show(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person){
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person")  Person person){
        personDao.save(person);
        return  "redirect:/people";
    }
    //Верный метод
    @GetMapping("/price/{id}")
    public String price(@PathVariable("id") int id,
                        Model model) {
        model.addAttribute("person", personDao.price(id));
        return "people/price";
    }

    @PostMapping("/price/")
    public String addPrice(@ModelAttribute("person") Person person) {
        personDao.addPrice(person.getId(), person.getPrice());
        return "redirect:/people";
    }

    @GetMapping("/visit/{id}")
    public String newVisit(@PathVariable("id") int id,
                        Model model) {
        model.addAttribute("person", personDao.show(id));
        return "people/visit";
    }

    @PostMapping("/visit/")
    public String inClubRightNow(@ModelAttribute("person") Person person) {
        personDao.addVisit(person.getId());
        return "redirect:/people";
    }

    @GetMapping("/presence/")
    public String presence(Model model) {
        model.addAttribute("people", personDao.presence());
        return "people/presence";
    }

    @PostMapping("/out/{id}")
    public String out(@PathVariable("id") int id) {
        personDao.out(id);
        return "redirect:/people";
    }



}

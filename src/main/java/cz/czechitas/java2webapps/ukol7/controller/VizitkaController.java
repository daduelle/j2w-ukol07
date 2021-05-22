package cz.czechitas.java2webapps.ukol7.controller;

import cz.czechitas.java2webapps.ukol7.entity.Vizitka;
import cz.czechitas.java2webapps.ukol7.repository.VizitkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class VizitkaController {

    private final VizitkaRepository repository;

    @Autowired
    public VizitkaController(VizitkaRepository repository) {
        this.repository = repository;
    }

    // Nastavení „bindování“ vstupů od uživatele do Java objektů – prázdné řetězce se nastaví jako {@code null} hodnota.
    @InitBinder
    public void nullStringBinding(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    // Zobrazí všechny vizitky.
    @GetMapping("/")
    public Object seznam() {
        return new ModelAndView("seznam")
                .addObject("vizitka", repository.findAll());
    }

    // Zobrazí detail vizitky.
    @GetMapping("/{id:[0-9]+}")
    public Object detail(@PathVariable Integer id) {
        Optional<Vizitka> vizitka = repository.findById(id);
        if (vizitka.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ModelAndView("vizitka")
                .addObject("vizitka", vizitka.get());
    }

    // Zobrazí formulář pro zadání nové vizitky.
    @GetMapping("/nova")
    public Object nova() {
        return new ModelAndView("formular")
                .addObject("vizitka", new Vizitka());
    }

    // Uloží vizitku do databáze, u nové založí ID (přesměruje na hlavní stánku), stávající uloží (přesměruje na detail).
    @PostMapping("/nova")
    public Object pridat(@ModelAttribute("vizitka") @Valid Vizitka vizitka, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "formular";
        }
        if (vizitka.getId() == null) {
            vizitka.setId(null);
            repository.save(vizitka);
            return "redirect:/";
        }
        repository.save(vizitka);
        String redirectURL = String.valueOf(vizitka.getId());
        return "redirect:/" + redirectURL;
    }

    // Smaže záznam z databáze.
    @PostMapping(value = "/{id:[0-9]+}", params = "akce=smazat")
    public Object smazat(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/";
    }

    // Zobrazí formulář pro změnu údajů.
    @GetMapping("/nova/{id:[0-9]+}")
    public Object zmena(@PathVariable Integer id) {
        Optional<Vizitka> vizitka = repository.findById(id);
        if (vizitka.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ModelAndView("formular")
                .addObject("vizitka", vizitka.get());
    }
}

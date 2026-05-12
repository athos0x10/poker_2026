package com.projet.poker.controller;

import com.projet.poker.engine.TableManager;
import com.projet.poker.model.game.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class TableController {

    @Autowired
    public TableController() {}

    @GetMapping("/table")
    public String listTables(Model model) {
        // à compléter
        return "a";
    }
}
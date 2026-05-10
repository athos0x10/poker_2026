package com.projet.poker.controller;

import com.projet.poker.engine.TableManager;
import com.projet.poker.model.game.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class LobbyController {

    private final TableManager tableManager;

    @Autowired
    public LobbyController(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    @GetMapping("/lobby")
    public String listTables(Model model) {
        // On récupère les valeurs de la Map (les objets Table) et on les met dans une liste
        var tables = new ArrayList<>(tableManager.getAllTables().values());
        model.addAttribute("listeDesTables", tables);
        System.out.println("Nombre de tables trouvées : " + tables.size());
        return "lobby"; // cherche le fichier src/main/resources/templates/lobby.html
    }
}
# Architecture du Système (9 Entités)

L'application respecte la contrainte d'avoir **plus de 7 entités** et suit une architecture **MVC** avec un back-end **SpringBoot**.

## 1. Bloc Persistant (Base de Données Relationnelle)
Ce bloc assure le stockage à long terme des informations utilisateurs.
* **Utilisateur :** Gère l'authentification (Login, mot de passe haché, email).
* **Profil :** Contient les données sociales (Avatar, biographie, niveau d'expérience).
* **Portefeuille :** Gère le capital global de jetons pour le **classement**.
* **Amitié :** Gère les relations et les **invitations entre joueurs**.

## 2. Bloc de Jeu (Gestion en mémoire / Temps réel)
Pour garantir le **temps réel** et la **performance**, ces classes gèrent l'état actif des parties :
* **Table :** Définit l'espace de jeu (Nom, mise minimale, places disponibles).
* **SessionJoueur :** Entité pivot permettant à un joueur d'être sur **plusieurs tables simultanément**.
* **Main (Hand) :** Représente un tour de jeu.
* **Action :** Enregistre chaque mouvement (Mise, fold, check) pour la logique de jeu.
* **MessageChat :** Gère les flux de communication du **Chat**.

## 3. Fonctionnalités
### 1. Authentification et Gestion des Joueurs
* Inscription et connexion sécurisée (hachage des mots de passe).
* Gestion du profil joueur (Avatar, Biographie).
* Portefeuille virtuel (Système de jetons globaux mis à jour après chaque table).
* Suivi de l'expérience (Niveaux de joueur).

### 2. Dimension Sociale
* **Système d'Amis :** Envoi, acceptation et refus d'invitations entre joueurs.
* **Chat Temps Réel :** Espace de discussion global sur chaque table via WebSockets.

### 3. Moteur de Jeu Poker (Temps Réel)
* **Lobby (Salle d'attente) :** Liste des tables disponibles, création de nouvelles tables (publiques ou privées).
* **Gestion des Sessions :** Un joueur peut rejoindre plusieurs tables simultanément.
* **Déroulement d'une Main :**
    * Distribution aléatoire des cartes (Hole cards & Community cards).
    * Gestion stricte des tours de parole (Bouton Dealer, Petite/Grosse Blinde).
    * Actions supportées : *Fold* (Coucher), *Check* (Parole), *Call* (Suivre), *Raise* (Relancer), *All-In* (Tapis).
* **Évaluation des Mains :** Algorithme de calcul de la meilleure combinaison de 5 cartes (Paire, Double Paire, Brelan, Suite, Couleur, Full, Carré, Quinte Flush).
* **Distribution des Gains :** Répartition du pot (gestion des égalités "Split Pot").

### 4. Historique et Classement
* **Leaderboard (Classement) :** Affichage des meilleurs joueurs basé sur l'expérience ou le total du portefeuille.
* **Historique des Parties :** Consultation des résultats des dernières mains jouées (gains/pertes).

## 4. Architecture Java
```
src/
 ├── main/
 │    ├── java/com/projet/poker/
 │    │    ├── PokerApplication.java          # Point d'entrée SpringBoot
 │    │    ├── model/                         # Entités (JPA @Entity ou simples POJOs)
 │    │    │    ├── persist/                  # Utilisateur, Profil, Portefeuille, Amitie
 │    │    │    └── game/                     # Table, SessionJoueur, Main, Action (État en mémoire)
 │    │    ├── repository/                    # Interfaces Spring Data JPA (UtilisateurRepository...)
 │    │    ├── service/                       # Les "Facades" contenant la logique métier (UserService, GameService)
 │    │    ├── controller/                    # Les Servlets ou @RestController (gèrent les requêtes HTTP/WebSockets)
 │    │    ├── dto/                           # Data Transfer Objects (Pour ne pas renvoyer directement les Entités JPA à la vue)
 │    │    ├── config/                        # Configuration Spring (WebSockets, Sécurité, BDD)
 │    │    └── engine/                        # Moteur du jeu de Poker (PUREMENT Java : règles, calcul des mains. Indépendant de Spring)
 │    │
 │    ├── resources/
 │    │    ├── application.properties         # Config BDD, port Tomcat, etc.
 │    │    └── static/                        # Fichiers CSS, JS, Images (si utilisation de JSP/HTML classique)
 │    │
 │    └── webapp/
 │         └── WEB-INF/
 │              └── jsp/                      # Vues JSP
 └── pom.xml                                  # Dépendances (Spring Web, Spring Data JPA, H2/MySQL, WebSockets)
```

## 5. Diagramme de classe 
```mermaid
classDiagram

    %% --- BLOC PERSISTANT (JPA / BDD) ---
    class Utilisateur {
        +Long id
        +String login
        +String passwordHash
        +String email
        +Date createdAt
        +authenticate(String password) boolean
    }

    class Profil {
        +Long id
        +Long userId
        +String avatarUrl
        +String bio
        +int experiencePoints
        +int level
        +addExperience(int points) void
    }

    class Portefeuille {
        +Long id
        +Long userId
        +double globalBalance
        +addFunds(double amount) void
        +withdraw(double amount) boolean
    }

    class Amitie {
        +Long id
        +Long user1Id
        +Long user2Id
        +FriendStatus status
        +Date since
    }
    
    class FriendStatus {
        <<enumeration>>
        PENDING
        ACCEPTED
        BLOCKED
    }

    %% --- BLOC DE JEU (EN MÉMOIRE / TEMPS RÉEL) ---
    class GameState {
        <<enumeration>>
        WAITING_FOR_PLAYERS
        PRE_FLOP
        FLOP
        TURN
        RIVER
        SHOWDOWN
    }

    class ActionType {
        <<enumeration>>
        FOLD
        CHECK
        CALL
        RAISE
        ALL_IN
    }

    class Carte {
        +String rank
        +String suit
    }

    class Table {
        +Long id
        +String name
        +double minBet
        +int maxPlayers
        +GameState state
        +List~SessionJoueur~ activePlayers
        +List~MessageChat~ chatHistory
        +broadcastState() void
        +addPlayer(Utilisateur u) boolean
        +removePlayer(SessionJoueur p) void
    }

    class SessionJoueur {
        +Long id
        +Long tableId
        +Long userId
        +double currentStack
        +int seatNumber
        +List~Carte~ holeCards
        +boolean hasFolded
        +boolean isAllIn
        +placeBet(double amount, ActionType type) Action
    }

    class Main {
        +Long id
        +Long tableId
        +double potAmount
        +List~Carte~ communityCards
        +Date startTime
        +int currentTurnIndex
        +SessionJoueur dealerButton
    }

    class Action {
        +Long id
        +Long handId
        +Long playerSessionId
        +ActionType type
        +double amount
        +Date timestamp
    }
    
    class MessageChat {
        +Long id
        +Long tableId
        +Long userId
        +String content
        +Date sentAt
    }

    %% --- LOGIQUE MÉTIER (SANS ÉTAT) ---
    class PokerEngine {
        +startNewHand(Table t) Main
        +processAction(Main m, Action a) boolean
        +evaluateShowdown(Main m) void
        +determineWinners(List~SessionJoueur~ players, List~Carte~ communityCards) List~SessionJoueur~
    }

    %% --- RELATIONS ---
    Utilisateur "1" -- "1" Profil : possède
    Utilisateur "1" -- "1" Portefeuille : détient
    Utilisateur "1" -- "*" Amitie : initie / reçoit
    Utilisateur "1" -- "*" SessionJoueur : participe via

    Table "1" -- "*" SessionJoueur : accueille
    Table "1" -- "1" Main : currentHand
    Table "1" -- "*" MessageChat : contient

    Main "1" -- "*" Action : enregistre
    SessionJoueur "1" -- "*" Action : effectue
```

## 6. Diagramme d'usage
```mermaid
flowchart LR
    %% Acteur
    Joueur((Joueur))

    %% Système et Cas d'utilisation
    subgraph Application de Poker en Ligne
        direction TB
        UC1(S'inscrire / Se connecter)
        UC2(Gérer son profil et son portefeuille)
        UC3(Gérer les invitations et amis)
        UC4(Créer ou rejoindre une Table)
        UC5(Jouer : Miser, Suivre, Se coucher)
        UC6(Discuter dans le Chat)
    end

    %% Liens Acteur -> Cas d'utilisation
    Joueur --> UC1
    Joueur --> UC2
    Joueur --> UC3
    Joueur --> UC4
    Joueur --> UC5
    Joueur --> UC6

    %% Dépendances (Includes / Extends) optionnelles pour montrer la logique
    UC5 -. "<< extends >>" .-> UC4
    UC6 -. "<< extends >>" .-> UC4
```

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

## Architecture Java
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

## Diagramme de classe 
```mermaid
classDiagram
    direction TB

    %% Bloc Persistant
    class Utilisateur {
        +Long id
        +String login
        +String passwordHash
        +String email
        +Date createdAt
    }
    class Profil {
        +Long id
        +Long userId
        +String avatarUrl
        +String bio
        +int experiencePoints
        +int level
    }
    class Portefeuille {
        +Long id
        +Long userId
        +double globalBalance
    }
    class Amitie {
        +Long id
        +Long user1Id
        +Long user2Id
        +String status
        +Date since
    }

    %% Bloc Temps Réel
    class Table {
        +Long id
        +String name
        +double minBet
        +int maxPlayers
        +boolean isPrivate
        +String gameState
    }
    class SessionJoueur {
        +Long id
        +Long tableId
        +Long userId
        +double currentStack
        +int seatNumber
    }
    class Main {
        +Long id
        +Long tableId
        +double potAmount
        +String communityCards
        +Date startTime
    }
    class Action {
        +Long id
        +Long handId
        +Long playerSessionId
        +String type
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

    %% Relations
    Utilisateur "1" -- "1" Profil : possède
    Utilisateur "1" -- "1" Portefeuille : détient
    Utilisateur "1" -- "*" Amitie : initie / reçoit
    Utilisateur "1" -- "*" SessionJoueur : participe via
    Utilisateur "1" -- "*" MessageChat : envoie

    Table "1" -- "*" SessionJoueur : accueille
    Table "1" -- "*" Main : déroule
    Table "1" -- "*" MessageChat : contient

    SessionJoueur "1" -- "*" Action : effectue
    Main "1" -- "*" Action : enregistre
```

## Diagramme d'usage
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

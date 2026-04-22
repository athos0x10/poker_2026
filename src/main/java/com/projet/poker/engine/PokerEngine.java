package com.projet.poker.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.projet.poker.engine.logger.ConsoleGameLogger;
import com.projet.poker.engine.logger.GameLogger;
import com.projet.poker.model.game.Action;
import com.projet.poker.model.game.GameHand;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public class PokerEngine {

    /*
     * Le moteur de jeu du poker gère la logique du jeu, les règles,
     * les tours de mise, la distribution des cartes, etc.
     */

    private static final int MAX_HAND = 5;
    private GameNotifier notifier;
    private GameLogger logger;


    public PokerEngine() {
        this.logger = new ConsoleGameLogger();
    }


    public void setNotifier(GameNotifier notifier) {
        this.notifier = notifier;
    }

    
    public GameLogger getLogger() {
        return logger;
    }



    /*=====================================================================================
     *=================================== EVALUATION ======================================
     ====================================================================================== */


    /* Trie les cartes cummulées d'un joueur et de la table
     */
    private List<Card> sortCards(PlayerSession player, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(player.getHoleCards());
        allCards.addAll(communityCards);

        CardComparator cmp = new CardComparator();
        Collections.sort(allCards, cmp);
            
        return allCards;
    }


    /* Renvoie une map avec pour chaque valeur de carte, son nombre d'occurences
    */
    private HashMap<CardValue, Integer> countCardValues(List<Card> cards) {
        HashMap<CardValue, Integer> map = new HashMap<>();

        for (Card c : cards) {
            map.merge(c.getCardValue(), 1, Integer::sum);
        }

        return map;
    }


    /* Renvoie une map avec pour chaque couleur de carte, son nombre d'occurences
    */
    private HashMap<CardColor, Integer> countCardColors(List<Card> cards) {
        HashMap<CardColor, Integer> map = new HashMap<>();

        for (Card c : cards) {
            map.merge(c.getCardColor(), 1, Integer::sum);
        }

        return map;
    }


    /* Renvoie la liste des clés qui apparaissent 'targetCount' fois (une clé par valeur)
     * Cela permet de trouver la combinaison (en couleur ou valeur)
     * Ex: pour un full (avec une contrainte sur les valeurs), il peut y avoir dans le jeu 
     * {Roi, Roi, Roi, Dame, 10, 10 10, 9, ...}
     * ce qui renverra {Roi, 10} (en valeurs = 13, 10)
    */
    private List<CardValue> findKeysWithCount(HandData hand, Integer targetCount) {
        List<CardValue> matchingKeys = new ArrayList<>();

        for (Map.Entry<CardValue, Integer> entry : hand.getCountCardValues().entrySet()) {
            if (Objects.equals(entry.getValue(), targetCount)) {
                matchingKeys.add(entry.getKey());
            }
        }
        
        // Une HashMap ne conserve pas l'ordre.
        // Trier à nouveau est plus "sûr" que d'utiliser une LinkedHashMap
        Collections.sort(matchingKeys, Collections.reverseOrder());; // Ordre décroissant

        return matchingKeys;
    }


    /* Renvoie toutes les cartes dont la clé fait partie de 'targetValues' (valeur ou couleur)
     * Cela permet de renvoyer toutes les cartes de la combinaison
     * Ex: pour un full (contraite sur les valeurs) dont le résultat de findValuesWithCount est {13, 10},
     * on récupère  {Roi, Roi, Roi, 10, 10 10}
     * Note: les cartes sont triées car celles en entrée sont triées aussi, donc la boucle garde l'ordre
     * - targetAtributes: liste des couleurs ou valeurs recherchées
     * - extractor: fonction pour extraire l'attribut de la carte: cardValue ou cardColor
    */
    private <T> List<Card> extractCards(List<Card> sortedCards, List<T> targetAttributes, Function<Card, T> extractor) {
        List<Card> extracted = new ArrayList<>();

        for (Card c : sortedCards) {
            if (targetAttributes.contains(extractor.apply(c))) {
                extracted.add(c);
            }
        }

        return extracted;
    }


    /* Complète la combinaison avec les meilleurs cartes de façon à toujours avoir 5 cartes dans la combinaison,
     * et pourvoir ainsi départager les égalités.
    */
    private void updateBestWithKickers(List<Card> currentBest, List<Card> ordered) {
        int numberOfKickers = MAX_HAND - currentBest.size();
        if (numberOfKickers == 0) return;

        List<Card> temp = new ArrayList<>();

        for (int i = 0; i < ordered.size() && (temp.size() < numberOfKickers); i++) {
            Card currentOrdered = ordered.get(i);
            if (!currentBest.contains(currentOrdered)) {
                temp.add(currentOrdered);
            }
        }

        currentBest.addAll(temp);
    }


    /* Une combinaison est un carré si elle possède 4 cartes de la même valeur */
    private List<Card> findCarre(HandData hand) {
        // renvoie toutes les occurences de 4 (il ne peut en avoir qu'une seule ou 0)
        List<CardValue> carreValues = findKeysWithCount(hand, 4); 

        if (carreValues.size() == 1) {
            List<Card> sorted = hand.getSortedCards();
            List<Card> best = extractCards(sorted, carreValues, Card::getCardValue); // On extrait les cartes associées
            updateBestWithKickers(best, sorted); // On complète avec la meilleur carte restante de la main
            return best;
        }

        return null;
    }


    /* Une combinaison est un full si elle possède 2 fois 3 cartes identiques,
     * Ou bien une fois 3 cartes et une fois 2 cartes identiques.
    */
    private List<Card> findFull(HandData hand) {
        // On cherche les occurences de 3, il peut y en avoir 0, 1 ou 2
        List<CardValue> fullValues = findKeysWithCount(hand, 3);
        // S'il n'y a qu'une fois 3 carte, il faut vérifier s'il n'existe pas une paire pour former le full
        if (fullValues.size() < 2) fullValues.addAll(findKeysWithCount(hand, 2));

        // Il est possible qu'il y ait plusieurs paires, mais on ne doit garder que 2 types de cartes
        if (fullValues.size() >= 2) fullValues = new ArrayList<>(fullValues.subList(0, 2));

        List<Card> best = extractCards(hand.getSortedCards(), fullValues, Card::getCardValue);
        
        return best.size() == MAX_HAND ? best : null;
    }


    /* Combinaison particulière AS,2,3,4,5
    */
    private boolean findFirstQuinte(List<Card> cards) {
        return cards.getFirst().getCardValue().equals(CardValue.AS)
            && cards.getLast().getCardValue().equals(CardValue.DEUX)
            && cards.get(cards.size() - 2).getCardValue().equals(CardValue.TROIS)
            && cards.get(cards.size() - 3).getCardValue().equals(CardValue.QUATRE)
            && cards.get(cards.size() - 4).getCardValue().equals(CardValue.CINQ);
    }


    /* Une combinaison est une quinte si 5 cartes se suivent, y compris
     *  10,VALET,DAME,ROI,AS (naturelle) ou AS,2,3,4,5 (cas particulier testFirstQuinte)
     *
    */
    private List<Card> findQuinte(HandData hand) {
        // Il faut supprimer les doublons pour ne pas biaiser le compteur
        LinkedHashSet<Card> set = new LinkedHashSet<>(hand.getSortedCards());
        // On recréé la liste triée sans les doublons
        List<Card> cards = new ArrayList<>(set);

        int count = 0;
        int endIndex = 0;

        // Rappel: cards est triée par ordre décroissant (AS, ROI, DAME, ..., 2)
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getCardValue().getValue() == 
                (cards.get(i + 1).getCardValue().getValue() + 1)) {
                count++;
                if (count >= 4) endIndex = i + 1;
            } else {
                count = 0;
            }
        }

        if (count >= 4) {
            return new ArrayList<>(cards.subList(endIndex - 4, endIndex + 1));
        }

        // On test le cas particulier
        if (findFirstQuinte(cards)) {
            List<Card> result = new ArrayList<>(cards.subList(cards.size() - 4, cards.size()));
            result.add(cards.getFirst());
            return result;
        } 

        return null;
    }


    /* Une combinaison est un brelan si elle contient 3 cartes de même valeur
     * (puisque le full est testé AVANT)
     */
    private List<Card> findBrelan(HandData hand) {
        // Renvoi les cartes qui apparaissent 3 fois, il y en a de 0 ou 1 type car le full est déjà traité
        List<CardValue> brelanValues = findKeysWithCount(hand, 3);

        if (brelanValues.size() == 1) {
            List<Card> sorted = hand.getSortedCards();
            List<Card> best = extractCards(sorted, brelanValues, Card::getCardValue); // On extrait les cartes associées
            updateBestWithKickers(best, sorted); // On complète avec la meilleur carte restante de la main
            return best;
        }

        return null;
    }


    /* Une combinaison est une paire double si elle possède aux moins deux paires
    */
    private List<Card> findDoublePaire(HandData hand) {
        List<CardValue> pairValues = findKeysWithCount(hand, 2);

        if (pairValues.size() >= 2) {
            // On ne garde que les deux meilleurs paires
            List<CardValue> bestTwoPairs = new ArrayList<>(pairValues.subList(0,  2));
            List<Card> sorted = hand.getSortedCards();
            List<Card> best = extractCards(sorted, bestTwoPairs, Card::getCardValue);
            updateBestWithKickers(best, sorted);
            return best;
        }
        
        return null;
    }
    

    /* Une combinaison est une paire si elle possède deux cartes seulement avec la même valeur
     * (la double paire est traitée avant)
     */
    private List<Card> findPaire(HandData hand) {
        List<CardValue> pairValue = findKeysWithCount(hand, 2);

        if (pairValue.size() == 1) {
            List<Card> best = extractCards(hand.getSortedCards(), pairValue, Card::getCardValue);
            updateBestWithKickers(best, hand.getSortedCards());
            return best;
        }
        
        return null;
    }


    /* Une combinaison est un flush si elle possède 5 cartes de la même couleur
     * (n'importe lesquelles)
    */
    private List<Card> findFlush(HandData hand) {
        for (Map.Entry<CardColor, Integer> entry : hand.getCountCardColors().entrySet()) {
            if (entry.getValue() >= 5) {
                List<Card> flushCards = extractCards(hand.getSortedCards(), Arrays.asList(entry.getKey()), Card::getCardColor);
                return new ArrayList<>(flushCards.subList(0, 5)); // On ne garde que les 5 meilleures
            }
        }
        return null;
    }
    

    /* Une combinaison est une quinte flush si c'est une quinte (les 5 cartes se suivent)
     * ET que CES 5 cartes sont toutes de la même couleur (pas seulement 5 cartes de la main)
     */
    private List<Card> findQuinteFlush(HandData hand) {
        List<Card> quinteCards = findQuinte(hand);
        if (quinteCards == null) return null;

        CardColor color = quinteCards.getFirst().getCardColor();

        for (Card c : quinteCards) {
            if (!c.getCardColor().equals(color)) {
                return null;
            }
        }

        return quinteCards;
    }


    /* Evalue une main en positionannt le type de combinaison (ex: QUINTE_FLUSH)
     * et en donnant les meilleurs 5 cartes (pour les égalités et l'évaluation du score)
    */
    private void evaluateHand(HandData hand) {
        List<Card> best;
        List<Function<HandData, List<Card>>> evaluators = Arrays.asList(
            this::findQuinteFlush, this::findCarre, this::findFull, this::findFlush, 
            this::findQuinte, this::findBrelan, this::findDoublePaire, this::findPaire
        );

        List<HandType> types = Arrays.asList(
            HandType.QUINTE_FLUSH, HandType.CARRE, HandType.FULL, HandType.FLUSH,
            HandType.QUINTE, HandType.BRELAN, HandType.DOUBLE_PAIRE, HandType.PAIRE
        );

        for (int i = 0; i < evaluators.size(); i++) {
            if ((best = evaluators.get(i).apply(hand)) != null) {
                hand.setType(types.get(i));
                hand.setBestFiveCards(best);
                return;
            }
        }
        
        hand.setType(HandType.CARTE_HAUTE); 
        hand.setBestFiveCards(new ArrayList<>(hand.getSortedCards().subList(0, MAX_HAND)));

        return;
    }


    /* Evalue la main d'un seul joueur en fonction des cartes présentes sur la table
     */
    private HandData evaluateSinglePlayer(PlayerSession player, List<Card> community) {
        List<Card> sortedCards = sortCards(player, community);
        HashMap<CardValue, Integer> cardValues = countCardValues(sortedCards);
        HashMap<CardColor, Integer> cardColors = countCardColors(sortedCards);

        HandData hand = new HandData(cardValues, cardColors, sortedCards);
        evaluateHand(hand);
        player.setFinalHand(hand.getType());
        
        return hand;
    }


    /* Compare les valeurs des cartes en cas d'égalité
     */
    private int compareTieBreak(HandData h1, HandData h2) {
        List<Card> h1List = h1.getBestFiveCards();
        List<Card> h2List = h2.getBestFiveCards();

        for (int i = 0; i < h1List.size(); i++) {
            int v1 = h1List.get(i).getCardValue().getValue();
            int v2 = h2List.get(i).getCardValue().getValue();

            if (v1 < v2) {
                return -1;
            } else if (v1 > v2) {
                return 1;
            } else {
                continue;
            }
        }

        return 0;
    }


    /* Compare les mains de deux joueurs,
     * en regardant d'abord la combinaison puis les valeurs des cartes
    */
    private int compareHands(HandData h1, HandData h2) {
        int h1Score = h1.getType().getScore();
        int h2Score = h2.getType().getScore();
        if (h1Score < h2Score) {
            return -1;
        } else if (h1Score > h2Score) {
            return 1;
        } else {
            return compareTieBreak(h1, h2);
        }
    }


    /* Méthode qui permet de trier les joueurs dans des groupes.
     * Cela a deux utilités: les classer pour déterminer le potentiel gagnant, celui avec la meilleur main.
     * Gérer des égalités et la distribution correcte du pot:
     * - par exemple si deux joueurs ont la même main, on doit diviser le gain
     * - si le joueur à une mise inférieur avec un all-in, il faut continuer à distribuer le montant équitablement aux "prochains gagnants"
    */
    private List<List<PlayerSession>> groupPlayersByHand(List<PlayerSession> survivors, List<Card> communityCards) {
        // Résultat final, chaque groupe contient des joueurs qui ont la même main (égalité), et les groupes sont triés du meilleur au moins bon
        List<List<PlayerSession>> rankedGroups = new ArrayList<>();

        List<PlayerSession> currentGroup = new ArrayList<>();
        currentGroup.add(survivors.get(0));  // survivors est trié donc le premier à l'une des meilleurs mains
        rankedGroups.add(currentGroup);            // On doit tout de suite ajouter le groupe du "potentiel gagnant"

        for (int i = 1; i < survivors.size(); i++) {
            PlayerSession p = survivors.get(i);
            HandData hSorted = evaluateSinglePlayer(survivors.get(i-1), communityCards); // Ce joueur est déjà dans le groupe courant par récurrence
            HandData hNotSorted = evaluateSinglePlayer(p, communityCards);               // On ré-évalue la main nouveau joueur pour le comparer avec le précédent

            if (compareHands(hSorted, hNotSorted) == 0) {
                currentGroup.add(p);               // Ils ont la même main (même si le joueur précédent était classé avant)
            } else {
                currentGroup = new ArrayList<>();  // Comme les survivors sont déjà classé, p a forcément une moins bonne main
                currentGroup.add(p);               // currentGroup est un nouveau groupe et on met le joueur dedans (le précédent est "plein")
                rankedGroups.add(currentGroup);    // On ajoute déjà ce nouveau groupe (et on pourra ajouter le prochain joueur etc...)
            }
        }

        return rankedGroups;
    }


    /* Méthode qui distribue équitablement le pot aux joueurs. :
     * Chaque joueur gagne ce qu'il peut gagner en partant de ceux qui ont la meilleur main,
     * jusqu'à ce que le pot soit vide. On distribue donc en cascade
     * L'idée est la suivante:
     *  - Pour chaque groupe, on récupère la valeur du plus petit all-in, c'est le palier maximum courant.
     *  - Chaque joueur du groupe récupère ce gain.
     *  - Dès qu'un joueur à tout récupéré (sa mise était égale au palier courant), il sort du calcul.
     *  - Les autres joueurs récupèreront d'autres gain (puisqu'ils ont misé davantage)
     *    en se basant sur le prochain palier
    */
    private void distributePot(Table table, List<List<PlayerSession>> rankedGroups) {
        for (List<PlayerSession> originalGroup : rankedGroups) {

            List<PlayerSession> group = new ArrayList<>(originalGroup);

            // On doit boucler tant qu'il y a encore des membres dans le groupe qui peuvent gagner de l'argent
            // C'est pour gérer le cas où des membres n'ont pas misé le même montant à cause de all-in
            while(!group.isEmpty()) {
                // On enlève directement les joueurs qui ne peuvent plus réclamer
                group.removeIf(p -> p.getTotalInvestedInHand() <= 0); // On les retire
                if (group.isEmpty()) break;                           // Groupe suivant

                // On récupère le plus petit all-in
                double minInvestedInGroup = group.stream()
                    .mapToDouble(PlayerSession::getTotalInvestedInHand)
                    .min().orElse(0);

                // Il reste les joueurs du groupe qui peuvent encore gagner à hauteur max du plus petit all-in
                double groupHarvest = 0;

                // Chaque contributeur donne au groupe
                for (PlayerSession contributor : table.getActivePlayers()) {
                    double contribution = Math.min(contributor.getTotalInvestedInHand(), minInvestedInGroup);
                    groupHarvest += contribution;
                    // Ici chaque gagnant est aussi un contributeur, mais il récupère ensuite ce qu'il a misé !
                    contributor.setTotalInvestedInHand(contributor.getTotalInvestedInHand() - contribution);
                }

                // On répartit la somme entre les membres du groupe
                double share = groupHarvest / group.size();
                for (PlayerSession winner : group) {
                    winner.deposit(share);
                }
            }
        }
   }



    /*=====================================================================================
     *================================= TOURS ET MISES ====================================
     ====================================================================================== */

    /* METHODES UTILITAIRES */

    /* Distribue des cartes à tous les joueurs
    */
    private void distributeHoleCards(Table table) {
        for (PlayerSession player: table.getActivePlayers()) {
            player.getHoleCards().addAll(table.getGameHand().drawCards(2));
        }
    }

    /* Récupère l'indice du prochain joueur en tournant dans le sens des aiguilles
     * d'une montre (prochain à gauche).
     * Ce n'est n'est pas le joueur qui débutera le tour, c'est celui
     * directement à gauche de 'index'
     */
    private int getNextPlayerIdx(int index, int size) {
        return (index + 1) % size;
    }

    /* Récupère l'indice du prochain joueur qui commencera le round
     * si index = dealerIdx ou bien seulement le prochain à jouer si index = currentIdx
     * (il ne doit pas s'être couché ou êre all-in)
     * Normalement, il n'y a pas besoin de protection sur la boucle.
     * Si on retombe sur le premier joueur (et donc ça tourne à l'infini car tout le monde est all-in par exemple),
     * alors isRoundFinished aura renvoyé true dans processAction et donc on gerera là-bas les cas particuliers ! 
    */
    private int findNextPlayer(Table table, int index) {
        List<PlayerSession> players = table.getActivePlayers();

        int nextIdx = (index + 1) % table.getActivePlayers().size();

        while (players.get(nextIdx).hasFolded() || players.get(nextIdx).isAllIn()) {
            nextIdx = (nextIdx + 1) % players.size();
        }

        return nextIdx;
    }


    /* Nombre de joueurs qui n'ont pas FOLD 
     * Si ce nombre vaut 1, la main s'arrête, le dernier joueur ramasse le pot.
     * On renvoie les joueurs pour y avoir accès. On vérifiera le nombre avec size()
    */
    private List<PlayerSession> countSurvivors(Table table) {
        List<PlayerSession> survivors = new ArrayList<>();

        for (PlayerSession player : table.getActivePlayers()) {
            if (!player.hasFolded()) {
                survivors.add(player);
            }
        }

        return survivors;
    }


    /* Nombre de joueurs n'ayant ni FOLD ni ALL-IN
     * Si ce nombre vaut 0 ou 1 (ET qu'il y a plus d'un survivant), alors on déroule les cartes
     * (RunTheBoard)
     * On renvoie les joueurs pour y avoir accès. On vérifiera le nombre avec size()
    */
    private List<PlayerSession> countActiveBettors(Table table) {
        List<PlayerSession> activeBettors = new ArrayList<>();

        for (PlayerSession player : table.getActivePlayers()) {
            if (!(player.hasFolded() || player.isAllIn())) {
                activeBettors.add(player);
            }
        }

        return activeBettors;
    } 

    public void goNextState(Table t) {
        switch(t.getGameState()) {
            case GameState.PRE_FLOP:
                t.setGameState(GameState.FLOP);
                break;
            case GameState.FLOP:
                t.setGameState(GameState.TURN);
                break;
            case GameState.TURN:
                t.setGameState(GameState.RIVER);
                break;
            case GameState.RIVER:
                t.setGameState(GameState.SHOWDOWN);
                break;
            case GameState.SHOWDOWN:
                t.setGameState(GameState.WAITING_FOR_PLAYERS);
                break;
            case WAITING_FOR_PLAYERS:
                t.setGameState(GameState.PRE_FLOP);
        }
    }

    /* Nettoie la table */
    public void clearTable(Table t) {
        t.setGameState(GameState.WAITING_FOR_PLAYERS);
        t.getGameHand().getCommunityCards().clear();

        t.getGameHand().resetDeck();
        
        for (PlayerSession p : t.getActivePlayers()) {
            p.resetBet();
            p.getHoleCards().clear();
            p.setHasFolded(false);
            p.setAllIn(false);
            p.setHasActed(false);
            p.setTotalInvestedInHand(0);
        }

        // Sécurité même si c'est fait au début d'une manche (startNewHand)
        t.getGameHand().setPotAmount(0);
    }


    /* Met à jour la table si le round (PRE-FLOP, ...) est fini
     * (voir processAction)
    */
    private void updateGameState(Table table) {
        GameHand gameHand = table.getGameHand();

        for (PlayerSession player : table.getActivePlayers()) {
            player.resetBet();
        }

        gameHand.setHighestBet(0);
        
        int dealerIdx = table.getActivePlayers().indexOf(gameHand.getDealerButton());
        gameHand.setCurrentTurnIndex(findNextPlayer(table, dealerIdx));
    }


    /* Au début d'une partie, récolte les blindes,
     * met à jour les bet des joueurs et le pot global, etc.
     */
    private void collectBlinds(Table table) {
        GameHand gameHand = table.getGameHand();
        List<PlayerSession> activePlayers = table.getActivePlayers();

        // Récupère les index du Dealer, Petite blinde et Grosse blinde
        int dealerIdx = activePlayers.indexOf(gameHand.getDealerButton());
        int smallBlindIdx = (dealerIdx + 1) % activePlayers.size();
        int bigBlindIdx = (smallBlindIdx + 1) % activePlayers.size();

        // Récupère les joueurs
        PlayerSession sbPlayer = activePlayers.get(smallBlindIdx);
        PlayerSession bbPlayer = activePlayers.get(bigBlindIdx);
        gameHand.setSmallBlindPlayer(sbPlayer);
        gameHand.setBigBlindPlayer(bbPlayer);

        // Définit les montants. Attention, on ne peut pas prélever plus que ce que le joueur possède
        double smallBlind = Math.min(table.getMinBet() / 2, sbPlayer.getCurrentStack());
        double bigBlind = Math.min(table.getMinBet(), bbPlayer.getCurrentStack());

        gameHand.setSmallBlindAmount(smallBlind);
        gameHand.setBigBlindAmount(bigBlind);

        // Retire les mises
        sbPlayer.bet(smallBlind);
        if (sbPlayer.getCurrentStack() <= 0) sbPlayer.setAllIn(true);

        bbPlayer.bet(bigBlind);
        if (bbPlayer.getCurrentStack() <= 0) bbPlayer.setAllIn(true);

        // Ajoute les mises au pot
        gameHand.addToPot(smallBlind + bigBlind);
        gameHand.setHighestBet(Math.max(smallBlind, bigBlind));
    }


    /* Débute une nouvelle partie:
    * distribution des cartes, collecte de la blind, désignation du dealer, etc.
    */
    public void startNewHand(Table table) {
        // Remise à 0 du pot, ramassage des cartes, ...
        clearTable(table);

        // Changer le GameState de la table en PRE_FLOP.
        table.setGameState(GameState.PRE_FLOP);

        GameHand gameHand = table.getGameHand();
        gameHand.setDealerButton(table.getActivePlayers().get(0));
        gameHand.shuffleDeck();

        // Retirer les "Blindes" (Petite et Grosse blinde) aux deux premiers joueurs et les mettre dans le pot.
        collectBlinds(table);

        // Distribuer les 2 cartes à tout le monde.
        distributeHoleCards(table);

        // Mettre le currentTurnIndex sur le 3ème joueur (celui après la Grosse Blinde).
        List<PlayerSession> activePlayers = table.getActivePlayers();
        int dealerIdx = activePlayers.indexOf(gameHand.getDealerButton());
        gameHand.setCurrentTurnIndex(
            getNextPlayerIdx(getNextPlayerIdx(getNextPlayerIdx(dealerIdx, activePlayers.size()), activePlayers.size()), activePlayers.size())
        );
    }


    /* Vérifie si le round est terminé.
     * C'est le cas si chaque joueur étant en capacié de jouer l'a fait, 
     * et qu'il a suivi la plus grosse mise, ou bien s'il ne reste qu'un seul joueur
    */
    private boolean isRoundFinished(Table table) {
        // Il ne reste qu'un survivant, le tour s'arrête immédiatement
        if (countSurvivors(table).size() <= 1) {
            return true;
        }

        // On regarde si tous les joueurs actifs on parlé et suivi la mise
        List<PlayerSession> activePlayers = table.getActivePlayers();
        for (PlayerSession player : activePlayers) {
            // On passe les joueurs qui se sont couchés ou ont all-in (ils ne peuvent plus rien faire)
            if (player.hasFolded() || player.isAllIn()) continue;

            if (!(player.hasActed() && (player.getBetInCurrentRound() == table.getGameHand().getHighestBet()))) {
                return false;
            }
        }
        return true;
    }


    private boolean handleFold(PlayerSession player) {
        player.setHasFolded(true);
        return true;
    }


    private boolean  handleCheck(Table table, Action action, PlayerSession player) {
        // Si le joueur a toujours la mise la plus élevée, il s'est passé un tour sans RAISE
        // Donc tout le monde à CHECK
        return player.getBetInCurrentRound() == table.getGameHand().getHighestBet();
    }


    private boolean handleCall(Table table, Action action, PlayerSession player) {
        double amountToWithdraw = table.getGameHand().getHighestBet() - player.getBetInCurrentRound();
        if (player.getCurrentStack() < amountToWithdraw) return false;

        // Le joueur dans son action, indique le prix pour suivre, pas ce qu'il va payer en plus
        player.bet(amountToWithdraw);
        table.getGameHand().addToPot(amountToWithdraw);
        return true;
    }


    private boolean handleRaise(Table table, Action action, PlayerSession player) {
        double targetBet = action.getAmount();
        if (targetBet <= table.getGameHand().getHighestBet()) return false;

        // Le joueur dans son action, indique à combien il va élever le bet, pas ce qu'il va payer en plus
        double amountToWithdraw = targetBet - player.getBetInCurrentRound();
        if (amountToWithdraw > player.getCurrentStack()) return false;

        player.bet(amountToWithdraw);
        table.getGameHand().addToPot(amountToWithdraw);
        table.getGameHand().setHighestBet(targetBet);

        // Les autres joueurs doivent pouvoir remiser
        for (PlayerSession p : table.getActivePlayers()) {
            if (!p.equals(player)) {
                p.setHasActed(false);
            }
        }
        return true;
    }


    private boolean handleAllIn(Table table, Action action, PlayerSession player) {
        GameHand gameHand = table.getGameHand();
        double amountToWithdraw = player.getCurrentStack();
        // Le joueur n'a plus d'argent (ou de jetons)
        if (amountToWithdraw <= 0) return false;

        player.bet(amountToWithdraw);
        gameHand.addToPot(amountToWithdraw);

        // Si la nouvelle mise total du joueur dépasse le record de la table
        if (player.getBetInCurrentRound() > gameHand.getHighestBet()) {
            gameHand.setHighestBet(player.getBetInCurrentRound());

            // Agit comme un RAISE !
            for (PlayerSession p : table.getActivePlayers()) {
                if (!p.equals(player)) {
                    p.setHasActed(false);
                }
            }
        }


        player.setAllIn(true);
        return true;
    }


    /* Fonction utilitaire pour processAction */
    private boolean handleAction(Table table, Action action, PlayerSession player) {
        boolean isLegal = false;

        switch(action.getActionType()) {
            case ActionType.FOLD:
                isLegal = handleFold(player);
                break;
            case ActionType.CHECK:
                isLegal = handleCheck(table, action, player);
                break;
            case ActionType.CALL:
                isLegal = handleCall(table, action, player);
                break;
            case ActionType.RAISE:
                isLegal = handleRaise(table, action, player);
                break;
            case ActionType.ALLIN:
                isLegal = handleAllIn(table, action, player);
                break;
        }

        return isLegal;
    }


    /* Méthode permettant de dévoiler toutes les cartes
     * et d'avancer directement à la fin de la manche quand tout le monde
     * a ALL-IN
    */
   private void runTheBoard(Table t) {

        // Si on est dans l'état RIVER, on a déjà distribué les cartes de cet état (on est à la fin)
        while (t.getGameState() != GameState.RIVER) {
            goNextState(t);

            if (t.getGameState() == GameState.FLOP) dealFlop(t);
            if (t.getGameState() == GameState.TURN) dealTurn(t);
            if (t.getGameState() == GameState.RIVER) dealRiver(t);

        }

        evaluateShowdown(t);
   }


    /* Distribue les cartes sur la table en fonction de l'état du jeu */
    private void distributeCards(Table table) {
        switch (table.getGameState()) {
            case GameState.FLOP: dealFlop(table); break;
            case GameState.TURN: dealTurn(table); break;
            case GameState.RIVER: dealRiver(table); break;
            default: break;
        }
    }


    /* Gère le début normal d'une nouvelle manche:
     * - Plusieurs joueurs peuvent encore miser
     * - Il n'y a pas eu que des tapis
     * On vérifie si en changeant de manche, on arrive naturellement à la fin de la partie (de la main = SHOWDOWN),
     * Ou bien si on continue normalement: nouvelle(s) carte(s) et tour de parole
    */
    private void handleNextRound(Table table) {
        if (table.getGameState() == GameState.SHOWDOWN) {
            evaluateShowdown(table);
        } else {
            distributeCards(table);
            updateGameState(table);
        }
    }

    
    /* Gère la fin d'un round (voir processAction):
     * - S'il n'y a qu'un survivor (voir la méthod utilitaire plus haut), on lui donne le pot
     * - Si 0 ou 1 joueur peut encore miser (avec plusieurs survivant ayant ALL-IN), on déroule les cartes
     * - Sinon, on continue normalement
     * Méthode à modifier plus tard pour les multi-pots
    */ 
    private void handleRoundEnding(Table table) {
        List<PlayerSession> survivors = countSurvivors(table);
        List<PlayerSession> activeBettors = countActiveBettors(table);

        if (survivors.size() == 1) {
            // Le gagant remporte le pot
            PlayerSession winner = survivors.getFirst();
            table.setWinners(Arrays.asList(winner));
            winner.deposit(table.getGameHand().getPotAmount());
            table.getGameHand().setPotAmount(0);
            table.setGameState(GameState.SHOWDOWN);

        } else if (activeBettors.size() <= 1 && survivors.size() > 1) {
            runTheBoard(table);

        } else {
            goNextState(table);
            handleNextRound(table);
        }
    }

    
    public void dealFlop(Table t) {
        GameHand g = t.getGameHand();

        g.burnCard();
        g.getCommunityCards().addAll(g.drawCards(3));
        t.setGameState(GameState.FLOP);

        for (PlayerSession p : t.getActivePlayers()) p.setHasActed(false);
    }


    public void dealTurn(Table t) {
        GameHand g = t.getGameHand();

        g.burnCard();
        g.getCommunityCards().add(g.drawCard());
        t.setGameState(GameState.TURN);

        for (PlayerSession p : t.getActivePlayers()) p.setHasActed(false);
    }


    public void dealRiver(Table t) {
        GameHand g = t.getGameHand();

        g.burnCard();
        g.getCommunityCards().add(g.drawCard());
        t.setGameState(GameState.RIVER);

        for (PlayerSession p : t.getActivePlayers()) p.setHasActed(false);
    }


    /* Evalue la fin de partie en distribuant le pot parmi les joueurs
     * Renvoie les joueurs aillant eu la meilleure main.
     * Attention, ce ne sont pas forcément ceux qui ont remporté le plus (dépend de leur mise de all-in)
    */
    public List<PlayerSession> evaluateShowdown(Table table) {
        List<Card> communityCards = table.getGameHand().getCommunityCards();  // Cartes sur le tapis
        List<PlayerSession> survivors = countSurvivors(table);  // Gagnants potentiels
        if (survivors.isEmpty()) return null;  // Normalement IMPOSSIBLE

        // On trie les survivants par meilleur main (sans gérer pour l'instant les égalités)
        survivors.sort((p1, p2) -> compareHands(evaluateSinglePlayer(p2, communityCards),
                                                evaluateSinglePlayer(p1, communityCards)));

        // On fait des groupes de joueurs pour gérer les égualité
        List<List<PlayerSession>> rankedGroups = groupPlayersByHand(survivors, communityCards);

        // On distribue les sommes par groupe
        distributePot(table, rankedGroups);

        table.getGameHand().setPotAmount(0);
        table.setGameState(GameState.SHOWDOWN);

        return rankedGroups.get(0);
    }


    /* Gère une action envoyée par un joueur, nécessité de vérifier sa légalité
     * Normalement, on a pas besoin de vérifier qu'un joueur est couché ou all-in
     * car il passera son tour selon l'algo de findNextPlayer.
     * Autrement dit, un joueur entre dans processAction s'il n'a pas encore joué,
     * ou que sa dernière action est CALL, RAISE ou CHECK.
     * De même, si un joueur clique sur le bouton pour CALL en dehors de son tour tandis qu'il
     * est all-in, la ligne vérifiant l'ID renverra false (son action sera ignorée)
     */
    public boolean processAction(Table table, Action action) {
        GameHand gameHand = table.getGameHand();

        // Le joueur n'est pas autorisé à jouer si ce n'est pas son tour
        if (action.getPlayerId() != gameHand.getCurrentTurnIndex()) return false;

        PlayerSession player = table.getActivePlayers().get(gameHand.getCurrentTurnIndex());

        // isLegal reste à false vraiment si le coup n'est pas autorisé (ex: jetons insuffisants)
        boolean isLegal = handleAction(table, action, player);

        if (!isLegal) return false;
        player.setHasActed(true);

        if (isRoundFinished(table)) {
            handleRoundEnding(table);
        } else {
            int nextTurn = findNextPlayer(table, gameHand.getCurrentTurnIndex());
            gameHand.setCurrentTurnIndex(nextTurn);
        }

        return true;
    }


    
    /*======================================================================================
    *========================================= MAIN ========================================
    ======================================================================================= */

    /* Méthode principale */
    public void start() {
        return;
    }
}

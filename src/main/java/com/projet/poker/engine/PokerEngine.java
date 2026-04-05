package main.java.com.projet.poker.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import main.java.com.projet.poker.model.game.Action;
import main.java.com.projet.poker.model.game.GameHand;
import main.java.com.projet.poker.model.game.PlayerSession;
import main.java.com.projet.poker.model.game.Table;

public class PokerEngine {

    /*
     * Le moteur de jeu du poker gère la logique du jeu, les règles,
     * les tours de mise, la distribution des cartes, etc.
     */

    private static final int MAX_HAND = 5;

    public PokerEngine() {}



    /*=====================================================================================
     *=================================== EVALUATION ======================================
     ====================================================================================== */

    /* Trie les cartes cummulées d'un joueur et de la table
     */
    public List<Card> sortCards(PlayerSession player, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(player.getHoleCards());
        allCards.addAll(communityCards);

        CardComparator cmp = new CardComparator();
        Collections.sort(allCards, cmp);
            
        return allCards;
    }


    /* Renvoie une map avec pour chaque valeur de carte, son nombre d'occurences
    */
    public HashMap<CardValue, Integer> countCardValues(List<Card> cards) {
        HashMap<CardValue, Integer> map = new HashMap<>();

        for (Card c : cards) {
            map.merge(c.getCardValue(), 1, Integer::sum);
        }

        return map;
    }


    /* Renvoie une map avec pour chaque couleur de carte, son nombre d'occurences
    */
    public HashMap<CardColor, Integer> countCardColors(List<Card> cards) {
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
    public <T extends Comparable<? super T>> List<T> findKeysWithCount(Map<T, Integer> countMap, Integer targetCount) {
        List<T> matchingKeys = new ArrayList<>();

        for (Map.Entry<T, Integer> entry : countMap.entrySet()) {
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
    public <T> List<Card> extractCards(List<Card> sortedCards, List<T> targetAttributes, Function<Card, T> extractor) {
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
    public void updateBestWithKickers(List<Card> currentBest, List<Card> ordered) {
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
    public List<Card> findCarre(HandData hand) {
        // renvoie toutes les occurences de 4 (il ne peut en avoir qu'une seule ou 0)
        List<CardValue> carreValues = findKeysWithCount(hand.getCountCardValues(), 4); 

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
    public List<Card> findFull(HandData hand) {
        // On cherche les occurences de 3, il peut y en avoir 0, 1 ou 2
        List<CardValue> fullValues = findKeysWithCount(hand.getCountCardValues(), 3);
        // S'il n'y a qu'une fois 3 carte, il faut vérifier s'il n'existe pas une paire pour former le full
        if (fullValues.size() < 2) fullValues.addAll(findKeysWithCount(hand.getCountCardValues(), 2));

        // Il est possible qu'il y ait plusieurs paires, mais on ne doit garder que 2 types de cartes
        if (fullValues.size() >= 2) fullValues = new ArrayList<>(fullValues.subList(0, 2));

        List<Card> best = extractCards(hand.getSortedCards(), fullValues, Card::getCardValue);
        
        return best.size() == MAX_HAND ? best : null;
    }


    /* Combinaison particulière AS,2,3,4,5
    */
    public boolean findFirstQuinte(List<Card> cards) {
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
    public List<Card> findQuinte(HandData hand) {
        // Il faut supprimer les doublons pour ne pas biaiser le compteur
        LinkedHashSet<Card> set = new LinkedHashSet<>(hand.getSortedCards());
        // On recréé la liste triée sans les doublons
        List<Card> cards = new ArrayList<>();
        cards.addAll(set);

        int i, count = 0;

        // Rappel: cards est triée par ordre décroissant (AS, ROI, DAME, ..., 2)
        for (i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getCardValue().getValue() == 
                (cards.get(i + 1).getCardValue().getValue() + 1)) {
                 count++;   
            } else {
                count = 0;
            }
        }

        if (count >= 5) {
            // Les 5 cartes se suivent
            return new ArrayList<>(cards.subList(i - 4, i + 1));
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
    public List<Card> findBrelan(HandData hand) {
        // Renvoi les cartes qui apparaissent 3 fois, il y en a de 0 ou 1 type car le full est déjà traité
        List<CardValue> brelanValues = findKeysWithCount(hand.getCountCardValues(), 3);

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
    public List<Card> findDoublePaire(HandData hand) {
        List<CardValue> pairValues = findKeysWithCount(hand.getCountCardValues(), 2);

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
    public List<Card> findPaire(HandData hand) {
        List<CardValue> pairValue = findKeysWithCount(hand.getCountCardValues(), 2);

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
    public List<Card> findFlush(HandData hand) {
        List<CardColor> flushValue = findKeysWithCount(hand.getCountCardColors(), 5);

        if (flushValue.size() == 1) {
            return extractCards(hand.getSortedCards(), flushValue, Card::getCardColor);
        }
        
        return null;
    }
    

    /* Une combinaison est une quinte flush si c'est une quinte (les 5 cartes se suivent)
     * ET que CES 5 cartes sont toutes de la même couleur (pas seulement 5 cartes de la main)
     */
    public List<Card> findQuinteFlush(HandData hand) {
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
    public void evaluateHand(HandData hand) {
        List<Card> best;
        List<Function<HandData, List<Card>>> evaluators = Arrays.asList(
            this::findQuinteFlush,
            this::findCarre, 
            this::findFull, 
            this::findFlush, 
            this::findQuinte,
            this::findBrelan,
            this::findDoublePaire, 
            this::findPaire
        );

        List<HandType> types = Arrays.asList(
            HandType.QUINTE_FLUSH,
            HandType.CARRE,
            HandType.FULL,
            HandType.FLUSH,
            HandType.QUINTE,
            HandType.BRELAN,
            HandType.DOUBLE_PAIRE,
            HandType.PAIRE
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
    public HandData evaluateSinglePlayer(PlayerSession player, List<Card> community) {
        List<Card> sortedCards = sortCards(player, community);
        HashMap<CardValue, Integer> cardValues = countCardValues(sortedCards);
        HashMap<CardColor, Integer> cardColors = countCardColors(sortedCards);

        HandData hand = new HandData(cardValues, cardColors, sortedCards);
        evaluateHand(hand);
        
        return hand;
    }


    /* Compare les valeurs des cartes en cas d'égalité
     */
    public int compareTieBreak(HandData h1, HandData h2) {
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
    public int compareHands(HandData h1, HandData h2) {
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


    List<PlayerSession> determineWinners(List<PlayerSession> players, List<Card> communityCards) {
        if (players == null || players.isEmpty()) return null;

        int i;
        List<PlayerSession> winners = new ArrayList<>();

        // Map pour stocker les mains des joueurs
        HashMap<PlayerSession, HandData> playerHands = new HashMap<>();
        for (PlayerSession p : players) {
            playerHands.put(p, evaluateSinglePlayer(p, communityCards));
        }

        // Gagnant temporaire
        PlayerSession currentWinner = players.getFirst();
        winners.add(currentWinner);
        HandData winningHand = playerHands.get(currentWinner);

        for (i = 1; i < players.size(); i++) {
            PlayerSession candidate = players.get(i);
            HandData candidateHand = playerHands.get(candidate);

            int cmp = compareHands(candidateHand, winningHand);
            
            // Le second joueur possède une meilleur main que le gagnant courant
            if (cmp > 0) {
                winners.clear();
                winners.add(candidate);
                winningHand = candidateHand;
            } else if (cmp == 0) {
                winners.add(candidate);
            }
        }

        return winners;
    }



    /*=====================================================================================
     *================================= TOURS ET MISES ====================================
     ====================================================================================== */


    /* Distribue des cartes à tous les joueurs
    */
    private void distribute(Table table) {
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
     * alors isRoundFinished aura renvoyé true dans processAction ! 
    */
    private int findNextPlayer(Table table, int index) {
        List<PlayerSession> players = table.getActivePlayers();

        int nextIdx = (index + 1) % table.getActivePlayers().size();

        while (players.get(nextIdx).hasFolded() || players.get(nextIdx).isAllIn()) {
            nextIdx = (nextIdx + 1) % players.size();
        }

        return nextIdx;
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

        // Définit les montants
        double smallBlind = table.getMinBet() / 2;
        double bigBlind = table.getMinBet();

        // Retire les mises
        sbPlayer.bet(smallBlind);
        bbPlayer.bet(bigBlind);

        // Ajoute les mises au pot
        gameHand.addToPot(smallBlind + bigBlind);
        gameHand.setHighestBet(bigBlind);
    }


    /* Débute une nouvelle partie:
    * distribution des cartes, collecte de la blind, désignation du dealer, etc.
    */
    public void startNewHand(Table table) {
        // Changer le GameState de la table en PRE_FLOP.
        if (!table.getGameState().equals(GameState.WAITING_FOR_PLAYERS)) return;
        table.setGameState(GameState.PRE_FLOP);

        // Mélanger le deck.
        GameHand gameHand = table.getGameHand();
        gameHand.shuffleDeck();

        // Mettre le potAmount à 0.
        gameHand.setPotAmount(0);

        // Retirer les "Blindes" (Petite et Grosse blinde) aux deux premiers joueurs et les mettre dans le pot.
        collectBlinds(table);

        // Distribuer les 2 cartes à tout le monde.
        distribute(table);

        // Mettre le currentTurnIndex sur le 3ème joueur (celui après la Grosse Blinde).
        List<PlayerSession> activePlayers = table.getActivePlayers();
        int dealerIdx = activePlayers.indexOf(gameHand.getDealerButton());
        gameHand.setCurrentTurnIndex(getNextPlayerIdx(getNextPlayerIdx(dealerIdx, activePlayers.size()), activePlayers.size()));
    }


    /* Vérifie si le round est terminé.
     * C'est le cas si chaque joueur étant en capacié de jouer l'a fait, 
     * et qu'il a suivi la plus grosse mise
    */
    private boolean isRoundFinished(Table table) {
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

    
    public void dealFlop(Table t) {
        GameHand g = t.getGameHand();

        g.burnCard();
        g.getCommunityCards().addAll(g.drawCards(3));
        t.goNextState();

        for (PlayerSession p : t.getActivePlayers()) p.setHasActed(false);
    }


    public void dealTurn(Table t) {
        GameHand g = t.getGameHand();

        g.burnCard();
        g.getCommunityCards().add(g.drawCard());
        t.goNextState();

        for (PlayerSession p : t.getActivePlayers()) p.setHasActed(false);
    }


    public void dealRiver(Table t) {
        // Même sémantique
        dealTurn(t);
    }


    public List<PlayerSession> evaluateShowdown(Table table) {
        List<Card> communityCards = table.getGameHand().getCommunityCards();
        List<PlayerSession> activePlayers = table.getActivePlayers();

        List<PlayerSession> winners = determineWinners(activePlayers, communityCards);

        double winnersGain = table.getGameHand().getPotAmount() / winners.size();

        for (PlayerSession winner : winners) {
            winner.deposit(winnersGain);
        }

        table.goNextState();

        return winners;
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

        if (action.getPlayerId() != gameHand.getCurrentTurnIndex()) return false;

        PlayerSession player = table.getActivePlayers().get(gameHand.getCurrentTurnIndex());

        // isLegal reste à false vraiment si le coup n'est pas autorisé (ex: jetons insuffisants)
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
            case ActionType.ALL_IN:
                isLegal = handleAllIn(table, action, player);
                break;
        }

        if (!isLegal) return false;
        player.setHasActed(true);

        if (isRoundFinished(table)) {
            table.goNextState();
            updateGameState(table);
        } else {
            int nextTurn = findNextPlayer(table, gameHand.getCurrentTurnIndex());
            gameHand.setCurrentTurnIndex(nextTurn);
        }

        return true;
    }


    /*======================================================================================
     *======================================== MAIN ========================================
     ======================================================================================= */

    public static void main(String[] args) {
        Table table = new Table();
        PokerEngine engine = new PokerEngine();

        for (int i = 0; i < 5; i++) table.addPlayer(new PlayerSession(i));

        engine.startNewHand(table);
        engine.dealFlop(table);
        engine.dealTurn(table);
        engine.dealRiver(table);

        List<PlayerSession> winners = engine.evaluateShowdown(table);

        for (PlayerSession p : winners) {
            System.out.println(p);
        }
    }
}


class Game {

    // ------- ATTRIBUTS ET METHODES DE CLASSE ---------
    static current_id = 0;
    
    static new_id() {
        return Game.current_id++;
    }

    // ------- ATTRIBUTS D'INSTANCE ---------
    #players;
    #nb_players;
    #board; // tableau de carte représentant les cartes au centre
    #id;

    // ------- CONSTRUCTEUR ---------
    constructor(players, cards) {
        this.#players = players;
        this.#nb_players = this.#players.length;
        this.#board = cards;
        this.#id = Card.new_id();
    }

    // ------- GETTERS ---------
    getPlayers() {
        return this.#players;
    }
    getCards() {
        return this.#board;
    }
    getId() {
        return this.#id;
    }

    setPlayers(players) {
        this.#players = players;
    }
    setBoard(board) {
        this.#board = board;
    }

    // ------- METHODES D'INSTANCE ---------

    // ajouter un joueur
    addPlayer(player) {
        this.#players.push(player);
    }
    // ajouter une carte à la board
    addCardToBoard(card) {
        this.#board.push(card);
    }

    // initialiser une partie
    init() {
        // donner des positions aux joueurs.
        let positions = getRandomSubarray([1, 2, 3, 4, 5, 6, 7], this.#nb_players);
        for (var i=0; i<this.#nb_players; i++) {
            this.#players[i].setPosition(positions[i]);
        }
    }
}

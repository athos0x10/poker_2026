
class Player {

    // ------- ATTRIBUTS ET METHODES DE CLASSE ---------
    static current_id = 0;
    
    static new_id() {
        return Card.current_id++;
    }

    // ------- ATTRIBUTS D'INSTANCE ---------
    #stack; // somme d'argent du joueur (qu'il a devant lui)
    #is_me;
    #position; // entier entre 1 et 7 (une des7 positions possibles)
    #cards;
    #bet;
    #id;

    // ------- CONSTRUCTEUR ---------
    constructor(id, stack, is_me=false) {
        this.#id = id;
        this.#stack = stack;
        this.#is_me = is_me;
    }

    // ------- GETTERS ---------
    getId() {
        return this.#id;
    }
    getPosition() {
        return this.#position;
    }
    getStack() {
        return this.#stack;
    }
    getCards() {
        return this.#cards;
    }
    getBet() {
        return this.#bet;
    }
    isMe() {
        return this.#is_me;
    }
    
    setCards(cards) {
        this.#cards = cards;
    }
    setStack(stack) {
        this.#stack = stack;
    }
    setPosition(position) {
        this.#position = position;
    }
    setBet(bet) {
        this.#bet = bet;
    }


    // ------- METHODES D'INSTANCE ---------
}

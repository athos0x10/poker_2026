const Suit = Object.freeze({
    SPADES: "spades",
    HEARTS: "hearts",
    CLUBS: "clubs",
    DIAMONDS: "diamonds"
});

const Rank = Object.freeze({
    ACE: "ace",
    TWO: "two",
    THREE: "three",
    FOUR: "four",
    FIVE: "five",
    SIX: "six",
    SEVEN: "seven",
    EIGHT: "eight",
    NINE: "nine",
    TEN: "ten",
    JACK: "jack",
    QUEEN: "queen",
    KING: "king"
});



class Card {

    // ------- ATTRIBUTS ET METHODES DE CLASSE ---------
    static current_id = 0;
    
    static new_id() {
        return Card.current_id++;
    }

    static getBackCardHTML(id) {
        return `<div id=${id} class="card back"></div>`;
    }

    // ------- ATTRIBUTS D'INSTANCE ---------
    #rank; // valeur (ace, 2, 3, ..., king)
    #suit; // couleur (pique, coeur, trefle, carreau)
    #is_back;
    #id;

    // ------- CONSTRUCTEUR ---------
    constructor(rank=null, suit=null) {
        if (rank == null || suit == null) {
            this.#is_back = true;
            this.#rank = null;
            this.#suit = null;
        } else {
            this.#is_back = false;
            this.#rank = rank;
            this.#suit = suit;
        }
        this.#id = Card.new_id();
    }

    // ------- GETTERS ---------
    getRank() {
        return this.#rank;
    }
    getSuit() {
        return this.#suit;
    }
    getId() {
        return this.#id;
    }
    isBack() {
        return this.#is_back;
    }

    // get name() {
    //     return this.#name;
    // }

    toHtml() {
        if (this.#is_back) {
            return Card.getBackCardHTML(this.#id);
        }
        return `<div class="card"><span class="${this.#rank}"></span> <span class="${this.#suit}"></span></div>`
    }

}

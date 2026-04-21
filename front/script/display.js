
function display_board(cards) {
    for (var i=0 ; i<cards.length ; i++) {
        const a = document.getElementById(`board_place_${i}`);
        console.log(cards[i])
        a.innerHTML = cards[i].toHtml();
    }
}

function display_pot(pot) {
    const total_pot = document.getElementById(`total_pot`);
    total_pot.innerText = pot;
}

function display_stack(player) {
    const player_place = document.getElementById(`player_place_${player.getPosition()}`);
    player_place.querySelector(".pot").innerHTML = player.getStack();
}

function display_player(player) {
    const player_place = document.getElementById(`player_place_${player.getPosition()}`);
    player_place.classList.remove("invisible");
    display_stack(player);
    // ensuite : afficher la photo de profil
}

function display_players(players) {
    for (var i=1 ; i<players.length ; i++) {
        display_player(players[i]);
    }
}

// mettre à jour l'affichage des cartes d'un joueur.
function display_player_cards(player) {
    let cards_container;
    if (player.isMe()) {
        cards_container = document.getElementById(`my_cards`);
    } else {
        cards_container = document.getElementById(`player_cards_${player.getPosition()}`);
        console.log(`cards_container initialized for other, ${cards_container === null}`)
    }

    cards_container.classList.remove("invisible");
    
    let temp = "";
    let cards = player.getCards();
    for (var i=0 ; i<cards.length ; i++) {
        temp += cards[i].toHtml();
    }
    
    cards_container.innerHTML = temp;
}





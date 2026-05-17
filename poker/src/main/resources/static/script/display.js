
function display_board(cards) {
  for (var i = 0; i < cards.length; i++) {
    const a = document.getElementById(`board_place_${i}`);
    if (!a) {
      console.warn(`board_place_${i} not found`);
      continue;
    }
    console.log('Rendering board card', i, cards[i]);
    a.innerHTML = cards[i].toHtml();
  }
}

function clear_board() {
  for (var i = 0; i < 5; i++) {
    const a = document.getElementById(`board_place_${i}`);
    if (!a) {
      console.warn(`board_place_${i} not found`);
      continue;
    } else {
      a.innerHTML = "";
    }
  }
  console.log("board cleared.");
}

function display_pot(pot) {
  const total_pot = document.getElementById(`total_pot`);
  if (!total_pot) {
    console.warn('total_pot element not found');
    return;
  }
  total_pot.innerText = pot;
}

// Map pour stocker les joueurs autres que moi
const otherPlayers = {};

function display_players_from_server(playersData) {
  if (!playersData || playersData.length === 0) return;
  console.log('Displaying players:', playersData);

  playersData.forEach((p) => {
    if (p.id !== playerId) {
      const pos = p.seatNumber || 1;
      otherPlayers[p.id] = {data: p, position: pos};
      const player_place = document.getElementById(`player_place_${pos}`);
      if (player_place) {
        player_place.classList.remove('invisible');
        const potElem = player_place.querySelector('.pot');
        if (potElem) potElem.innerText = p.stack;
        console.log(`Displayed player ${p.id} at position ${pos}`);
      } else {
        console.warn(`player_place_${pos} not found`);
      }
    }
  });
}

function display_stack(player) {
  const player_place = player.isMe() ?
      document.getElementById('my_place') :
      document.getElementById(`player_place_${player.getPosition()}`);
  if (!player_place) return;
  const potElem = player_place.querySelector('.pot');
  if (potElem) {
    potElem.innerHTML = player.getStack();
  }
}

function display_player(player) {
  const player_place = player.isMe() ?
      document.getElementById('my_place') :
      document.getElementById(`player_place_${player.getPosition()}`);
  if (!player_place) return;
  player_place.classList.remove('invisible');
  display_stack(player);
  // ensuite : afficher la photo de profil
}

function display_players(players) {
  for (var i = 1; i < players.length; i++) {
    display_player(players[i]);
  }
}

// mettre à jour l'affichage des cartes d'un joueur.
function display_player_cards(player) {
  console.log('display_player_cards called for player', player.getId());
  let cards_container;
  if (player.isMe()) {
    cards_container = document.getElementById(`my_cards`);
  } else {
    cards_container =
        document.getElementById(`player_cards_${player.getPosition()}`);
  }

  if (!cards_container) {
    console.error(`Cards container not found for player`, player);
    return;
  }

  cards_container.classList.remove('invisible');

  let temp = '';
  let cards = player.getCards();
  if (!cards || cards.length === 0) {
    console.warn('No cards to display for player', player.getId());
    return;
  }
  for (var i = 0; i < cards.length; i++) {
    console.log('Adding card HTML:', cards[i]);
    temp += cards[i].toHtml();
  }

  cards_container.innerHTML = temp;
  console.log('Cards rendered for player', player.getId());
}


// Sélectionne tous les boutons
const buttons = document.querySelectorAll('#buttons .button');

// stocker les fonctions onclick d'origine
const originalOnClicks = Array.from(buttons).map(button => button.onclick);

// desactiver les boutons
function disable_buttons() {
  document.getElementById("buttons").classList.add("disabled");
  buttons.forEach(button => {
    // button.classList.add('disabled');
    button.onclick = null; // supprime l'onclick
    button.style.pointerEvents = 'none';
  });
  console.log("BOUTONS DESACTIVEEEEEEEEEEEEEEEEEEEEEEEEEEEESSSSSSSSSSSSSSSSSSSSSSSSSSS");
}

// activer les boutons
function enable_buttons() {
  document.getElementById("buttons").classList.remove("disabled");
  buttons.forEach((button, index) => {
    // button.classList.remove('disabled');
    button.onclick = originalOnClicks[index]; // retablit l'onclick
    button.style.pointerEvents = 'auto';
  });
  console.log("BOUTONS ACTIVEEEEEEEEEEEEEEEEEEEEEEEEEEEESSSSSSSSSSSSSSSSSSSSSSSSSSS");
}
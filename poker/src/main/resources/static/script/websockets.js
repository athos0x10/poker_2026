let stompClient;
var tableId = 1;

// Génération des données de session du joueur
var playerId = null;
var seatNumber = null;
var playerName = null;
var initialStack = 1000;
let myPlayer;


/**
 * Initialise la connexion SockJS et STOMP vers le serveur
 */
function connect() {
  if (!playerId) {
    console.error('Impossible de se connecter : playerId non défini');
    showActionMessage('Session non valide, reconnectez-vous.', true);
    return;
  }

  if (!seatNumber || seatNumber < 1 || seatNumber > 7) {
    seatNumber = parseInt(localStorage.getItem('poker_seat') || '0', 10);
    if (!seatNumber || seatNumber < 1 || seatNumber > 7) {
      seatNumber = Math.floor(Math.random() * 7) + 1;
      localStorage.setItem('poker_seat', seatNumber);
    }
  }

  console.log(`Connecting to STOMP server for playerId=${playerId} seatNumber=${
      seatNumber}`);

  // Connexion au endpoint configuré côté Spring Boot
  const socket = new SockJS(`/poker-game?playerId=${playerId}`);
  stompClient = Stomp.over(socket);

  stompClient.connect(
      {},
      (frame) => {
        console.log('Connected to STOMP server:', frame);

        // 1. Abonnement aux annonces publiques de la table
        stompClient.subscribe('/topic/table', (message) => {
          console.log('Received /topic/table message');
          handleServerMessage(JSON.parse(message.body));
        });

        // 2. Abonnement à la file privée du joueur (Cartes cachées, etc.)
        stompClient.subscribe('/user/queue/private', (message) => {
          console.log('Received /user/queue/private message');
          handleServerMessage(JSON.parse(message.body));
        });

        // Une fois connecté, on demande à rejoindre la table
        sendJoin();
      },
      (error) => {
        console.error('STOMP connection error:', error);
      });
}

/**
 * Envoie l'action de rejoindre la table de poker
 */
function sendJoin() {
  if (!stompClient || !stompClient.connected) {
    console.error('Impossible d’envoyer join : pas connecté.');
    return;
  }

  console.log(`Sending join request for tableId=${tableId} playerId=${
      playerId} seat=${seatNumber}`);

  // Initialisation locale du joueur
  myPlayer = new Player(playerId, initialStack, true);
  myPlayer.setPosition(seatNumber);
  display_player(myPlayer);

  const request = {
    tableId: tableId,
    playerId: playerId,
    playerName: `Player${playerId}`,
    initialStack: initialStack,
    seatNumber: seatNumber
  };

  stompClient.send('/app/join', {}, JSON.stringify(request));

  // Petit délai pour laisser le serveur traiter le "join" avant de demander
  // l'état de la table
  setTimeout(sendInfosRequest, 200);
}

/**
 * Demande les informations actuelles de la table (Joueurs connectés, pot, etc.)
 */
function sendInfosRequest() {
  if (!stompClient || !stompClient.connected) return;

  console.log(`Sending infos_req for playerId=${playerId}`);
  stompClient.send('/app/action', {}, JSON.stringify({
    tableId: tableId,
    playerId: playerId,
    action: 'infos_req',
    amount: 0
  }));
}

/**
 * Fonction générique pour envoyer une action de jeu (fold, check, call, raise)
 */
function sendAction(action, amount = 0) {
  if (!stompClient || !stompClient.connected) {
    console.error('La connexion STOMP n’est pas ouverte.');
    showActionMessage('Connexion fermée ou introuvable.', true);
    return;
  }
  stompClient.send('/app/action', {}, JSON.stringify({
    tableId: tableId,
    playerId: playerId,
    action: action,
    amount: amount
  }));
}

function showStatusMessage(text) {
  const el = document.getElementById('status_message');
  if (el) el.innerText = text;
}

function showActionMessage(text, isError = false) {
  const el = document.getElementById('action_message');
  if (!el) return;
  el.innerText = text;
  el.style.color = isError ? 'crimson' : 'darkgreen';
}

function showBetMessage(text) {
  const el = document.getElementById('bet_message');
  if (!el) return;
  el.innerText = text;
}

function getCurrentPlayerIdFromInfos(message) {
  if (!message.players || message.players.length === 0) return null;
  const idx = message.currentTurn;
  if (idx == null || idx < 0 || idx >= message.players.length) return null;
  return message.players[idx].id;
}

function AmITheWinner(message) {
  const a = message.gameWinners
  if (!a) return null;
  return a.some(objet => objet.id === playerId);
}

function updateTurnStatus(message, end_game=false) {
  
  if (end_game) {
    const winner = AmITheWinner(message);
    if (winner) {
      showStatusMessage("Vous avez gagné !");
    } else {
      showStatusMessage("Vous avez perdu !");
    }
  } else {
    const currentPlayerId = getCurrentPlayerIdFromInfos(message);

    if (currentPlayerId == null) {
      showStatusMessage('Tour inconnu');
      return;
    }
    if (currentPlayerId === playerId) {
      showStatusMessage('C’est ton tour ! in updateTurnStatus');
      enable_buttons(); // activer les boutons d'action
    } else {
      showStatusMessage(`C’est le tour du joueur ${currentPlayerId}`);
    }
  }
}

// Actions de jeu de poker rapides
function fold() {
  sendAction('fold', 0);
}
function check() {
  sendAction('check', 0);
}
function call() {
  sendAction('call', 0);
}
function quit() {
  sendAction('quit', 0);
}

function raise() {
  const amount = parseFloat(prompt(
      'Montant total à miser (doit être supérieur à la mise actuelle)', '50'));
  if (isNaN(amount) || amount <= 0) {
    showActionMessage('Montant de relance invalide.', true);
    return;
  }
  sendAction('raise', amount);
}

/**
 * Parse une chaîne de caractères reçue du serveur pour en faire un objet Card
 * Exemple attendu : "[Ace, Spades]" ou format similaire à découper
 */
function parseCard(cardString) {
  if (!cardString || typeof cardString !== 'string') {
    console.warn('Invalid card string:', cardString, 'returning back card');
    return new Card();
  }

  const sanitized = cardString.trim();
  const match =
      sanitized.match(/^[\[{]?(?<rank>[^,]+),(?<suit>[^\]}]+)[\]}]?$/);
  if (!match) {
    console.warn('Card parse failed for string:', cardString);
    return new Card();
  }

  const rankRaw = match.groups.rank.trim().toUpperCase();
  const suitRaw = match.groups.suit.trim().toLowerCase();

  const rankMap = {
    'AS': 'ace',
    'A': 'ace',
    'ROI': 'king',
    'K': 'king',
    'DAME': 'queen',
    'Q': 'queen',
    'VALET': 'jack',
    'J': 'jack',
    'DIX': 'ten',
    '10': 'ten',
    'NEUF': 'nine',
    '9': 'nine',
    'HUIT': 'eight',
    '8': 'eight',
    'SEPT': 'seven',
    '7': 'seven',
    'SIX': 'six',
    '6': 'six',
    'CINQ': 'five',
    '5': 'five',
    'QUATRE': 'four',
    '4': 'four',
    'TROIS': 'three',
    '3': 'three',
    'DEUX': 'two',
    '2': 'two'
  };

  const suitMap = {
    'pique': 'spades',
    'coeur': 'hearts',
    'trefle': 'clubs',
    'carreau': 'diamonds',
    'spades': 'spades',
    'hearts': 'hearts',
    'clubs': 'clubs',
    'diamonds': 'diamonds'
  };

  const rank = rankMap[rankRaw] || rankRaw.toLowerCase();
  const suit = suitMap[suitRaw] || suitRaw.toLowerCase();
  console.log(`Parsed card: raw=(${rankRaw},${suitRaw}) -> (${rank},${suit})`);
  return new Card(rank, suit);
}

/**
 * Routeur central de messages reçus depuis le serveur WebSocket
 */
function handleServerMessage(message) {
  console.log('Server message received:', message);

  // Sécurité : s'assurer que myPlayer existe au besoin
  if (!myPlayer && playerId) {
    myPlayer = new Player(playerId, initialStack, true);
    myPlayer.setPosition(seatNumber);
  }

  switch (message.type) {
    case 'send_hand':
      console.log("erase board");
      clear_board();
      console.log('Received send_hand with cards:', message.cards_ids);
      showActionMessage('Tu as reçu tes cartes.', false);
      const handCards = message.cards_ids.map(parseCard);
      myPlayer.setCards(handCards);
      display_player_cards(myPlayer);
      break;

    case 'infos_res':
      console.log(`Received infos_res, pot=${message.pot}, board=${
          message.board?.length ||
          0}, players=${message.players?.length || 0}`);
      display_pot(message.pot);
      updateTurnStatus(message);

      if (message.board && message.board.length > 0) {
        display_board(message.board.map(parseCard));
      }

      display_players_from_server(message.players);

      if (message.players) {
        const me = message.players.find((p) => p.id === playerId);
        if (me) {
          myPlayer.setStack(me.stack);
          display_stack(myPlayer);
          const callAmount = Math.max(0, message.highestBet - me.bet);
          showBetMessage(`Stack: ${me.stack} | Mise actuelle: ${
              me.bet} | à suivre: ${callAmount}`);
        }
      }
      break;

    case 'update_board':
      console.log('Received update_board');
      display_board(message.new_cards.map(parseCard));
      break;

    case 'update_bet_stack':
      if (message.user_id === playerId) {
        myPlayer.setStack(message.currentStack);
        display_stack(myPlayer);
      }
      break;

    case 'update_pot':
      display_pot(message.amount);
      break;

    case 'your_turn':
      console.log('Your turn!');
      showStatusMessage('C’est ton tour ! in handleServerMessage');
      showActionMessage('Ton action est attendue.', false);
      enable_buttons(); // activer les boutons d'action
      break;

    case 'ack':
      console.log('Action acknowledgement:', message.detail);
      showActionMessage(message.detail, message.i !== 1);
      disable_buttons(); // desactiver les boutons d'actions
      break;

    case 'user_quit':
      console.log('Joueur quitté :', message.user_id);
      // help me find the player position to hide in the next line
      const playerToHide = Object.values(otherPlayers).find(
          (p) => p.data.id === message.user_id);
      if (playerToHide) {
        const pos = playerToHide.position;
        const player_place = document.getElementById(`player_place_${pos}`);
        if (player_place) {
          player_place.classList.add('invisible');
          console.log(`Player ${message.user_id} at position ${pos} is now invisible`);
        }
      }

      break;

    case 'reveal_cards':
      if (message.finalBoard) {
        display_board(message.finalBoard.map(parseCard));
      }
      console.log('Showdown - Winners:', message.gameWinners);
      updateTurnStatus(message, end_game=true);
      break;

    default:
      console.log('Message inconnu reçu :', message);
  }
}

// Lancement automatique de la connexion au chargement de la page
window.addEventListener('load', connect);

/**
 * ZONE DE TEST : Permet d'afficher des cartes à l'écran sans le serveur
 * Appelable depuis la console du navigateur : window.testDisplayCards()
 */
window.testDisplayCards = function() {
  console.log('TEST: Displaying test cards...');
  if (!myPlayer) {
    myPlayer = new Player(playerId, initialStack, true);
    myPlayer.setPosition(seatNumber);
    display_player(myPlayer);
  }
  const testCards = [new Card('ace', 'spades'), new Card('king', 'hearts')];
  myPlayer.setCards(testCards);
  display_player_cards(myPlayer);
  console.log('TEST: Test cards should be displayed now');
};
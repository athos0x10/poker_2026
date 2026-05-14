
const socket = new WebSocket('ws://localhost:8080/poker-game');

socket.onopen = (event) => {
  console.log('Connexion WebSocket établie !');
  // Tu peux envoyer un message ici si besoin
  socket.send(JSON.stringify({ type: 'init', data: 'Hello Server!' }));
};

socket.onmessage = (event) => {
  console.log('Message reçu du serveur :', event.data);
  // Traite les données reçues (ex: JSON.parse(event.data))
};

socket.onclose = (event) => {
  console.log('Connexion WebSocket fermée :', event.code, event.reason);
};

socket.onerror = (error) => {
  console.error('Erreur WebSocket :', error);
};

function sendMessage(message) {
  if (socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(message));
  } else {
    console.error("La connexion WebSocket n'est pas ouverte.");
  }
}

sendMessage({ type: 'chat', text: 'Bonjour !' });


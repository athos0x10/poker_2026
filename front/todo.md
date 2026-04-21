# TODO + notes

## TODO

- raise n prend en compte la somme qui a déjà été misée (donc si on a déjà misé 150, et qu'on raise 1000, on ne rajoute que 850)
- les joueuers ne peuvent pas être placés aléatoirement (il faut respecter l'ordre)
- bloquer les fonctions dont les boutons sont grisés.



## communication user/server

### server -> user
| Nom  | type | syntaxe |
|----------------|--------|---------|
| envoyer cartes | privé  | send_hand cards[] |
| envoyer maj board | broadcast  | update_board new_cards[] |
| maj bet and stack d'un joueur | broadcast | update_bet_stack bet stack |
| erreur/validation d'action | privé | ack i (i=0 -> ok i=1 -> ko) |
| envoyer tout le jeu (réponse de infos_req) | privé | infos_res infos |
| reveal cards et winner | broadcast | reveal cards winner |
| notifier d'un tour | privé | your_turn |
| un joueur quitte la table | broadcast | user_quit user_id |
| | | |
*pas encore sûr :*
| envoyer la meilleure combinaison actuelle | privé | best_cards cards_id[] |

### user -> server
| Nom  | syntaxe |
|------|-----|
| fold | fold  |
| check | check  |
| call  | call  |
| allin  | allin  |
| raise n  | raise n |
| demander les infos  | infos_req |
| quitter la partie  | quit |


## Description du jeu

### Description d'un tour

```
SERVEUR dévoile des cartes
    // au début : les deux cartes de chaque joueur,
    // ensuite : 3 cartes dans le board
    // ensuite : 1 carte dans le board
    // ensuite : 1 carte dans le board
    // EXCEPTION : All-In pour tous les joueurs non-couché : 
    //              on dévoile toutes les cartes restantes.

pour chaque JOUEUR :
    SERVEUR notifie JOUEUR que c'est à lui de jouer.
    
    JOUEUR choisit une action : check, fold, call, bet, raise 
    // bet et raise sont sensiblement identiques je crois
    
    JOUEUR envoie son coup (via websocket, privée (pas de broadcast))
    
    SERVEUR vérifie le coup
    // car potentiellement coup impossible : raise + que possible
    
    si coup ok:
        SERVEUR broadcast le coup et la mise à jour du jeu qui s'en suit.
    sinon:
        SERVEUR fait rejouer JOUEUR (ou le ban)
    finsi.
```

### Notes
A tout moment, un JOUEUR peut arriver dans la partie
-> il jouera qu'au prochain tour donc pas de modification du jeu.

A tout moment, un JOUEUR peut quitter une partie
-> ça revient à se coucher au prochain coup qu'il jouera, donc pas de modif du jeu.



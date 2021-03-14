var stompClient = null;
var mySessionId = "";
var myTeam = "";
var myRole = "";
var myPlayer = "";
var playerCardAreaClone = null;

function connect() {

	return new Promise((resolve, reject) => {
		var socket = new SockJS('/two-zooms-boom/game');

		if (stompClient == null || !stompClient.connected) {
			stompClient = Stomp.over(socket);
			stompClient.connect({}, function (frame) {
				console.log('Session Id: ' + socket._transport.url);
				console.log('Connected: ' + frame);
				stompClient.subscribe('/topic/lobby', function (lobbyMessage) {
					updateLobbyListing(JSON.parse(lobbyMessage.body));
				});
				stompClient.subscribe('/user/queue/lobby', function (lobbyMessage) {
					updateLobbyListing(JSON.parse(lobbyMessage.body));
				});
				stompClient.subscribe('/topic/game/roundTimer', function (roundTimerMessage) {
					updateRoundTimerArea(JSON.parse(roundTimerMessage.body));
				});
				stompClient.subscribe('/topic/game/reset', function (resetMessage) {
					resetGame();
				});
				stompClient.subscribe('/user/queue/game/player', function (playerMessage) {
					updatePlayerArea(JSON.parse(playerMessage.body));
				});
				stompClient.subscribe('/user/queue/game/revealedPlayer', function (revealedPlayerMessage) {
					revealPlayer(JSON.parse(revealedPlayerMessage.body));
				});
				stompClient.subscribe('/topic/game/start', function (gameStartMessage) {
					readyPlayerLists(JSON.parse(gameStartMessage.body));
				});

				resolve('Connected!');

			}, function (err) {
				console.log(err);
				reject('Unconnected');
			});
		}
		else {
			resolve('Already connected!');
		}
	});
}

function disconnect() {

	document.getElementById('join-lobby').disabled = false;
	document.getElementById('disconnect').disabled = true;

	if (stompClient != null) {
		stompClient.disconnect();
	}
	console.log("Disconnected");
}

function resetGame() {

	location.reload();
};

function readyPlayerLists(gameStartMessage) {
	var playerListings = gameStartMessage.playerListings;
	var playerListing = document.getElementById("player-listing");
	var recipientList = document.getElementById('recipient-list');
	var revealTeam = document.getElementById('reveal-team');
	var revealRole = document.getElementById('reveal-role');
	var defaultOption = document.createElement('option');

	removeAllChildNodes(recipientList);
	removeAllChildNodes(playerListing);

	revealTeam.disabled = false;
	revealRole.disabled = false;

	defaultOption.value = '-';
	defaultOption.innerHTML = 'Select Recipient';
	recipientList.appendChild(defaultOption);

	var currentPlayer;

	playerListings.forEach(function (player) {

		var option = document.createElement('option');
		option.value = player.playerId;
		option.text = player.playerName;

		if(player.playerId!=mySessionId){
			//Set player listings to only other current players
			updatePlayerStatuses(player);
			recipientList.appendChild(option);
		}
		else {

			currentPlayer = player;
		}
	});

	recipientList.value = '-';

	playerListing.appendChild(document.createElement('hr'));
	currentPlayer.message = "Me: Team ("+myTeam+") Role ("+myRole+")";
	updatePlayerStatuses(currentPlayer);

}

function updatePlayerStatuses(playerListingMessage) {
	var playerListing = document.getElementById("player-listing");
	var div = document.createElement('div');
	var playerIcon = document.createElement('img');
	var listingText = document.createElement('span');
	
	playerIcon.id = 'ico_' + playerListingMessage.playerId;
	playerIcon.src = 'images/player_icon.png';
	playerIcon.classList.add('player-icon');

	listingText.id = 'txt_' + playerListingMessage.playerId;
	listingText.appendChild(document.createTextNode(playerListingMessage.message));

	div.id = playerListingMessage.playerId;
	div.style.wordWrap = 'break-word';
	div.appendChild(playerIcon);
	div.appendChild(listingText);

	playerListing.appendChild(div);

}

function updatePlayerArea(player) {
	myPlayer = player;
	playerCardAreaClone = document.querySelector('#player-card-area').cloneNode(true);

	var playerRoom = document.getElementById('player-room-link');
	var playerTeam = document.getElementById('player-team');
	var playerRole = document.getElementById('player-role');
	var playerRows = document.querySelectorAll('.player-area-row');
	var cardFlipper = document.getElementById('player-card-flipper');
	var cardsElements = document.querySelectorAll('div.card');
	var teamColours = {
		"Blue": "#3a56a5",
		"Red": "#ee1c26",
		"Grey": "#918585"
	}
	var flipClass = "flip-card-reveal";

	mySessionId = player.sessionId;
	myTeam = player.teamRole.team;
	myRole = player.teamRole.role;

	playerRoom.innerHTML = player.room.name + " (click to join)";
	playerRoom.href = player.room.url;
	playerTeam.innerHTML = myTeam;
	playerRole.innerHTML = myRole;

	playerRows.forEach(row => {
		row.style.backgroundColor = teamColours[player.teamRole.team];
	});

	cardsElements.forEach(card => {
		card.style.backgroundColor = teamColours[player.teamRole.team];
	});

	var nextCardId = (cardFlipper.classList.contains(flipClass)) ? 'card-front' : 'card-back';
	var nextCardElem = document.getElementById(nextCardId);
	var nextCardImg = document.createElement('div');
	var nextCardText = document.createElement('div');

	removeAllChildNodes(nextCardElem);

	nextCardImg.classList.add('role-' + player.teamRole.role.toLowerCase());
	nextCardImg.title = player.teamRole.roleDescription;
	nextCardText.classList.add('role-label');
	nextCardText.appendChild(document.createTextNode(player.teamRole.role));
	nextCardElem.appendChild(nextCardImg);
	nextCardElem.appendChild(nextCardText);

	cardFlipper.classList.toggle(flipClass);
}


function updateRoundTimerArea(roundTimerMessage) {
	var roundNo = document.getElementById('roundNo');
	var roundTimer = document.getElementById('roundTimer');

	roundNo.innerHTML = roundTimerMessage.roundNo;
	roundTimer.innerHTML = roundTimerMessage.remainingTime;
}

function revealMyRole(revealType) {

	var playerList = document.getElementById('recipient-list');
	recipientSessionId = playerList.value;

	stompClient.send("/app/game/revealPlayer", {}, JSON.stringify({ 'recipientSessionId': recipientSessionId, 'revealType': revealType}));
}

function revealPlayer(revealedPlayer) {

	var playerListingIcon = document.getElementById('ico_' + revealedPlayer.revealedPlayerSessionId);
	var playerListingText = document.getElementById('txt_' + revealedPlayer.revealedPlayerSessionId);

	var teamColour = revealedPlayer.revealedPlayerTeam.toLowerCase();
	playerListingIcon.src = 'images/player_icon_' + teamColour + '.png';
	playerListingText.innerHTML = revealedPlayer.revealedPlayerMessage;
}

function removeAllChildNodes(parent) {
	while (parent.firstChild) {
		parent.removeChild(parent.lastChild);
	}
}

function refreshRole(){
	var playerCardArea = document.querySelector('#player-card-area').cloneNode(true);
	playerCardArea.innerHTML = playerCardAreaClone.innerHTML;
	updatePlayerArea(myPlayer);
}
var stompClient = null;
var mySessionId = "";

function connect() {

	return new Promise((resolve, reject) => {
		var socket = new SockJS('/2ZaaB');

		if (stompClient == null || !stompClient.connected) {
			stompClient = Stomp.over(socket);
			stompClient.connect({}, function (frame) {
				console.log('Session Id: ' + socket._transport.url);
				console.log('Connected: ' + frame);
				stompClient.subscribe('/topic/lobby', function (lobbyMessage) {
					updateLobbyArea(JSON.parse(lobbyMessage.body));
				});
				stompClient.subscribe('/topic/game/roundTimer', function (roundTimerMessage) {
					updateRoundTimerArea(JSON.parse(roundTimerMessage.body));
				});
				stompClient.subscribe('/user/queue/game/player', function (playerMessage) {
					updatePlayerArea(JSON.parse(playerMessage.body));
				});
				stompClient.subscribe('/user/queue/game/playerReveal', function (playerRevealMessage) {
					updateLobbyRole(JSON.parse(playerRevealMessage.body));
				});
				stompClient.subscribe('/user/queue/game/playerNames', function (recipientListMessage) {
					updateRecipientList(JSON.parse(recipientListMessage.body));
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

function updateRecipientList(recipientList){
	var revealTeam = document.getElementById('reveal-team');
	var revealRole = document.getElementById('reveal-role');
	var playerList = document.getElementById('player-list');
	var defaultOption = document.createElement('option');
	
	revealTeam.disabled = false;
	revealRole.disabled = false;
	removeAllChildNodes(playerList);
	
	defaultOption.value = '-';
	defaultOption.innerHTML = 'Select Recipient';
	playerList.appendChild(defaultOption);

	recipientList.forEach(function(recipient) {
		var option = document.createElement('option');
		option.value = recipient.playerId;
		option.text = recipient.playerName;
		playerList.appendChild(option);
	});

	playerList.value = '-';
}

function updatePlayerArea(player) {
	mySessionId = player.sessionId;
	var playerRoom = document.getElementById('player-room-link');
	var playerTeam = document.getElementById('player-team');
	var playerRole = document.getElementById('player-role');

	var playerRows = document.querySelectorAll('.player-area-row');
	var cardFlipper = document.getElementById('player-card-flipper');
	var cardsElements = document.querySelectorAll('div.card');
	var teamColours = {
		"Blue Team": "#3a56a5",
		"Red Team": "#ee1c26",
		"Grey Team":"#918585"
	}
	var flipClass = "flip-card-reveal";

	playerRoom.innerHTML = player.room.name + " (click to join)";
	playerRoom.href = player.room.url;
	playerTeam.innerHTML = player.teamRole.team;
	playerRole.innerHTML = player.teamRole.role;

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

	var playerList = document.getElementById('player-list');
	var playerTeam = document.getElementById('player-team');
	var playerRole = document.getElementById('player-role');

	recipientSessionId = playerList.value;
	team = playerTeam.innerHTML;
	role = (revealType == 2) ? playerRole.innerHTML : "";

	stompClient.send("/app/game/playerReveal", {}, JSON.stringify({ 'recipientSessionId': recipientSessionId, 'team': team, 'role': role }));
}


function removeAllChildNodes(parent) {
	while (parent.firstChild) {
		parent.removeChild(parent.lastChild);
	}
}
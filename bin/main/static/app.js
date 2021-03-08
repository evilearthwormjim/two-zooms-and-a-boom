var stompClient = null;


function connect() {
	var socket = new SockJS('/lobby');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Session Id: '+socket._transport.url); 
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/lobby', function(lobbyMessage) {
			updateLobbyArea(JSON.parse(lobbyMessage.body));
		});
		stompClient.subscribe('/user/queue/role', function(playerMessage) {
			updatePlayerArea(JSON.parse(playerMessage.body));
		});
	}, function(err) {
		console.log(err);
	});
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function joinLobby() {
	var playerName = document.getElementById('player-name').value;
	stompClient.send("/app/lobby", {}, JSON.stringify({ 'playerName': playerName }));
}

function updateLobbyArea(lobbyMessage) {
	var lobbyListing = document.getElementById('lobby-listing');
	var p = document.createElement('p');
	p.style.wordWrap = 'break-word';
	p.appendChild(document.createTextNode("(" + lobbyMessage.loggedInTime + ") " + lobbyMessage.playerName + ": "
		+ lobbyMessage.assignedRoom));

	lobbyListing.appendChild(p);
	lobbyListing.scrollTop = lobbyListing.scrollHeight - lobbyListing.clientHeight;
}


function updatePlayerArea(player) {

	var playerRoom = document.getElementById('player-room-link');
	var playerTeam = document.getElementById('player-team');
	var playerRole = document.getElementById('player-role');

	var playerRows = document.querySelectorAll('.player-area-row');
	var cardFlipper = document.getElementById('player-card-flipper');
	var cardsElements = document.querySelectorAll('div.card');
	var teamColours = {
		"Blue Team":"#3a56a5",
		"Red Team": "#ee1c26"
	}
	var flipClass = "flip-card-reveal";

	playerRoom.innerHTML = player.room.name;
	playerRoom.href = player.room.url;
	playerTeam.innerHTML = player.team;
	playerRole.innerHTML = player.role;
	
	playerRows.forEach(row => {
		row.style.backgroundColor = teamColours[player.team];
	});

	cardsElements.forEach(card => {
		card.style.backgroundColor = teamColours[player.team];
	});

	var nextCardId = (cardFlipper.classList.contains(flipClass))? 'card-front':'card-back';
	var nextCardElem = document.getElementById(nextCardId);
	var nextCardImg = document.createElement('div');
	var nextCardText = document.createElement('div');

	while (nextCardElem.firstChild) {
		nextCardElem.removeChild(nextCardElem.lastChild);
	}

	nextCardImg.classList.add('role-'+player.role.toLowerCase());
	nextCardText.classList.add('role-label');
	nextCardText.appendChild(document.createTextNode(player.role));
	nextCardElem.appendChild(nextCardImg);
	nextCardElem.appendChild(nextCardText);

	cardFlipper.classList.toggle(flipClass);
}


function startGame() {

	var roomA = document.getElementById('room-a-url').value;
	var roomB = document.getElementById('room-b-url').value;
	params = "roomAURL="+ roomA + "&roomBURL="+ roomB;
	const Http = new XMLHttpRequest();
	const url = 'http://localhost:8080/game/start';
	
	Http.open("POST", url, true);
	Http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	Http.send(params);

	Http.onreadystatechange = (e) => {
		console.log(e.responseText)
	}

}
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
	p.appendChild(document.createTextNode(lobbyMessage.playerName + ": "
		+ lobbyMessage.assignedRoom + " (" + lobbyMessage.loggedInTime + ")"));

	lobbyListing.appendChild(p);
}


function updatePlayerArea(player) {
	var playerArea = document.getElementById('player-area');
	var playerRoom = document.getElementById('player-room-link');
	var playerTeam = document.getElementById('player-team');
	var playerRole = document.getElementById('player-role');

	playerRoom.innerHTML = player.room.name;
	playerRoom.href = player.room.url;
	playerTeam.innerHTML = player.team;
	playerRole.innerHTML = player.role;
	
	assignmentColour = player.team == "Blue Team"? "blue":"red";
	
	playerArea.style.backgroundColor = assignmentColour;

	if (player.team == "Blue Team"){
		
		
	}

	
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
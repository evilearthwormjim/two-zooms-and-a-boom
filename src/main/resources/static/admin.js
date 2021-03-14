var stompClient = null;


function connect() {
	var socket = new SockJS('/two-zooms-boom/game');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		console.log('Session Id: ' + socket._transport.url);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/lobby', function (lobbyMessage) {
			updateLobbyListing(JSON.parse(lobbyMessage.body));
		});

		stompClient.send("/app/lobby/enter", {}, JSON.stringify({ 'playerName': 'Admin' }));

		document.getElementById("connect").disabled = true;
		document.getElementById("disconnect").disabled = false;
		document.querySelectorAll('.game-button').forEach(function (element) {
			element.disabled = false;
		});
		document.getElementById("game-roles").disabled = false;

	}, function (err) {
		console.log(err);
	});
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	console.log("Disconnected");
	document.getElementById("connect").disabled = false;
	document.getElementById("disconnect").disabled = true;
	document.querySelectorAll('.game-button').forEach(function (element) {
		element.disabled = true;
	});
	document.getElementById("game-roles").disabled = true;
}

function startGame() {

	var lobbyUrl = document.getElementById('lobby-url');
	var roomAUrl = document.getElementById('room-a-url');
	var roomBUrl = document.getElementById('room-b-url');

	lobbyUrl.classList.remove('error');
	roomAUrl.classList.remove('error');
	roomBUrl.classList.remove('error');

	if (lobbyUrl.value == "" || roomAUrl.value == "" || roomBUrl.value == "") {

		if (lobbyUrl.value == "")
			lobbyUrl.classList.add('error');
		if (roomBUrl.value == "")
			roomBUrl.classList.add('error');
		if (roomAUrl.value == "")
			roomAUrl.classList.add('error');
		if (roomBUrl.value == "")
			roomBUrl.classList.add('error');

		return false;
	}

	var gameRoles = document.getElementById('game-roles');
	var selectedRoles = [...gameRoles.selectedOptions]
		.map(option => option.value);

	var rooms = [{ name: "Lobby", url: lobbyUrl.value }, { name: "Room A", url: roomAUrl.value }, { name: "Room B", url: roomBUrl.value }]
	var startGameMessage = { selectedRoles: selectedRoles, rooms: rooms};

	stompClient.send("/app/game/start", {}, JSON.stringify(startGameMessage));
	stompClient.send("/app/lobby/message", {}, JSON.stringify({ 'playerName': 'Admin', message: 'Player Roles Assigned' }));
}

function resetGame() {

	stompClient.send("/app/game/reset", {}, "reset");
}

function startRound(roundNo) {

	stompClient.send("/app/game/startRound", {}, JSON.stringify(roundNo));
}


function selectRoles(){

	var gameRoles = document.getElementById('game-roles');
	var colourCounter = {'blue-team': 0, 'red-team':0, 'grey-team': 0};

	var selectedRoles = [...gameRoles.selectedOptions]
		.forEach((option) => {
			colourCounter[option.classList[0]]++;
		});

	document.getElementById('blue-role-count').innerHTML = 'b['+colourCounter['blue-team']+"]"
	document.getElementById('red-role-count').innerHTML = 'r['+colourCounter['red-team']+"]"
	document.getElementById('grey-role-count').innerHTML = 'g['+colourCounter['grey-team']+"]"
}
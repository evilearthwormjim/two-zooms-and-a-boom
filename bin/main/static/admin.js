var stompClient = null;


function connect() {
	var socket = new SockJS('/2ZaaB');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		console.log('Session Id: ' + socket._transport.url);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/lobby', function (lobbyMessage) {
			updateLobbyArea(JSON.parse(lobbyMessage.body));
		});

		stompClient.send("/app/lobby/enter", {}, JSON.stringify({ 'playerName': 'Admin' }));

		document.getElementById("connect").disabled = true;
		document.getElementById("disconnect").disabled = false;
		document.querySelectorAll('.game-button').forEach(function (element) {
			element.disabled = false;
		});

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
}

function startGame() {

	var roomAUrl = document.getElementById('room-a-url');
	var roomBUrl = document.getElementById('room-b-url');
	roomAUrl.classList.remove('error');
	roomBUrl.classList.remove('error');

	if (roomAUrl.value == "" || roomBUrl.value == "") {

		if (roomAUrl.value == "")
			roomAUrl.classList.add('error');
		if (roomBUrl.value == "")
			roomBUrl.classList.add('error');

		return false;
	}

	var rooms = [{ name: "Room A", url: roomAUrl.value }, { name: "Room B", url: roomBUrl.value }]

	stompClient.send("/app/game/start", {}, JSON.stringify(rooms));
	stompClient.send("/app/lobby/message", {}, JSON.stringify({ 'playerName': 'Admin', message: 'Game Started...' }));
}

function resetGame() {

	stompClient.send("/app/game/reset", {}, "reset");
}

function startRound(roundNo) {

	stompClient.send("/app/game/startRound", {}, JSON.stringify(roundNo));
}

function joinLobby() {
	var playerName = document.getElementById('player-name');
	removeAllChildNodes(document.getElementById('lobby-listing'));

	if (playerName.value == "") {
		playerName.classList.toggle('error');
	}
	else {

		playerName.classList.remove('error');
		const connection = connect();

		//Connect to websocket server first
		connection.then(function (fulfilled) {
			console.log(fulfilled);
			document.getElementById('join-lobby').disabled = true;
			document.getElementById('disconnect').disabled = false;
			stompClient.send("/app/lobby/enter", {}, JSON.stringify({ 'playerName': playerName.value }));
		});

	}
}

function updateLobbyListing(lobbyListingMessage) {
	var playerName = document.getElementById('player-name');
	var lobbyListing = document.getElementById('lobby-listing');

	if (playerName) {
		playerName.classList.remove('error');

		if (lobbyListingMessage.nameAlreadyTaken) {
			document.getElementById('join-lobby').disabled = false;
			document.getElementById('disconnect').disabled = true;
			playerName.classList.add('error');
			playerName.value = 'Name already taken';
			disconnect();
			return false;
		}
	}
	var div = document.createElement('div');
	var lobbyText = document.createElement('span');

	lobbyText.innerHTML = lobbyListingMessage.message;
	div.style.wordWrap = 'break-word';
	div.appendChild(lobbyText);

	lobbyListing.appendChild(div);
	lobbyListing.scrollTop = lobbyListing.scrollHeight - lobbyListing.clientHeight;

}


function sendLobbyMessage(playerName){

	var message = document.getElementById("lobby-message").value;

	if(message!=""){

		stompClient.send("/app/lobby/message", {}, JSON.stringify({ 'playerName': playerName, message: message }));
	}
}

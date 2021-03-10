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

function updateLobbyArea(lobbyMessage) {
	var playerName = document.getElementById('player-name');
	var lobbyListing = document.getElementById('lobby-listing');
	var lobbyRow = document.getElementById(lobbyMessage.playerId);

	if (playerName) {
		playerName.classList.remove('error');
		playerName.placeHolder = 'Choose a nickname';

		if (lobbyMessage.nameAlreadyTaken) {

			playerName.classList.add('error');
			playerName.placeHolder = 'Name already taken';
			return false;
		}
	}
	var div = document.createElement('div');
	var playerIcon = document.createElement('img');
	var lobbyText = document.createElement('span');

	playerIcon.id = 'ico_' + lobbyMessage.playerId;
	playerIcon.src = 'images/player_icon.png';
	playerIcon.classList.add('player-icon');

	lobbyText.id = 'txt_' + lobbyMessage.playerId;
	lobbyText.appendChild(document.createTextNode(lobbyMessage.message));

	div.id = lobbyMessage.playerId;
	div.style.wordWrap = 'break-word';
	div.appendChild(playerIcon);
	div.appendChild(lobbyText);

	lobbyListing.appendChild(div);
	lobbyListing.scrollTop = lobbyListing.scrollHeight - lobbyListing.clientHeight;

}


function updateLobbyRole(revealedPlayer) {

	var lobbyPlayerText = document.getElementById('txt_' + revealedPlayer.revealedPlayerSessionId);
	var lobbyPlayerIcon = document.getElementById('ico_' + revealedPlayer.revealedPlayerSessionId);

	var teamColour = revealedPlayer.revealedPlayerTeam.toLowerCase();
	lobbyPlayerIcon.src = 'images/player_icon_' + teamColour + '.png';
	lobbyPlayerText.innerHTML = revealedPlayer.revealedPlayerMessage;
}

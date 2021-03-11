package two.zooms.boom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

	@Autowired
	public GameService gameService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private Thread thread;
	
	private static final String PLAYER_LISTING_TEXT = "%s: Team (%s) - Role (%s)";

	@MessageMapping("/game/start")
	@SendTo("/topic/game/start")
	public GameStartMessage startGame(Room[] rooms) throws Exception {
		Room roomA = new Room(rooms[0].name, rooms[0].url);
		Room roomB = new Room(rooms[1].name, rooms[1].url);

		resetTimer();
		
		gameService.assignTeamRoles(roomA, roomB);
		HashMap<String, Player> players = gameService.getPlayers();
		//Send list of other players
		GameStartMessage gameStartMessage = new GameStartMessage();
		
		for (String k : players.keySet()) {
			Player player = players.get(k);
			LobbyMessage lobbyMessage = new LobbyMessage();
			lobbyMessage.playerId = k;
			lobbyMessage.playerName = player.name;
			lobbyMessage.message = String.format(PLAYER_LISTING_TEXT, lobbyMessage.playerName, "?", "?");
			gameStartMessage.playerListings.add(lobbyMessage);
			
			// Publish roles to individual players
			SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			headerAccessor.setLeaveMutable(true);
			headerAccessor.setSessionId(k);
			simpMessagingTemplate.convertAndSendToUser(k, "/queue/game/player", 
					player,
					headerAccessor.getMessageHeaders());
		}
		
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		LobbyMessage lobbyMessage = new LobbyMessage();
		
		lobbyMessage.message = String.format("(%s) Admin: <a href=\"%s\" target=\"_blank\"> Room A Link</a> created", 
				time,
				rooms[0].url);
		
		simpMessagingTemplate.convertAndSend("/topic/lobby", lobbyMessage);
		
		lobbyMessage.message = String.format("(%s) Admin: <a href=\"%s\" target=\\\"_blank\\\"> Room B Link</a> created", 
				time,
				rooms[1].url);
		
		simpMessagingTemplate.convertAndSend("/topic/lobby", lobbyMessage);
		
		return gameStartMessage;
	}

	
	@MessageMapping("/game/reset")
	@SendTo("/topic/game/reset")
	public String resetGame(String resetGame) {
		
		gameService.setPlayers(new HashMap<>());
		resetTimer();
		
		return resetGame;
	}
	
	@MessageMapping("/game/revealPlayer")
	public void revealPlayer(@Header("simpSessionId") String sessionId, RevealedPlayerMessage revealedPlayerMessage) {
		
		Player revealedPlayer = gameService.findPlayerById(sessionId);
		revealedPlayerMessage.revealedPlayerSessionId = sessionId;
		revealedPlayerMessage.revealedPlayerName = revealedPlayer.name;
		revealedPlayerMessage.revealedPlayerRole = revealedPlayer.teamRole.role;
		revealedPlayerMessage.revealedPlayerTeam = revealedPlayer.teamRole.team;
		
		if(RevealedPlayerMessage.REVEAL_TYPE_TEAM == revealedPlayerMessage.revealType) {
			revealedPlayerMessage.revealedPlayerMessage = 
						String.format(PLAYER_LISTING_TEXT, 
								revealedPlayer.name, 
								revealedPlayer.teamRole.team, "?");
		}
		else if (RevealedPlayerMessage.REVEAL_TYPE_ROLE == revealedPlayerMessage.revealType){
			//Role is both team and role
			revealedPlayerMessage.revealedPlayerMessage = 
					String.format(PLAYER_LISTING_TEXT, 
							revealedPlayer.name, 
							revealedPlayer.teamRole.team, 
							revealedPlayer.teamRole.role);
		}

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setLeaveMutable(true);
		headerAccessor.setSessionId(revealedPlayerMessage.recipientSessionId);

		simpMessagingTemplate.convertAndSendToUser(revealedPlayerMessage.recipientSessionId, "/queue/game/revealedPlayer",
				revealedPlayerMessage, headerAccessor.getMessageHeaders());
	}

	
	@MessageMapping("/game/startRound")
	public void startRound(int roundNo) throws Exception {

		LobbyMessage lobbyMessage = new LobbyMessage();
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		lobbyMessage.message = String.format("(%s) Admin: Round %s started", time, roundNo);
		simpMessagingTemplate.convertAndSend("/topic/lobby", lobbyMessage);
		RoundTimerMessage roundTimerMessage = new RoundTimerMessage(roundNo);
		if (thread != null) {
			thread.interrupt();
		}
		this.runCountdownTimer(roundTimerMessage);
	}

	private void resetTimer() {
		//Reset the timer
		if (thread != null) {
			thread.interrupt();
			simpMessagingTemplate.convertAndSend("/topic/game/roundTimer", new RoundTimerMessage());
		}
	}
	
	private void runCountdownTimer(RoundTimerMessage roundTimerMessage) throws InterruptedException {

		Runnable runnable = () -> {
			try {
				int roundTime = roundTimerMessage.roundTime;
				int numberOfMinutes;
				int numberOfSeconds;

				for (int i = 0; i < roundTime; i++) {
					TimeUnit.SECONDS.sleep(1);
					roundTimerMessage.roundTime--;
					numberOfMinutes = ((roundTimerMessage.roundTime % 86400) % 3600) / 60;
					numberOfSeconds = ((roundTimerMessage.roundTime % 86400) % 3600) % 60;
					roundTimerMessage.remainingTime = String.format("%02d", numberOfMinutes) + ":"
							+ String.format("%02d", numberOfSeconds);

					simpMessagingTemplate.convertAndSend("/topic/game/roundTimer", roundTimerMessage);
				}
				
				String time = new SimpleDateFormat("HH:mm").format(new Date());
				LobbyMessage roundOverMessage = new LobbyMessage();
				roundOverMessage.message = 
						String.format("(%s) Admin: Round %s has ended. Swap Hostages!", time, roundTimerMessage.roundNo);
				
				simpMessagingTemplate.convertAndSend("/topic/lobby", roundOverMessage);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		thread = new Thread(runnable);
		thread.start();

	}

}
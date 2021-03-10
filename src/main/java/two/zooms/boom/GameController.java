package two.zooms.boom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

	@MessageMapping("/game/startGame")
	public void startGame(Room[] rooms) throws Exception {
		Room roomA = new Room(rooms[0].name, rooms[0].url);
		Room roomB = new Room(rooms[1].name, rooms[1].url);

		//Reset the timer
		if (thread != null) {
			thread.interrupt();
			simpMessagingTemplate.convertAndSend("/topic/game/roundTimer", new RoundTimerMessage());
		}

		gameService.assignTeamRoles(roomA, roomB);
		HashMap<String, Player> players = gameService.getPlayers();
		
		// Publish roles to players
		for (String k : players.keySet()) {
			SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			headerAccessor.setLeaveMutable(true);
			headerAccessor.setSessionId(k);
			simpMessagingTemplate.convertAndSendToUser(k, "/queue/game/player", 
					players.get(k),
					headerAccessor.getMessageHeaders());
			
			//Send list of other players
			List<RecipientListMessage> playerNames = new ArrayList<>();
			players.entrySet().stream().filter(p-> !p.getKey().equals(k)).forEach(elem -> {
				RecipientListMessage playerName = new RecipientListMessage(elem.getKey(), elem.getValue().name);
				playerNames.add(playerName);
			});
			
			simpMessagingTemplate.convertAndSendToUser(k, "/queue/game/playerNames", 
					playerNames,
					headerAccessor.getMessageHeaders());
		}
	}

	@MessageMapping("/game/playerReveal")
	public void playerReveal(@Header("simpSessionId") String sessionId, RevealedPlayerMessage revealedPlayerMessage) {
		String time = new SimpleDateFormat("HH:mm").format(new Date());

		Player revealedPlayer = gameService.findPlayerById(sessionId);
		revealedPlayerMessage.revealTime = time;
		revealedPlayerMessage.revealedPlayerName = revealedPlayer.name;
		revealedPlayerMessage.revealedPlayerSessionId = sessionId;

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setLeaveMutable(true);
		headerAccessor.setSessionId(revealedPlayerMessage.recipientSessionId);

		simpMessagingTemplate.convertAndSendToUser(revealedPlayerMessage.recipientSessionId, "/queue/game/playerReveal",
				revealedPlayerMessage, headerAccessor.getMessageHeaders());
	}

	@MessageMapping("/game/startRound")
	public void startRound(int roundNo) throws Exception {

		RoundTimerMessage roundTimerMessage = new RoundTimerMessage(roundNo);
		if (thread != null) {
			thread.interrupt();
		}
		this.runCountdownTimer(roundTimerMessage);
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		thread = new Thread(runnable);
		thread.start();

	}

}
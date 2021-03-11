package two.zooms.boom;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class LobbyController {

	@Autowired
	public GameService gameService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	private static String WAITING_MSG = "Waiting for game to start...";
	
	@MessageMapping("/lobby/enter")
	public void enterLobby(@Header("simpSessionId") String sessionId, LobbyMessage message) throws Exception {
	    String time = new SimpleDateFormat("HH:mm").format(new Date());
	    System.out.print(message.playerName+" joining the game");
	    Player player = gameService.findPlayerById(sessionId);
	    
	    boolean playerNameTaken = gameService.isPlayerNameTaken(message.playerName);
	    
	    message.loggedInTime = time;
	    message.assignedRoom = WAITING_MSG;
	    message.playerId = sessionId;
	    
		if(playerNameTaken) {
			message.nameAlreadyTaken = playerNameTaken && (player == null || !player.sessionId.equals(sessionId));
			message.message = "Player with that name has already joined the game";
			simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/lobby", message);
		}
		else {
			if(!"Admin".equalsIgnoreCase(message.playerName)) {
				player = gameService.registerPlayer(sessionId, message.playerName);
			}
		    message.message = String.format("(%s) %s: Has joined the game", time, message.playerName);
		    simpMessagingTemplate.convertAndSend("/topic/lobby", message);
		}
	    
	}
	
	@MessageMapping("/lobby/message")
	@SendTo("/topic/lobby")
	public LobbyMessage messageLobby(@Header("simpSessionId") String sessionId, LobbyMessage lobbyMessage) throws Exception {
	
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		String messageText = lobbyMessage.message;
		Player player = gameService.findPlayerById(sessionId);
		String name = (player==null)? lobbyMessage.playerName:player.name;
		
		lobbyMessage.message = String.format("(%s) %s: %s", time, name, messageText);
		return lobbyMessage;
	
	}
	
	@EventListener
	public void onDisconnectEvent(SessionDisconnectEvent event) {
		
		//Sign out player
	    Player player = gameService.removePlayer(event.getSessionId());
	    if(player != null) {
		    String time = new SimpleDateFormat("HH:mm").format(new Date());
		    LobbyMessage lobbyMessage = new LobbyMessage(player.name, "", time);
		    lobbyMessage.message = String.format("(%s) %s: Has left the game", time, player.name);
		    simpMessagingTemplate.convertAndSend("/topic/lobby", lobbyMessage);
	    }
	}
}
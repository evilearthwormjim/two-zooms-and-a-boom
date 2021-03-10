package two.zooms.boom;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class LobbyController {

	@Autowired
	public GameService gameService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	private static String WAITING_MSG = "Waiting for game to start...";
	
	@MessageMapping("/lobby/enter")
	@SendTo("/topic/lobby")
	public LobbyMessage enterLobby(@Header("simpSessionId") String sessionId, LobbyMessage message) throws Exception {
	    String time = new SimpleDateFormat("HH:mm").format(new Date());
	    System.out.print(message.playerName+" joining the game");
	    Player player = gameService.findPlayerById(sessionId);
	    
	    boolean playerNameTaken = gameService.isPlayerNameTaken(message.playerName);
	    
	    message.loggedInTime = time;
	    message.assignedRoom = WAITING_MSG;
	    message.playerId = sessionId;
	    
		if(playerNameTaken) {
			message.nameAlreadyTaken = playerNameTaken && !player.sessionId.equals(sessionId);
			message.message = "Player with that name has already joined the game";
		}
		else {
			if(!"Admin".equalsIgnoreCase(message.playerName)) {
				player = gameService.registerPlayer(sessionId, message.playerName);
			}
		    message.message = String.format("(%s) %s: Has joined the game", time, message.playerName);
		}
		
		
	    
		return message;
	}
	
	@MessageMapping("/lobby/message")
	@SendTo("/topic/lobby")
	public LobbyMessage messageLobby(@Header("simpSessionId") String sessionId, LobbyMessage lobbyMessage) throws Exception {
	
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		String messageText = lobbyMessage.message;
		
		lobbyMessage.message = String.format("(%s) %s: %s", time, lobbyMessage.playerName, messageText);
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
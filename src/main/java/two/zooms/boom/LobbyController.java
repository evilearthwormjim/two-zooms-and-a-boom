package two.zooms.boom;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class LobbyController {

	@Autowired
	public GameService gameService;
	
	
	private static String WAITING_MSG = "Waiting for game to start...";
	
	@MessageMapping("/lobby")
	@SendTo("/topic/lobby")
	public LobbyMessage enterLobby(@Header("simpSessionId") String sessionId, Principal principal, LobbyMessage message) throws Exception {
	    String time = new SimpleDateFormat("HH:mm").format(new Date());

	    message.loggedInTime = time;
	    message.assignedRoom = WAITING_MSG;
	    
	    boolean playerNameTaken = gameService.isPlayerNameTaken(message.playerName);
	    
		if(playerNameTaken) {
			
			message.nameAlreadyTaken = playerNameTaken;
			message.assignedRoom = "Player with that name has already in the lobby";
		}
		else {
			
			boolean newPlayerRegistered = gameService.registerPlayer(sessionId, message.playerName);
		}
		
		return message;
	}

	@EventListener
	public void onDisconnectEvent(SessionDisconnectEvent event) {
	    gameService.removePlayer(event.getSessionId());
	}
}
package two.zooms.boom;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/game")
public class GameController {

	private static final String ROOM_A = "Room A";
	private static final String ROOM_B = "Room B";
	
	@Autowired
	public GameService gameService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@GetMapping("/")
	public String index() {
	    return "index";
	}
	
	@PostMapping("/start")
	public ResponseEntity<String> startGame(@RequestParam("roomAURL") String roomAURL, @RequestParam("roomBURL") String roomBURL) throws Exception {
		Room roomA = new Room(ROOM_A, roomAURL);
		Room roomB = new Room(ROOM_B, roomAURL);
		
		gameService.assignTeamRoles(roomA, roomB);
		
		HashMap<String, Player> players = gameService.getPlayers();
		
		for(String k : players.keySet()) {
			SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			headerAccessor.setLeaveMutable(true);
			headerAccessor.setSessionId(k);
			simpMessagingTemplate.convertAndSendToUser(k, "/queue/role", players.get(k), headerAccessor.getMessageHeaders());
		}
		
		return new ResponseEntity<String>("Game On!", HttpStatus.OK);
	}

}
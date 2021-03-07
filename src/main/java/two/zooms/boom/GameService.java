package two.zooms.boom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class GameService {

	private static final String WAITING_MSG = "Waiting for game to start...";
	private static final String RED_TEAM = "Red Team";
	private static final String BLUE_TEAM = "Blue Team";
	private static final String ROLE_PRESIDENT = "President";
	private static final String ROLE_BOMB = "Bomb";
	private static final String ROLE_MOOK = "Mook";
	
	private HashMap<String, Player> players = new HashMap<>();
	
	public boolean registerPlayer(String sessionId, String playerName) {

		boolean isNewPlayer = !players.containsKey(sessionId);

		Player player = new Player();
		player.name = playerName;
		player.team = WAITING_MSG;
		player.role = WAITING_MSG;

		players.put(sessionId, player);
		
		return isNewPlayer;
	}

	public void assignTeamRoles(Room roomA, Room roomB) {
		
		int noPlayers = players.size();
		Random generator = new Random();
		
		List<Room> roomAssigments = new ArrayList<>();
		List<String> roleAssigments = new ArrayList<>();
		List<String> teamAssigments = new ArrayList<>();
		
		roleAssigments.add(ROLE_PRESIDENT);
		roleAssigments.add(ROLE_BOMB);
		
		for(int i=0; i<noPlayers; i++) {
			
			if(roomAssigments.size()<noPlayers) {
				roomAssigments.add((i<(noPlayers/2))?roomA:roomB);
			}
			if(teamAssigments.size()<noPlayers) {
				teamAssigments.add((i<(noPlayers/2))?RED_TEAM:BLUE_TEAM);
			}
			if(roleAssigments.size()<noPlayers) {
				roleAssigments.add(ROLE_MOOK);
			}
			
		}
		
		Collections.shuffle(roomAssigments, new Random(noPlayers));
		Collections.shuffle(roleAssigments, new Random(noPlayers));
		
		for(String k : players.keySet()) {
			
			Player player = players.get(k);
			
			int randomRoom = generator.nextInt(roomAssigments.size());
			Room room = roomAssigments.get(randomRoom);
			roomAssigments.remove(randomRoom);
			player.room = room;
			
			int randomRole = generator.nextInt(roleAssigments.size());
			String role = roleAssigments.get(randomRole);
			roleAssigments.remove(randomRole);
			player.role = role;
			
			int randomTeam = generator.nextInt(teamAssigments.size());
			String team = teamAssigments.get(randomTeam);
			teamAssigments.remove(randomTeam);
			player.team = team;
		}
		
		
	}
	
	public boolean isPlayerNameTaken(String playerName) {
		
		boolean playerNameExistsAlready = players.values().stream().anyMatch(player -> player.name.equals(playerName));

		return playerNameExistsAlready;
	}
	
	public boolean removePlayer(String sessionId) {
		
		Player player = players.remove(sessionId);
		
		return player!=null;
		
	}
	
	public HashMap<String, Player> getPlayers() {
		return players;
	}

	public void setPlayers(HashMap<String, Player> players) {
		this.players = players;
	}
	
	
}

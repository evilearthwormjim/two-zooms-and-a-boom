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
	private static final String RED_TEAM = "Red";
	private static final String BLUE_TEAM = "Blue";
	private static final String GREY_TEAM = "Grey Team";
	private static final String ROLE_PRESIDENT = "President";
	private static final String ROLE_BOMB = "Bomb";
	private static final String ROLE_MOOK = "Mook";
	private static final String ROLE_GAMBLER = "Gambler";

	private HashMap<String, Player> players = new HashMap<>();

	public Player registerPlayer(String sessionId, String playerName) {

		Player player = new Player();
		player.name = playerName;
		player.sessionId = sessionId;
		players.put(sessionId, player);

		return player;
	}

	public List<TeamRole> generateTeamRoles(String[] requestedRoles, int noPlayers) {

		List<TeamRole> teamRoles = new ArrayList<>();

		//Basic Roles
		TeamRole president = new TeamRole(BLUE_TEAM, ROLE_PRESIDENT);
		TeamRole bomb = new TeamRole(RED_TEAM, ROLE_BOMB);
		TeamRole gambler = new TeamRole(GREY_TEAM, ROLE_GAMBLER);

		teamRoles.add(president);
		teamRoles.add(bomb);
		
		if (noPlayers % 2 == 1) {
			teamRoles.add(gambler);
		}

		int addedRoles = teamRoles.size();

		//Fill up remaining player slots with basic mooks
		for (int i = 0; i < (noPlayers - addedRoles); i++) {
			String team = (i % 2 == 0) ? BLUE_TEAM : RED_TEAM;
			teamRoles.add( new TeamRole(team, ROLE_MOOK));
		}

		return teamRoles;
	}

	public void assignTeamRoles(Room roomA, Room roomB) {

		int noPlayers = players.size();
		Random generator = new Random();

		List<Room> roomAssigments = new ArrayList<>();

		for (int i = 0; i < noPlayers; i++) {

			if (roomAssigments.size() < noPlayers) {
				roomAssigments.add((i % 2 == 0) ? roomA : roomB);
			}
		}

		List<TeamRole> teamRoles = generateTeamRoles(null, noPlayers);

		Collections.shuffle(roomAssigments, new Random(generator.nextInt(noPlayers)));
		Collections.shuffle(teamRoles, new Random(generator.nextInt(noPlayers)));

		for (String k : players.keySet()) {

			Player player = players.get(k);

			int randomRoom = generator.nextInt(roomAssigments.size());
			Room room = roomAssigments.get(randomRoom);
			roomAssigments.remove(randomRoom);
			player.room = room;

			int randomRole = generator.nextInt(teamRoles.size());
			TeamRole teamRole = teamRoles.get(randomRole);
			teamRoles.remove(randomRole);
			player.teamRole = teamRole;

		}

	}

	public boolean isPlayerNameTaken(String playerName) {

		boolean playerNameExistsAlready = players.values().stream().anyMatch(player -> player.name.equals(playerName));

		return playerNameExistsAlready;
	}

	public Player removePlayer(String sessionId) {

		Player player = players.remove(sessionId);

		return player;

	}

	public Player findPlayerById(String id) {
		
		return players.get(id);
	}
	
	public HashMap<String, Player> getPlayers() {
		return players;
	}

	public void setPlayers(HashMap<String, Player> players) {
		this.players = players;
	}

}

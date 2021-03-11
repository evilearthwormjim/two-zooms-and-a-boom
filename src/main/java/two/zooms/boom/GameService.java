package two.zooms.boom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class GameService {

	private static final String RED_TEAM = "Red";
	private static final String BLUE_TEAM = "Blue";
	private static final String GREY_TEAM = "Grey";
	
	private static final String ROLE_PRESIDENT = "President";
	private static final String ROLE_PRESIDENT_DESCRIPTION = "President wins if they are "
			+ "not in the same room as a the bomb at the end of the game";

	private static final String ROLE_BOMB = "Bomb";
	private static final String ROLE_BOMB_DESCRIPTION = "The Bomb wins if they are "
			+ " in the same room as a the President at the end of the game";
	
	private static final String ROLE_MOOK = "Mook";
	private static final String ROLE_MOOK_DESCRIPTION = "The Mook wins depending on their team colour. "
			+ "Blue wants the President to survive. Red wants President dead";
	
	private static final String ROLE_GAMBLER = "Gambler";
	private static final String ROLE_GAMBLER_DESCRIPTION = "At the end of the last round, before all players reveal their cards, you must publicly announce which\r\n"
			+ "team (Red Team, Blue Team, or neither) you think won the game. Win only if you are correct.";

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
		TeamRole president = new TeamRole(BLUE_TEAM, ROLE_PRESIDENT, ROLE_PRESIDENT_DESCRIPTION);
		TeamRole bomb = new TeamRole(RED_TEAM, ROLE_BOMB, ROLE_BOMB_DESCRIPTION);
		TeamRole gambler = new TeamRole(GREY_TEAM, ROLE_GAMBLER, ROLE_GAMBLER_DESCRIPTION);

		teamRoles.add(president);
		teamRoles.add(bomb);
		
		if (noPlayers % 2 == 1) {
			teamRoles.add(gambler);
		}

		int addedRoles = teamRoles.size();

		//Fill up remaining player slots with basic mooks
		for (int i = 0; i < (noPlayers - addedRoles); i++) {
			String team = (i % 2 == 0) ? BLUE_TEAM : RED_TEAM;
			teamRoles.add( new TeamRole(team, ROLE_MOOK, ROLE_MOOK_DESCRIPTION));
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

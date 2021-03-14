package two.zooms.boom.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import two.zooms.boom.Room;
import two.zooms.boom.roles.TeamRole;
import two.zooms.boom.roles.TeamRoleService;

@Service
public class GameService {

	@Autowired
	private TeamRoleService teamRoleService;
	
	private HashMap<String, Player> players = new HashMap<>();

	public Player registerPlayer(String sessionId, String playerName) {

		Player player = new Player();
		player.name = playerName;
		player.sessionId = sessionId;
		players.put(sessionId, player);

		return player;
	}

	public void assignTeamRoles(String[] selectedRoles, Room roomA, Room roomB) {

		int noPlayers = players.size();
		Random generator = new Random();

		List<Room> roomAssigments = new ArrayList<>();

		for (int i = 0; i < noPlayers; i++) {

			if (roomAssigments.size() < noPlayers) {
				roomAssigments.add((i % 2 == 0) ? roomA : roomB);
			}
		}

		List<TeamRole> teamRoles = teamRoleService.selectTeamRolesForGame(selectedRoles, noPlayers);

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

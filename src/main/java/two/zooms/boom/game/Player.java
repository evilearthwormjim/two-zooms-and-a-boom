package two.zooms.boom.game;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import two.zooms.boom.Room;
import two.zooms.boom.roles.TeamRole;

public class Player {

	public Player() {};
	
	private UUID uuid = UUID.randomUUID();
	public String sessionId;
	
	@Getter @Setter
	public String name;
	
	@Getter	@Setter
	public Room room;
	
	@Getter	@Setter
	public TeamRole teamRole;

	public UUID getUuid() {
		return uuid;
	}
	
}



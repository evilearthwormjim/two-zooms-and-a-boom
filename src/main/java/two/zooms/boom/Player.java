package two.zooms.boom;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

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



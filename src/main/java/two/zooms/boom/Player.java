package two.zooms.boom;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class Player {

	public Player() {};
	
	private UUID uuid = UUID.randomUUID();
	
	@Getter @Setter
	public String name;
	
	@Getter	@Setter
	public Room room;
	
	@Getter	@Setter
	public String team;
	
	@Getter	@Setter
	public String role;

	public UUID getUuid() {
		return uuid;
	}
	
}



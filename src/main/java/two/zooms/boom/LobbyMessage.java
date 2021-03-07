package two.zooms.boom;

import lombok.Getter;
import lombok.Setter;

public class LobbyMessage {

	public LobbyMessage() {};
	public LobbyMessage(String playerName, String assignedRoom, String loggedInTime) {
		super();
		this.playerName = playerName;
		this.assignedRoom = assignedRoom;
		this.loggedInTime = loggedInTime;
	}

	@Getter @Setter
	public String playerName;
	
	@Getter @Setter
	public boolean nameAlreadyTaken;
	
	@Getter @Setter
	public String assignedRoom;

	@Getter @Setter
	public String loggedInTime;
	
	@Getter @Setter
	public String roundNo;

}
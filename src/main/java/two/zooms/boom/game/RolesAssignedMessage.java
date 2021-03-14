package two.zooms.boom.game;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import two.zooms.boom.LobbyMessage;

public class RolesAssignedMessage {

	@Getter @Setter
	public String playerId;
	
	@Getter @Setter
	public List<LobbyMessage> playerListings = new ArrayList<>();
	
}
package two.zooms.boom;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class GameStartMessage {

	@Getter @Setter
	public String playerId;
	
	@Getter @Setter
	public List<LobbyMessage> playerListings = new ArrayList<>();
	
}
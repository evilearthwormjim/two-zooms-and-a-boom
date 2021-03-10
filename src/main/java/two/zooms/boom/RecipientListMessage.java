package two.zooms.boom;

import lombok.Getter;
import lombok.Setter;

public class RecipientListMessage {

	
	public RecipientListMessage(String playerId, String playerName) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
	}

	@Getter @Setter
	public String playerId;
	
	@Getter @Setter
	public String playerName;
	
}
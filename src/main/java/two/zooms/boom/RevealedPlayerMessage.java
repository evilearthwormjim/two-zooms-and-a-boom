package two.zooms.boom;

import lombok.Getter;
import lombok.Setter;

public class RevealedPlayerMessage extends TeamRole {
	
	@Getter @Setter
	public String revealedPlayerSessionId;
	
	@Getter @Setter
	public String recipientSessionId;

	@Getter @Setter
	public String revealedPlayerName;
	
	@Getter @Setter
	public String revealTime;
	
}

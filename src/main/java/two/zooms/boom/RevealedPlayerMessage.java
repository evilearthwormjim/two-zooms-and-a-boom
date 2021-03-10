package two.zooms.boom;

import lombok.Getter;
import lombok.Setter;

public class RevealedPlayerMessage extends TeamRole {
	
	final static int REVEAL_TYPE_TEAM = 1;
	final static int REVEAL_TYPE_ROLE = 2;
	
	@Getter @Setter
	public String recipientSessionId;

	@Getter @Setter
	public int revealType;
	
	@Getter @Setter
	public String revealedPlayerSessionId;
	
	@Getter @Setter
	public String revealedPlayerName;
	
	@Getter @Setter
	public String revealedPlayerTeam;
	
	@Getter @Setter
	public String revealedPlayerRole;
	
	@Getter @Setter
	public String revealedPlayerMessage;
	
}

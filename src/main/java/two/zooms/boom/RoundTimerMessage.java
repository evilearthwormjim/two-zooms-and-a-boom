package two.zooms.boom;

import lombok.Getter;
import lombok.Setter;

public class RoundTimerMessage {
	
	public RoundTimerMessage() {};
	
	public RoundTimerMessage(int roundNo) {
		super();
		this.roundNo = roundNo;
		this.roundTime = (4-roundNo)*60;
	}

	@Getter @Setter
	public int roundNo = 1;
	
	@Getter @Setter
	public int roundTime;
	
	@Getter @Setter
	public String remainingTime = "";

}

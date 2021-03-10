package two.zooms.boom;

import lombok.Getter;
import lombok.Setter;

public class TeamRole {
	
	public TeamRole() {}
	public TeamRole(String team, String role) {
		super();
		this.team = team;
		this.role = role;
	}

	@Getter @Setter
	public String team;
	
	@Getter @Setter
	public String role;

}

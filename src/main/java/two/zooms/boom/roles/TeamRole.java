package two.zooms.boom.roles;

import lombok.Getter;
import lombok.Setter;

public class TeamRole {
	
	public TeamRole() {}
	public TeamRole(String team, String role, String roleDescription) {
		super();
		this.team = team;
		this.role = role;
		this.roleDescription = roleDescription;
	}

	@Getter @Setter
	public String team;
	
	@Getter @Setter
	public String role;
	
	@Getter @Setter
	public String roleDescription;

}

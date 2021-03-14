package two.zooms.boom.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TeamRoleService {

	public List<TeamRole> selectTeamRolesForGame(String[] selectedRoles, int noPlayers){
		List<TeamRole> teamRoles = new ArrayList<>();
		
		Map<String, TeamRole> availableTeamRoles = TeamRoleDefinitions.initAvailableRoles();
		
		teamRoles.add(availableTeamRoles.get(TeamRoleDefinitions.ROLE_PRESIDENT));
		teamRoles.add(availableTeamRoles.get(TeamRoleDefinitions.ROLE_BOMB));
		
		if(noPlayers % 2 == 1) {
			teamRoles.add(availableTeamRoles.get(TeamRoleDefinitions.ROLE_GAMBLER));
		}
		
		int bluePlayers = 1;
		int redPlayers = 1;
		
		//Add additional roles selected by user
		for(int i=0; i<selectedRoles.length; i++) {
			if(noPlayers - teamRoles.size()>0) {
				TeamRole role = availableTeamRoles.get(selectedRoles[i]);
				teamRoles.add(role);
				
				if(role.team.equals(TeamRoleDefinitions.BLUE_TEAM)) {
					bluePlayers++;
				}
				else if(role.team.equals(TeamRoleDefinitions.RED_TEAM)) {
					redPlayers++;
				}
			}
			else {
				break;
			}
		} 
		
		//Fill remaining player slots with Mooks
		teamRoles.addAll(TeamRoleDefinitions.addMooks(noPlayers, bluePlayers, redPlayers));
		
		return teamRoles;
	}
}

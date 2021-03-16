package two.zooms.boom.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamRoleDefinitions {

	public static final String RED_TEAM = "Red";
	public static final String BLUE_TEAM = "Blue";
	public static final String GREY_TEAM = "Grey";
	
	public static final String ROLE_PRESIDENT = "President";
	public static final String ROLE_PRESIDENT_DESCRIPTION = "President wins if they are "
			+ "not in the same room as a the bomb at the end of the game";

	public static final String ROLE_BOMB = "Bomb";
	public static final String ROLE_BOMB_DESCRIPTION = "The Bomb wins if they are "
			+ " in the same room as a the President at the end of the game";
	
	public static final String ROLE_MOOK = "Mook";
	public static final String ROLE_MOOK_DESCRIPTION = "The Mook wins depending on their team colour. "
			+ "Blue wants the President to survive. Red wants President dead";
	
	public static final String ROLE_GAMBLER = "Gambler";
	public static final String ROLE_GAMBLER_DESCRIPTION = "At the end of the last round, before all players reveal their cards, you must publicly announce which\r\n"
			+ "team (Red Team, Blue Team, or neither) you think won the game. Win only if you are correct.";
	
	public static final String ROLE_AGENT = "Agent";
	public static final String ROLE_AGENT_DESCRIPTION = "You have the AGENT power: once per round, you may privately reveal your card to a player and force\r\n"
			+ "that player to card share with you. You must verbally say to the target player, “I’m using my AGENT\r\n"
			+ "power. You must card share with me.”";
	
	public static final String ROLE_AGORAPHOBE = "Agoraphobe";
	public static final String ROLE_AGORAPHOBE_DESCRIPTION = "You win as long as you never leave your initial room";
	
	public static final String ROLE_BOUNCER = "Bouncer";
	public static final String ROLE_BOUNCER_DESCRIPTION = "You have the BOUNCER power: if you are in a room that has more players than the other room,\r\n"
			+ "you may privately reveal your card to any player and verbally tell them, “Get out!” When you do, that\r\n"
			+ "player must immediately change rooms. The BOUNCER power does not work during the last round\r\n"
			+ "or between rounds.";
	
	
	
	
	private static Map<String, TeamRole> availableTeamRoles = new HashMap<>();
	
    public static Map<String, TeamRole> initAvailableRoles() {
    	
        //Mandatory roles
    	availableTeamRoles.put(ROLE_PRESIDENT, 
    			new TeamRole(BLUE_TEAM, 
    					ROLE_PRESIDENT, 
    					ROLE_PRESIDENT_DESCRIPTION));
    	
    	availableTeamRoles.put(ROLE_BOMB, 
    			new TeamRole(RED_TEAM, 
    					ROLE_BOMB, 
    					ROLE_BOMB_DESCRIPTION));
    	
    	availableTeamRoles.put(ROLE_GAMBLER, 
				new TeamRole(GREY_TEAM, 
						ROLE_GAMBLER, 
						ROLE_GAMBLER_DESCRIPTION));
    	
    	// Optional Roles
    	availableTeamRoles.put(ROLE_AGENT+BLUE_TEAM, 
    			new TeamRole(BLUE_TEAM, 
    					ROLE_AGENT, 
    					ROLE_AGENT_DESCRIPTION));
    	
    	availableTeamRoles.put(ROLE_AGENT+RED_TEAM, 
    			new TeamRole(RED_TEAM, 
    					ROLE_AGENT, 
    					ROLE_AGENT_DESCRIPTION));
    	
    	availableTeamRoles.put(ROLE_AGORAPHOBE, 
    			new TeamRole(GREY_TEAM, 
    					ROLE_AGORAPHOBE, 
    					ROLE_AGORAPHOBE_DESCRIPTION));
    	
    	availableTeamRoles.put(ROLE_BOUNCER+BLUE_TEAM, 
    			new TeamRole(BLUE_TEAM, 
    					ROLE_BOUNCER, 
    					ROLE_GAMBLER_DESCRIPTION));
    	
    	availableTeamRoles.put(ROLE_BOUNCER+RED_TEAM, 
    			new TeamRole(RED_TEAM, 
    					ROLE_BOUNCER, 
    					ROLE_BOUNCER_DESCRIPTION));
    	
    	
    	
        return Collections.unmodifiableMap(availableTeamRoles);
    }
    
    
    public static List<TeamRole> addMooks(int noPlayers, int bluePlayers, int redPlayers){
    	
    	List<TeamRole> teamRoles = new ArrayList<>();
    	
    	int slotsTofill = noPlayers - bluePlayers - redPlayers;
    			
    	//Fill up remaining player slots with basic mooks
		for (int i = 0; i < slotsTofill; i++) {
			
			String team = "";
			
			if(bluePlayers < redPlayers) {
				team = BLUE_TEAM;
				bluePlayers++;
			}
			else {
				team = RED_TEAM;
				redPlayers++;
			}
			
			teamRoles.add( new TeamRole(team, ROLE_MOOK, ROLE_MOOK_DESCRIPTION));
		}
		
		return teamRoles;
    }
}

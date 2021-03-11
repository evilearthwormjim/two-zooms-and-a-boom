## About The Project


Two Booms and a Room is a social deduction party game by [Tuesday Night Games](https://www.tuesdayknightgames.com/tworoomsandaboom) where players are divided into 2 rooms and must exchange hostages before the timer runs out.

This is a version for anyone who wants to play remotely, using Zoom (or Google Meet etc). Each player will get their team role assigned privately and a link to one of the two rooms. 

This currently has 4 roles: President, Bomb, Gambler, Mook

## Getting Started

### Prerequisites
This is a Spring Boot 5 app built with gradle. 
You will need Java 11 JRE

### Starting the application
Download the repo, and run:

```
./gradelew bootRun
```


## Running the game

- Everyone participating should go to the player screen: `http://localhost:8080/two-zooms-boom`
- Whoever is running the game also should bring up the admin screen: `http://localhost:8080/two-zooms-boom/admin.html`

Once all the players have joined, the admin can click `Assign Roles` on the admin screen - this will reveal to each player their team, role, and which room link they should go to.

Once ready, the admin can start the round 1 timer, and continue until round 3 is complete.

During the game, players may secretly reveal either their team, or their role. Revealing a role will include team too.

### Player Screen
![Game Running](https://github.com/evilearthwormjim/two-zooms-and-a-boom/blob/master/README/role-assigned.png)

### Admin Screen
![Admin Screen](https://github.com/evilearthwormjim/two-zooms-and-a-boom/blob/master/README/admin.png)

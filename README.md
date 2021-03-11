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

- Players go to `http://localhost:8080/two-zooms-boom`
- Whoever is running the game can go to `http://localhost:8080/two-zooms-boom/admin.html`

Once all the players have joined, the administrator can click `Start Game` - this will assign teams and roles that cna

### Player Screen
![Game Start](https://github.com/evilearthwormjim/two-zooms-and-a-boom/blob/master/README/game-start.png)

![Game Running](https://github.com/evilearthwormjim/two-zooms-and-a-boom/blob/master/README/role-assigned.png)

### Admin Screen
![Admin Screen](https://github.com/evilearthwormjim/two-zooms-and-a-boom/blob/master/README/admin.png)

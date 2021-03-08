## About The Project


Two Booms and a Room is a social deduction party game by [Tuesday Night Games](https://www.tuesdayknightgames.com/tworoomsandaboom) where players are divided into 2 rooms and must exchange hostages before the timer runs out.

This is a version for anyone who wants to play remotely, using Zoom (or Google Meet etc). Each player will get their role assigned privately and a link to one of the two rooms. 

## Getting Started

### Prerequisites
This is a Spring Boot 5 app built with gradle. 

### Starting the application
Download the repo, and run:

```
./gradelew bootRun
```


## Running the game

- Players go to `http://localhost:8080`
- Whoever is running the game can go to `http://localhost:8080/admin.html`

Once all the players have joined, the administrator can click `Start Game` and roles will be reassigned

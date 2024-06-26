<h1 align="center">
    Quests Of The Round Table
  <br>
  <br>
  <p align="center">
  	<img width="460" height="300" src="https://user-images.githubusercontent.com/44578113/166127909-791b4146-25d0-44d0-9bde-bee958cbd330.png">
	</p>
</h1>

## Introduction

A multiplayer medieval card game networked across multiple machines.

The game comes with two decks of cards. The story deck determines what happens on each player's turn; a quest, a tournament, or a special event.
The adventure deck holds the allies, foes, weapons, tests, and amour cards which get dealt and drawn into players' hands.
Players begin the game as squires with a lowly battle strength of five and, by earning shields, try to be the first to progress through knight and champion knight to become a knight of the round table.

A copy of the rules can be [found here](https://www.fgbradleys.com/rules/rules4/Quests%20of%20the%20Round%20Table%20-%20rules.pdf)

#### Table of Contents
- [Softwares used](#softwares)
- [1st iteration](#first)
- [2nd iteration](#second)
- [3rd iteration](#third)
- [4th iteration](#fourth)
- [Final iteration](#final)
- [Tweaks and Features](#tweaks)
- [How to Run](#run)
- [Team Roles](#roles)
- [Copyright](#copyright)


<br></br>
## Softwares used <a name="softwares"></a>

- Windows
- Trello
- Intellij IDEA
- Oracle OpenJDK version 17.0.2
- Apache Maven
- JavaFX
- Java Networking (including port-forwarding)
- Git


<br></br>
## 1st iteration of the game (March 4): [Demo](https://www.youtube.com/watch?v=cYr4cSpDKhM) <a name="first"></a>
- 2 to 4 players can join the game and take turns discarding cards and taking new ones
- The UI also allows each player to:
	- See their current cards and select some for discarding
	- Get new cards into their hand
	- See the cards discarded by other players

![image](https://user-images.githubusercontent.com/44578113/166127520-a06c0fd3-75ed-41f3-8f18-bce12fdffe2a.png)


<br></br>
## 2nd iteration of the game (March 13): [Demo](https://www.youtube.com/watch?v=NdfmuBfkgcY) <a name="second"></a>
- The game consists only of quests, with NO tests, no amours and no allies
- Mordred is merely a normal foe

![image](https://user-images.githubusercontent.com/44578113/166127638-616ab155-a865-4104-ab05-9029bc308555.png)


<br></br>
## 3rd iteration of the game (March 20): [Demo](https://www.youtube.com/watch?v=B8WLqqNAn3M) <a name="third"></a>
- The game is augmented with all events

![image](https://user-images.githubusercontent.com/44578113/166127666-082ef67d-b096-43a4-9483-a7b4a59440ac.png)


<br></br>
## 4th iteration of the game (March 31): [Demo](https://www.youtube.com/watch?v=jxndgWbg8mg) <a name="fourth"></a>
- The game is augmented with tournaments and tests in quests

![image](https://user-images.githubusercontent.com/44578113/166127714-7af75fca-8c17-4870-bd24-c8152d96d168.png)


<br></br>
## Final iteration of the game (April 8): [Demo](https://www.youtube.com/watch?v=jWEatQwQw0E) <a name="final"></a>
- The game is augmented with amours and allies
- Mordred is able to kill an ally

![image](https://user-images.githubusercontent.com/44578113/166127746-38f472b6-d395-4ec0-8e5f-c9e08a2b43a7.png)


<br></br>
## Tweaks and Features <a name="tweaks"></a>
- The game is multiplayer and able to be played across different machines with port-forwarding
- The game features a game log to easily track game activity
- The entirety of the game logic has been programmed in with some limitations for simplicity
	- A winner is crowned when a player reaches the Knight rank


<br></br>
## How to Run <a name="run"></a>
- Download and build the project in IntelliJ, then run at least 2 instances of App.java (for hosting and joining)
- Alternatively, you can open multiple terminals in the project folder and run “mvn clean javafx:run” in each to start multiple instances of the game without using an IDE


<br></br>
## Team Roles <a name="roles"></a>
- Walker Mercer - Networking 
- Philip Wanczycki - UI 
- Henry Zhangxiao - Game Logic
- Alex Muir - Game Logic


<br></br>
### Copyright <a name="copyright"></a>
"Quests of the Round Table" by GAMEWRIGHT INC.

This project was created privately and internally for academic and educational purposes only with no intent to distribute.

All game rules, cards, media, and game components belong to GAMEWRIGHT INC. and their rightful owners.






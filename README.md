## Bubble Shooter

### Introduction

I have made the Bubble Shooter game as my third game. It uses a client-server architecture and is a single player game. I will give instructions on how to run the program and in the following sections explain certain parts of the game. 

### Running my program

There are two ways to run my Space Invaders game with scripting enabled:

1. **JAR**:
    1. Find the JAR file for this project in
    `[root_dir]\out\artifacts\bubble_shooter_jar\bubble-shooter.jar`.
    2. Open a command line and type `java -jar bubble-shooter.jar s` (for server).
    3. For running clients, open other command lines, type and execute `java -jar bubble-shooter.jar c` ONCE. This game is implemented using a client-server architecture but is a single-player game. So there can be only one client. Running more clients may have undefined results.
    4. Remember that you need the run the server first and then the client, otherwise this might throw some exception. This should be normal, as for most multiplayer games, the headless server generally is always running. 
    5. Also currently, the client searches for a running server in `localhost`, so running the server and client in different computers will not work. If you still want to run it in different computers, follow my second way of running the program and before building it, open `Constants.java` and assign the server's IP to the `SERVER_ADDRESS` String variable.
      
2. **IntelliJ**:
    1. Install [IntelliJ Community Edition](https://www.jetbrains.com/idea/download/#section=windows).
    2. Import and build my project.
    3. There should be two run configurations - one for the server and one for the client. Run the "Server" first and then the "Client". The shortcut for running programs in IntelliJ is `Alt + Shift + F10`.
    4. If you don't find the run configurations, make two yourself. For the sever, give a command line argument of `s` and for the client, give a command line argument of `c` (without the quotes).
    
### Controls

The controls are displayed on screen, but still I will give a description of the same here:

1. Move the mouse to control the turret direction.
2. Press `SPACE` to shoot bubbles. 

### The game

This follows the classic game rules. I will list them and any variations here:

1. There are 9 rows of bubbles initially. 
2. Additional rows get added from the bubbles that you shoot.
3. The bubbles keep coming down at a constant pace.
3. There are three colors of bubbles: red, green and blue. 
4. The objective is to shoot and match all bubbles before it reaches the red line. 
5. If the player loses, the game restarts with a new set of bubbles.
6. Three bubbles to be fired are shown beside the player controller turret. 
6. A score is maintained which is based on the number of bubbles you match. 
7. Two bubbles of the same color are matched. The matching is done with neighbors on the left, right and two on the top. 
8. The game is continuous, i.e. if you die, then it restarts and you keep building your score. 

### Client-server architecture

The server here creates the bubble map and sends it over to the client, i.e. it establishes the environment. From there on, the client handles the player movements and killing of enemies. 

### Scripting 

Scripting is enabled in my game and the script file lies in the same directory as my previous assignment section. This is the same as the last game's script. In the script file I can change the color of my player to something else. I expose the player object to the script and then I call the `changeColor` method in that to change the color. 

### Difference and code reuse

This is exactly the same as my second game report and I have made no extra changes to my engine between my second and third game. So, I will just give the new set of difference images below. 
 
 Here is a report for the `src` folder comparison. 
 
 ![Imgur](http://i.imgur.com/eetK5VN.png)
  
1. **`EventManager`**: 
   ![Imgur](http://i.imgur.com/s9isqDm.png)
2. **`EngineConstants`**:  
   ![Imgur](http://i.imgur.com/lgd7pFH.png)
3. **`MainEngine`**: 
   ![Imgur](http://i.imgur.com/kxEFNB7.png) 
   ![Imgur](http://i.imgur.com/yE2up3U.png)
   ![Imgur](http://i.imgur.com/NpOLlb6.png)
    
Full reports are available in the `reports` directory. The reports were taken using a trial version of Araxis Merge.
 
### Thoughts

All in all, it was very interesting to use my game engine and make another new game. Like before, it was a very smooth process.
 
### Screencast

I have uploaded a screencast to YouTube so that it's easier for you to check what I've done. I play the game one time and when the bubbles reach the bottom, you can see that the game restarts with the death count increased by one. I also show the use of scripts in the end.

https://www.youtube.com/watch?v=o7P0cNh5ofI&feature=youtu.be
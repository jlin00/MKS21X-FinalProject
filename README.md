# Bloons Tower Defense
This project aims to recreate a game known as Bloons Tower Defense. The program will be interactive and act similarly to the original game. The user will be able to choose a map to play the game in. Each map will have a road for the balloons to pass through and surrounding grass to place the towers. Balloons will enter from one end of the road and continue traveling until they either reach the end of the road or are popped by surrounding towers. Each balloon that reaches the end of the road will decrease the lives of the user. When the user has no more lives and has passed all the levels, the game ends. The user will steadily earn income as time passes and can use it to purchase towers that pop the balloons.

## Instructions
(Make your terminal full screen for the best experience! Otherwise, make sure it is at least 110x50.)
<b>Do not resize the terminal during the game!</b>

After cloning the repo, run the command "bash run.sh" on the terminal. 
If this command does not work, run the following sequence: 
'''
resize -s 50 110
javac -cp lanterna.jar:. Balloon.java
javac -cp lanterna.jar:. Tile.java
javac -cp lanterna.jar:. SpikeTower.java
javac -cp lanterna.jar:. Spike.java
javac -cp lanterna.jar:. TackShooter.java
javac -cp lanterna.jar:. Tower.java
javac -cp lanterna.jar:. Tack.java
javac -cp lanterna.jar:. GameScreen.java
'''

After compiling all of these files, run the command "java -cp lanterna.jar:. GameScreen <optional map number>". Where <map number> is, pass in either 1, 2, or 3  in order to use map 1,2, or 3 for the game. Be careful, because passing in a number that is not 1, 2, 3 will not generate a map! If no argument is passed, map 1 is generated by default.

Once GameScreen.java is compiled, a terminal screen will appear, displaying the Bloons Tower Defense Game. To start the game, press 'b' once, which will load the map of the game. Pressing 'a' will pause the game. Money is earned as time goes on— $50 every 10 seconds. When the game is paused, the timer stops as well, so money will not be gained during this period. The currency can be used to buy towers. The game screen will display a chart of each tower type and their respective keys. Select the tower you want to place based on the chart and use the arrow keys to move the cursor along the map to the tile that you want to place your tower on. Then press enter place down the tower. You can place TackShooter and SpikeTower directly on the grass but you can only place Spikes on the road. To upgrade the towers, move the cursor over them and press enter. The upgraded tower stats will be displayed on the screen. The TackShooter can be upgraded once and the SpikeTower can be upgraded twice. Once the level beings, balloons will move down the road tiles and each time a balloon reaches the end of the road, the user's lives will decrease. Users have a limited amount of lives. After they are used up, the user loses the game. Successfully popping all the balloons means the user can advance to the next level. As the user advances, the number of balloons increases and the balloons also get faster and have more lives. If the user successfully passes level 15, they win the game. 

## Development Log
<table>
  <th>Log Date</th>
  <th>Description</th>
  <tr>
    <td>1/3/19</td>
    <td>Today, we created the new repository. We learned about lanterna.jar and will be spending some time trying to
    figure out how to use it. The formatting for the README.md was also finalized. </td>
  </tr>
<tr>
    <td>1/4/19</td>
    <td>Today, we worked on creating the Tile and Balloon classes, and added some methods. We decided to add a few more
    variables to our Balloon class and also to combine the Map and User classes into one big Game class.
    </td>
</tr>
<tr>
    <td>1/5/19</td>
    <td>Today, we experimented with lanterna.jar by creating different types of modes (such as starting mode and game mode).
    We did not code anything definitive because we are still trying to understand how to use lanterna.jar. We anticipate
    this task to be one of the more challenging ones as we move forward.
    </td>
</tr>
<tr>
    <td>1/6/19</td>
    <td>Today, we experimented with some graphics of the game. We read in coordinates from a map file, and tried to color
    in the terminal accordingly. This task is still a work in progress. We will continue working on it tomorrow, and will
    also continue working on the Tile class.
    </td>
</tr>
<tr>
    <td>1/7/19</td>
    <td>Today, we continued to experiment with the terminal graphics of the game. We started to format the graphics and achieved having two modes, one screen for starting up the game and second screen for the map of the game. The basic Tile class was finished with some additions, like the Balloon List, and we implemented it to the Game terminal. We also finished the Tower abstract class and started working on the three different towers, TackShooter, RoadSpike, and IceTower, that extend the abstract class.
    </td>
</tr>
<tr>
    <td>1/8/19</td>
    <td>Today, we continued writing methods for the Tile and Balloon classes. Hopefully, we will soon be able to test out their behaviors in the terminal. We are also working on features like drawing the map and setting up a timer system for the game.
    </td>
</tr>
<tr>
    <td>1/9/19</td>
    <td>Today, we continued working on developing the graphics of the game. We attempted to implement the Balloon class in the terminal, with
    limited success. We also merged the two branches together and will modify methods over the next few days as we test them out.  
    </td>
</tr>
<tr>
    <td>1/10/19</td>
    <td>Today, we worked on implementing the balloon class and having it show up on the terminal. We still haven't figured out the timer system, so hopefully we
    fix that problem sometime in the near future. We will also start working on implementing the tower class in order to prepare for demos.   
    </td>
</tr>
<tr>
    <td>1/11/19</td>
    <td>Today, we continued to on implementing the balloon objects onto the terminal class. We had many challenges with this, such as the balloon getting stuck after some time and some flickering issues, but we at least managed to have one balloon object moving along the road. To address these issues, we went to our teacher to see if he could help us fix anything, and we received advice on how to improve the game and solve the problems. Besides the terminal graphics, we also revised the attack methods in each of the different towers.
    </td>
</tr>
<tr>
    <td>1/12/19</td>
    <td>Today, we tried implementing the changes that our teacher had advised us to implement. Though the graphics of the terminal improved significantly due to these changes, we continued to have problems with the flickering balloons and the balloon getting stuck after a while. We had also changed the workings of the Balloon and Tile class so now they have to be compiled with the lanterna.jar in order for the game to work. Meanwhile, we also updated instructions for the game.
    </td>
<tr>
    <td>1/13/19</td>
    <td>Today, we continued to work on the terminal graphics. We managed to have one balloon moving without getting stuck after making major changes to the Game, Balloon and Tile classes. While some balloon problems were fixed, we had trouble with the tower graphics on the screen. In particular, the cursor caused problems with the coloring of the terminal, which we were unable to solve. As we worked on that, we also created some new maps for the game and made the game able to switch between maps easily. We updated the instructions screen as well as the UML diagram. We made the tackShooter tower more advanced by creating a new object, Tack to be owned by the tackShooter. Tack will move and attack balloons, but we have yet to test that out.
    </td>
</tr>
<tr>
    <td>1/14/19</td>
    <td>Today, we continued working on the terminal graphics. The balloon movement is fully functional now after moving the implementation onto a Screen. We also worked started implementing Towers into the GameScreen class using arrow keys. Today, we also demonstrated our game in class. We still need to work on Tower attack methods.
    </td>
</tr>
  <tr>
    <td>1/15/19</td>
    <td>Today, we began implementing the TackShooter and Tack classes into the terminal. We added methods such as spawnTacks() for TackShooter and move(), draw() and hitTarget() for Tacks. We added code for this into the terminal and were able to get TackShooters to start shooting Tacks according to specifications such as radius, speed and delay. We also started to write a levelUp function. We will continue working on modifying hitTarget(), fix some draw() issues for Tack and fix the levelUp() function.
    </td>
</tr>
 <tr>
    <td>1/16/19</td>
    <td>Today, we fixed a cursor bug and also fixed the levelUp() function. We started modifying the Tack class by writing an inRadius() function, which we will continue testing. We also will start making all the main class variables into private fields of GameScreen.
    </td>
</tr>
<tr>
  <td>1/17/19</td>
  <td>Today, we finished most of the Tack class, and made the tack shooter shoot out tacks only when a balloon was in radius. We also tried fixing timer issues so that the tacks wouldn't miss the balloons. We also implemented the Spike class in the terminal and started the implementation of the SpikeTower class.
  </td>
</tr>
<tr>
  <td>1/18/19</td>
  <td>Today we finished implementing the SpikeTower class in the terminal and wrote methods to randomly generate spikes onto a road tile. We also updated graphics for balloons (they are now color coded based on the amount of lives they have). We manually set up fifteen levels, which increase in difficulty. We are considering adding more levels and will be adding more instructions to the starting screen in the near future. 
  </td>
</tr>
<tr>
  <td>1/19/19</td>
  <td>Today we revised and added some comments to all of the files. We also fixed a method in the GameScreen class to improve the SpikeTower class. We also added code so that towers (TackShooters and SpikeTowers) could be upgraded. We also updated the instructions for the game and added a option to delete towers. 
  </td>
</table>

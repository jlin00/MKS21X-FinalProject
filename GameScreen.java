//API : http://mabe02.github.io/lanterna/apidocs/2.1/
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.CharacterPattern;
import com.googlecode.lanterna.input.InputDecoder;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;
import com.googlecode.lanterna.screen.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.*;

public class GameScreen{

  /**A method that draws the border of the map
  *@param int r
  *@param int c
  *@param int length
  *@param Screen s
  */

  public static void drawBorder(int r, int c, int length, Screen s){ //draws a border for the game, terminal must be at least 104 x 45
    for (int i = 0; i < length; i++){
      s.putString(r,c+i," ",Terminal.Color.DEFAULT,Terminal.Color.BLACK);
    }

    for (int i = 0; i < 2*length; i++){
      s.putString(r+i,c," ",Terminal.Color.DEFAULT,Terminal.Color.BLACK);
    }

    for (int i = 0; i < 2*length; i++){
      s.putString(r+i,c+length," ",Terminal.Color.DEFAULT,Terminal.Color.BLACK);
    }

    for (int i = 0; i < length; i++){
      s.putString((r+length-1)*2,c+i," ",Terminal.Color.DEFAULT,Terminal.Color.BLACK);
    }
  }

  /**A method to check that a coordinate is within range of the game map
  *this method ensures that the cursor will always stay within the map
  *@param int xcor
  *@param int ycor
  *@return boolean
  */
  public static boolean isWalkable(int xcor, int ycor){
    if ((xcor == 1 || xcor == 60) && (ycor >= 3 && ycor <= 33)) return false;
    if ((ycor == 3 || ycor == 33) && (xcor >= 1 && xcor <= 60)) return false;
    return true;
  }

  /**A method that checks if a tower can be placed at a given coordinate on the map
  *@param int xcor
  *@param int ycor
  *@param List<Tile> road
  *@param List<TackShooter> TackShooters
  *@param List<SpikeTower> SpikeTowers
  *@return boolean
  */
  public static boolean isPlaceable(int xcor, int ycor, List<Tile> road, List<TackShooter> TackShooters, List<SpikeTower> SpikeTowers){
    for (Tile x: road){ //towers cannot be placed on a road tile
      if (x.getX() == xcor && x.getY() == ycor) return false;
    }

    for (TackShooter x: TackShooters){ //towers cannot be placed on top of each other
      if (x.getX() == xcor && x.getY() == ycor) return false;
    }

    for (SpikeTower x: SpikeTowers){ //towers cannot be placed on top of each other
      if (x.getX() == xcor && x.getY() == ycor) return false;
    }
    return true;
  }

  /**A method that checks if a spike object can be placed at a given coordinate on the map
  *@param int xcor
  *@param int ycor
  *@param List<Tile> road
  *@param List<Spike> spikes
  *@return boolean
  */
  public static boolean spikeIsPlaceable(int xcor, int ycor, List<Tile> road, List<Spike> spikes){
    for (Spike x: spikes){ //Spikes cannot be placed on each other
      if (x.getX() == xcor && x.getY() == ycor) return false;
    }

    for (Tile x: road){ //Spikes can only be placed on road tiles
      if (x.getX() == xcor && x.getY() == ycor) return true;
    }

    return false;
  }

  /**A method that checks if there is a tower at the cursor
  *@param int xcor of the cursor
  *@param int ycor of the cursor
  *@param List<TackShooter> TackShooters
  *@param List<SpikeTower> SpikeTowers
  *@return boolean
  */
  public static boolean isUpgradeable(int xcor, int ycor, List<TackShooter> TackShooters, List<SpikeTower> SpikeTowers){
    for (TackShooter x: TackShooters){ //if a TackShooter is at the cursor
      if (x.getX() == xcor && x.getY() == ycor) return true;
    }

    for (SpikeTower x: SpikeTowers){ //if a SpikeTower is at the cursor
      if (x.getX() == xcor && x.getY() == ycor) return true;
    }

    return false;
  }

  public static void main(String[] args) throws FileNotFoundException {
    Terminal terminal = TerminalFacade.createTextTerminal();
    terminal.enterPrivateMode();

    TerminalSize size = terminal.getTerminalSize();
    terminal.setCursorVisible(false);

    int cursorX = 30; //initial cursor coordinates
    int cursorY = 18;

    boolean running = true;
    int mode = 1; //start off in pause mode
    int map = 1; //default map is Map 1
    if (args.length != 0) map = Integer.parseInt(args[0]); //choose between maps
    long lastTime =  System.currentTimeMillis();
    long currentTime = lastTime; //timer syster
    long timer = 0;
    int toggle = 0; //one time check to see if user has started game
    int sinceTime = 0; //keep track of income

    List<Tile> road = new ArrayList<Tile>(); //stores the coordinates from map files for the road
    List<Tile> border = new ArrayList<Tile>(); //stores the coordinates of the border tiles around the map

    List<TackShooter> TackShooters = new ArrayList<TackShooter>(); //stores TackShooters that have been placed on the map
    int TackShooterPrice = 400; //price for TackShooters
    int TackShooterDelay = 800; //delay between each TackShooter shot
    int TackShooterRad = 4; //radius of the TackShooters
    int TackShooterSinceTime = 0; //the time since the TackShooters last shot tacks

    List<Tack> tacks = new ArrayList<Tack>(); //stores tacks owned by the TackShooters that have been placed
    int tackSinceTime = 0; //time since the tacks last moved
    int tackDelay = 50; //delay time of tacks between each movement

    List<Spike> spikes = new ArrayList<Spike>(); //stores spikes that have been placed on the map by the user or by SpikeTowers
    int SpikePrice = 50; //price for spikes
    int SpikeLives = 5; //each spike can be used 5 times

    List<SpikeTower> SpikeTowers = new ArrayList<SpikeTower>(); //stores SpikeTowers that have been placed on the map
    int SpikeTowerPrice = 300; //price for SpikeTowers
    int SpikeTowerDelay = 7000; //delay time for SpikeTowers to place another spike
    int SpikeTowerRad = 3; //the radius of the SpikeTowers; spikes can only be placed on road tiles within the radius
    int SpikeTowerSinceTime = 0; //the time since the SpikeTowers last placed spikes
    int SpikeTowerLives = 3; //the spikes placed by the SpikeTowers only have three lives
    int UpgradePrice = 400;

    int lives = 25; //user variables
    int money = 500;
    int income = 50;

    List<Balloon> balloons = new ArrayList<Balloon>(); //stores the
    int balloonSinceTime = 0; //used to spawn a balloon every second
    int balloonMoveTime = 0; //used to move all balloons every two seconds
    int level = 1; //variables to be adjusted according to level
    int num_balloons = 15; //number of balloons to be initialized; this is default
    int balloons_made = 0; //number of balloons already initialized
    int balloon_lives = 1; //number of lives each balloon will have; this is default
    int balloon_delay = 600; //milliseconds between each balloon movement
    int balloon_spawnTime = 1200; //time for the balloons to spawn after the previous balloon
    boolean level_started = false;
    boolean all_spawned = false;

    //checks which tower is being placed down
    String TowerToggled = "None";
    boolean tack_toggled = false;
    boolean spike_toggled = false;
    boolean factory_toggled = false;

    Screen s = new Screen(terminal);
    s.startScreen();
    s.setCursorPosition(null);
    s.clear();

    //instructions to play game
    s.putString(23,6,"Welcome to",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(23,8,"╔╗ ┬  ┌─┐┌─┐┌┐┌┌─┐  ╔╦╗┌─┐┬ ┬┌─┐┬─┐  ╔╦╗┌─┐┌─┐┌─┐┌┐┌┌─┐┌─┐",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(23,9,"╠╩╗│  │ ││ ││││└─┐   ║ │ ││││├┤ ├┬┘   ║║├┤ ├┤ ├┤ │││└─┐├┤┐",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(23,10,"╚═╝┴─┘└─┘└─┘┘└┘└─┘   ╩ └─┘└┴┘└─┘┴└─  ═╩╝└─┘└  └─┘┘└┘└─┘└─┘",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(23,11,"                        made by Jackie Lin and Tiffany Cao",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(26,14,"              +----------------------+",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(26,15,"              |                      |",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(26,16,"              |                      |",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(44,16,"Press b to begin.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Blinking);
    s.putString(26,17,"              |                      |",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(26,18,"              +----------------------+",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,21,"Once you  begin,  balloons  will  start  spawning and traveling",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,22,"down the road. To defeat them, you  need  to  select a tower by",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,23,"typing  the  key  of   the  tower  you want  to  buy and  using",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,24,"the  arrow  keys  to  give  it  a  location on  the  grass. You",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,25,"also  choose  to  put  down  Road Spikes on the  road.    Press",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,26,"enter  to place the item  down. Pressing backspace will  delete",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,27,"that  tower  (no  refunds)! You  can  upgrade towers by  moving",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,28,"the  cursor  over  it and p ressing  enter. Upgrades cost $400.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,29,"Tack Shooters  can  be  upgraded  once and  Spike Towers twice.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,30,"If you pop all the balloons  in  a  level, you  will advance to",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,31,"the next level. Each level will have  more balloons, which will",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,32,"be faster and have more lives. If you beat  level  15, you win!",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,33,"If you  lose all  your  lives  before then, you lose.  You will",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,34,"receive $50 every 10 seconds.You earn money by popping balloons",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,35,"as well. Spend you money wisely, because  the price  of  towers",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,36,"increases by $100 every time you buy one.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(21,38,"                         Best of Luck!",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
    s.refresh();

    /*

    s.putString(0,8,"Press the enter key to place the tower or spikes down.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,9,"Pressing backspace will delete that tower (no refunds)! You can upgrade towers by placing",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,10,"the cursor over the tower and pressing enter. The upgraded stats of the tower will be show under",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,11,"Tower Selected on the screen. TackShooters can be upgraded once and SpikeTowers can be upgraded twice.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,12,"If you manage to kill all the balloons in a level, you will advance to the next level. As you progress",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,13,"each level will have more balloons, which have more lives and are therefore harder to pop. ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,14,"If you manage to beat level 15, you will have won the game.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,15,"You will receive $50 every 10 seconds. You will receive $5 for every balloon that you pop. Balloons will",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,16,"multiple lives will be worth more. Use your money wisely to purchase towers and upgrade them!",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,17,"Upgrades cost $400, but the price of towers increases by $100 every time you buy one of them.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,18,"If any balloons reach the end of the road, your lives will decrease. If your lives reach 0, you lose.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,19,"[For a more in-depth explanation, read the README.md file on GitHub.]",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
    s.putString(0,22,"Best of luck!",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Blinking);
    */
    s.refresh();

    while (running){

    //drawing onto the screen all the towers and roads
      for (Tile x: road){
        x.draw(s);
      }

      for (TackShooter x: TackShooters){
        x.draw(s);
        if (x.getX() == cursorX && x.getY() == cursorY){ //if there is a TackShooter at the cursor
          if (money >= UpgradePrice && x.canUpgrade()){ //if the tower can be upgraded, the upgrade info is shown
            s.putString(65,30,"Upgrade for $"+UpgradePrice+"?",Terminal.Color.GREEN,Terminal.Color.DEFAULT);
            s.putString(78,28," - 300 ms ",Terminal.Color.GREEN,Terminal.Color.DEFAULT);
            s.putString(85,29," + 1",Terminal.Color.GREEN,Terminal.Color.DEFAULT);
          }
          s.putString(78,27,""+x.getUpgrade(),Terminal.Color.DEFAULT,Terminal.Color.DEFAULT); //shows the status of the tower in terms of its upgrade model
          s.putString(77,28,""+x.getHits(),Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
          s.putString(78,29,""+x.getDelay()+" ms ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        }
      }

      for (Tack x: tacks){
        x.draw(s);
      }

      for (Spike x: spikes){
        x.draw(s);
      }

      for (SpikeTower x: SpikeTowers){
        x.draw(s);
        if (x.getX() == cursorX && x.getY() == cursorY){ //if there is a SpikeTower at the cursor
          if (money >= UpgradePrice && x.canUpgrade()){ //if the tower can be upgraded, the upgrade info is shown
            s.putString(65,30,"Upgrade for $"+UpgradePrice+"?",Terminal.Color.GREEN,Terminal.Color.DEFAULT);
            s.putString(85,29," - 1000 ms",Terminal.Color.GREEN,Terminal.Color.DEFAULT);
            s.putString(78,28," + 1",Terminal.Color.GREEN,Terminal.Color.DEFAULT);
          }
          s.putString(78,27,""+x.getUpgrade(),Terminal.Color.DEFAULT,Terminal.Color.DEFAULT); //shows the status of the tower in terms of its upgrade model
          s.putString(77,28,""+x.getLives(),Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
          s.putString(78,29,""+x.getDelay()+" ms ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        }
        else{
        }
      }

      if (!isUpgradeable(cursorX,cursorY,TackShooters,SpikeTowers) && toggle > 0){ //game screen does not show upgrade info when the cursor is not at a tower
        s.putString(65,30,"                      ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,27,"              ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,28,"                               ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,29,"                              ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
      }

      //changes the delay time of the balloon movements according to the level; the higher the level, the faster the balloons
      if (level <= 15){
        balloon_delay = 100;
        balloon_spawnTime = 800;
      }
      if (level <= 11){
        balloon_delay = 150;
        balloon_spawnTime = 900;
      }
      if (level <= 7){
        balloon_delay = 200;
        balloon_spawnTime = 1000;
      }
      if (level <= 3){
        balloon_delay = 300;
        balloon_spawnTime = 1200;
      }
      if (level == 16 || lives == 0){ //after 15 levels, the game is won or if lives reach 0, game is over
        mode = 2;
      }

      if (toggle > 0){
        s.putString(cursorX,cursorY,"+",Terminal.Color.WHITE,Terminal.Color.BLACK);
        //shows the time and money on the screen
        s.putString(65,5,"Time: "+(timer /1000)+"            ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,7,"Money: "+money+"            ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        //shows info of the tower
        s.putString(65,16,"Tower Info",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Underline);
        s.putString(65,17,"Type of Tower:  "+TowerToggled,Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,18,"Price: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,19,"Radius: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,20,"Hits: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,21,"Placement: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,22,"Upgrade Price: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,26,"Tower Selected",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Underline);
        s.putString(65,27,"Tower Level: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,28,"Tower Hits: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,29,"Tower Delay: ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(2,0,"[Press a to pause. Press b to resume. Press ESC to exit.]",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
      }

      balloonMoveTime += (currentTime - lastTime); //adds the amount of time since the last frame
      for(int i = balloons.size()-1; i >= 0; i--){
        Balloon x = balloons.get(i);
        x.draw(s);
          if (balloonMoveTime >= x.getSince() && x.getIsAlive()){ //the balloon moves once the time needed is reached
            if (x.getTile() < road.size()){
              x.move(road.get(x.getTile()), balloonMoveTime);

              if (x.getTile() == road.size()-1){ //when balloon reaches end of road
                x.makeDead();
                lives-=x.getLives();
                balloons.remove(i);
              }
            }
          }
      }

      s.refresh();

      //shows information of the tower when its respective key is selected
      if (tack_toggled){
        s.putString(74,18,""+TackShooterPrice+"   ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(74,19,"  "+TackShooterRad,Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(76,20,"1",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(76,21,"Grass Tiles (Green)",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(81,22,""+UpgradePrice,Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,23,"Shoots out tacks in four directions  ",Terminal.Color.RED,Terminal.Color.DEFAULT);
      }

      if (spike_toggled){
        s.putString(74,18," "+SpikePrice+"      ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(74,19,"N/A",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(76,20,"5",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(76,21,"Road Tiles (Gray)  ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(81,22,"N/A",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,23,"Pops five balloons                   ",Terminal.Color.RED,Terminal.Color.DEFAULT);
      }

      if (factory_toggled){
        s.putString(74,18,""+SpikeTowerPrice+"  ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(74,19,"  "+SpikeTowerRad,Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(76,20,"3",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(76,21,"Grass Tiles (Green)",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(81,22,""+UpgradePrice,Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,23,"Spawns spikes that pop three balloons",Terminal.Color.RED,Terminal.Color.DEFAULT);
      }

      if (mode == 0){ //if the game has started
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timer += (currentTime - lastTime); //adds the amount of time since the last frame
        s.putString(65,6,"Lives Left: "+lives+"            ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,3,"Level: "+level+"            ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
        s.putString(65,10, "Tower Key", Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Underline);
        s.putString(65,11,"TackShooter: key t",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,12,"SpikeTower:  key s",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.putString(65,13,"RoadSpike:   key *",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.refresh();

        sinceTime += (currentTime - lastTime); //adds the amount of time since the last frame
        if (sinceTime >= 10000 && timer != 0){ //if the time since the last income gain is greater than 10 seconds, income is gained
          money += income;
          sinceTime = 0; //the income timer is set to 0 again for the next time
        }

        balloonSinceTime += (currentTime - lastTime); //adds the amount of time since the last frame
        if (balloonSinceTime >= balloon_spawnTime && balloons_made < num_balloons){ //if the time passed is greater than the balloon_spwanTime
                                                                                    //and all the balloons haven't spawned yet
          level_started = true; //start the level
          balloons.add(new Balloon(balloon_lives, balloon_delay, road.get(0).getX(), road.get(0).getY())); //create and spawn in balloon at the first road tile
          balloons_made++;
          if (balloons_made == num_balloons) all_spawned = true; //checks if all balloons have been spawned
          balloonSinceTime = 0; //balloon timer is set to 0 again for the next round of spawning
        }

        if (balloons.size() == 0 && level_started && all_spawned){ //if all the balloons have been spawned and defeated
          level++; //increase the level
          num_balloons+=10; //increases the number of balloons per level
          balloons_made=0;
          if (level % 2 == 0) balloon_lives++; //balloon lives increaseevery two levels
          for (Tack x: tacks) x.setSteps(4); //reset tacks
          mode = 1;//the game is paused to tell the user a new level is to be started
          level_started = false;
          all_spawned = false;
          if (level < 16) s.putString(10,2,"Now commencing level "+level+". Press b to begin.",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Blinking);
        }

        if (level_started) s.putString(10,2,  "                                           ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
        s.refresh();

        TackShooterSinceTime  += (currentTime - lastTime); //adds the amount of time since the last frame
        for (TackShooter x: TackShooters){
          if (TackShooterSinceTime >= x.getSince() && x.inRadius(balloons)){ //checks the time since the last tacks were created and that balloons are in radius
            x.spawnTacks(tacks, TackShooterSinceTime, tackDelay); //create new round of tacks
          }
        }

        tackSinceTime += (currentTime - lastTime); //adds the amount of time since the last frame
        for (int i = tacks.size()-1; i>=0; i--){
          Tack x = tacks.get(i);
          if (tackSinceTime >= x.getSince()){ //creates delay between tacks shots
            x.undraw(s, x.getX(), x.getY(), road); //undraw tacks if it's on a road tile
            x.move(tackSinceTime); //tacks move
            money += x.hitTarget(balloons); //check if tacks have hit a balloon and takes a life from the balloon hit; money is also gained for each life taken
            if (x.getSteps() >= TackShooterRad){ //if the tacks have reached the radius of their movements
              tacks.remove(i); //they "die" and are removed from the list of tacks
            }
          }
        }

        SpikeTowerSinceTime += (currentTime - lastTime); //adds the amount of time since the last frame
        for (SpikeTower x: SpikeTowers){
          if (SpikeTowerSinceTime >= x.getSince()){ //checks that a spike can be placed again by checking the time a spike was last placed
            x.spawnSpikes(spikes,road,SpikeTowerSinceTime,SpikeTowerDelay,SpikePrice); //spawns a spike
          }
        }

        for (int i = spikes.size()-1; i>=0; i--){
          Spike x = spikes.get(i);
          if (x.getLives() == 0) spikes.remove(i); //the spikes are removed from the list after 5 uses
          money += x.hitTarget(balloons); //checks if spikes hit anything and takes a life from the balloon they hit; money is also gained for each life taken
        }

        s.refresh();
      }

      if (mode == 1){ //pause timer when the game is paused
        lastTime = System.currentTimeMillis(); //stops the time
        currentTime = System.currentTimeMillis();
        if (toggle >= 1) s.putString(65,5,"Time: " + (timer / 1000),Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
      }

      if (mode == 2){ //win or lose game mode
        if (level == 16) s.putString(10,1,"\t  You won! Press ESC to exit.\t",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Blinking);
        if (lives == 0) s.putString(10,1,"\t You lost! Press ESC to exit.\t",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Blinking);
      }

      Key key = s.readInput();
      if (key != null){ //what to start doing when a key is pressed

        toggle++;
        if (toggle == 1){ //one-time execution
          s.clear();

          File f = new File("map0.txt");
          if (map == 1) f = new File("map1.txt"); //different choices of maps
          if (map == 2) f = new File("map2.txt");
          if (map == 3) f = new File("map3.txt");
          Scanner in = new Scanner(f);
          while (in.hasNext()){ //read in coordinates of map file to draw the road
            String line = in.nextLine();
            String[] arr = line.split(" ");
            int xcor = Integer.parseInt(arr[0]);
            int ycor = Integer.parseInt(arr[1]);
            road.add(new Tile(xcor, ycor));
          }
        }

        if (toggle >= 1 && mode == 1){
          drawBorder(1,3,30,s);

          for (int x = 2; x < 60; x++){ //color in background
            for (int y = 4; y < 33; y++){
              s.putString(x,y," ",Terminal.Color.DEFAULT,Terminal.Color.GREEN);
            }
          }

          //shows on the map information about balloon lives and colors
          s.putString(65,32,"Balloon Key",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT,ScreenCharacterStyle.Underline);
          s.putString(65,33,"Red = 1 life",Terminal.Color.RED,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.putString(65,34,"Yellow = 2 lives ",Terminal.Color.YELLOW,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.putString(65,35,"Green = 3 lives",Terminal.Color.GREEN,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.putString(65,36,"Cyan = 4 lives",Terminal.Color.CYAN,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.putString(65,37,"Blue = 5 lives ",Terminal.Color.BLUE,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.putString(65,38,"Magenta = 6 lives",Terminal.Color.MAGENTA,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.putString(65,39,"Black = 7 lives ",Terminal.Color.BLACK,Terminal.Color.DEFAULT,ScreenCharacterStyle.Bold);
          s.refresh();
        }


        if (key.getKind() == Key.Kind.Escape){ //exit game
          s.stopScreen();
          running = false;
        }

        if (mode == 1 && key.getCharacter() == 'b'){ //enter game mode
          mode=0;
        }

        if (mode == 0 && key.getCharacter() == 'a'){ //pause game
          mode=1;
        }

        //code for cursor movement
        if (toggle > 0 && key.getKind() == Key.Kind.ArrowUp){
          if (isWalkable(cursorX, cursorY-1)){
            cursorY--;
              if (isPlaceable(cursorX,cursorY+1,road,TackShooters,SpikeTowers)) s.putString(cursorX,cursorY+1," ",Terminal.Color.DEFAULT,Terminal.Color.GREEN);
          }
        }

        if (toggle > 0 && key.getKind() == Key.Kind.ArrowDown){
          if (isWalkable(cursorX, cursorY+1)){
            cursorY++;
            if (isPlaceable(cursorX,cursorY-1,road,TackShooters,SpikeTowers)) s.putString(cursorX,cursorY-1," ",Terminal.Color.DEFAULT,Terminal.Color.GREEN);
          }
        }

        if (toggle > 0 && key.getKind() == Key.Kind.ArrowLeft){
          if (isWalkable(cursorX-1, cursorY)){
            cursorX--;
            if (isPlaceable(cursorX+1,cursorY,road,TackShooters,SpikeTowers)) s.putString(cursorX+1,cursorY," ",Terminal.Color.DEFAULT,Terminal.Color.GREEN);
          }
        }

        if (toggle > 0 && key.getKind() == Key.Kind.ArrowRight){
          if (isWalkable(cursorX+1, cursorY)){
            cursorX++;
            if (isPlaceable(cursorX-1,cursorY,road,TackShooters,SpikeTowers)) s.putString(cursorX-1,cursorY," ",Terminal.Color.DEFAULT,Terminal.Color.GREEN);
          }
        }

        //checks which tower is being selected by the user
        if (toggle > 0 && key.getCharacter() == 't'){
          tack_toggled = true;
          spike_toggled = false;
          factory_toggled = false;
          TowerToggled = "TackShooter";
        }

        if (toggle > 0 && key.getCharacter() == '*'){
          tack_toggled = false;
          spike_toggled = true;
          factory_toggled = false;
          TowerToggled = "RoadSpike  ";
        }

        if (toggle > 0 && key.getCharacter() == 's'){
          tack_toggled = false;
          spike_toggled = false;
          factory_toggled = true;
          TowerToggled = "SpikeTower ";
        }

        //if the user wants to remove a tower
        //checks if a tower does exist at the cursor and removes the tower from the game
        if (toggle > 0 && key.getKind() == Key.Kind.Backspace){
          for (int i = TackShooters.size()-1; i >= 0; i--){
            TackShooter temp = TackShooters.get(i);
            if (temp.getX() == cursorX && temp.getY() == cursorY) TackShooters.remove(i);
          }

          for (int i = SpikeTowers.size()-1; i >= 0; i--){
            SpikeTower temp = SpikeTowers.get(i);
            if (temp.getX() == cursorX && temp.getY() == cursorY) SpikeTowers.remove(i);
          }

          for (int i = spikes.size()-1; i >= 0; i--){
            Spike temp = spikes.get(i);
            if (temp.getX() == cursorX && temp.getY() == cursorY) spikes.remove(i);
          }
        }

        //if the user wants to place a tower
        if (toggle >= 1 && key.getKind() == Key.Kind.Enter){
          if (tack_toggled){ //if the tower chosen is a TackShooter
            if (isPlaceable(cursorX,cursorY,road,TackShooters,SpikeTowers) && (money - TackShooterPrice >= 0)){ //if the coordinate is placeable and the user has enough money...
              TackShooters.add(new TackShooter(cursorX,cursorY,TackShooterPrice,TackShooterDelay,TackShooterRad,1)); //a new TackShooter is created on the map
              money -= TackShooterPrice; //take away money
              TackShooterPrice += 100; //the cost of the tower increases with each purchase
              if (cursorX == 59) cursorX--;
              else cursorX++;
            }
          }

          if (spike_toggled){ //if the tower chosen is a spike
            if (spikeIsPlaceable(cursorX,cursorY,road,spikes) && (money - SpikePrice >= 0)){ //if the coordinate is placeable and the user has enough money...
              spikes.add(new Spike(cursorX,cursorY,SpikePrice,SpikeLives)); //a new spike is created on the map
              money -= SpikePrice; //take away money
              if (cursorX == 59) cursorX--;
              else cursorX++;
            }
          }

          if (factory_toggled){ //if the tower chosen is a SpikeTower
            if (isPlaceable(cursorX,cursorY,road,TackShooters,SpikeTowers) && (money - SpikeTowerPrice >= 0)){ //if the coordinate is placeable and the user has enough money...
              SpikeTowers.add(new SpikeTower(cursorX,cursorY,SpikeTowerPrice,SpikeTowerDelay,SpikeTowerRad,SpikeTowerLives)); //a new SpikeTower is created on the map
              money -= SpikeTowerPrice; //take away money
              SpikeTowerPrice += 100; //the cost of the tower increases with each purchase
              if (cursorX == 59) cursorX--;
              else cursorX++;
            }
          }

          //f the user wants to upgrade a tower
          if (isUpgradeable(cursorX,cursorY,TackShooters,SpikeTowers) && money >= UpgradePrice){ //if the tower is able to be upgraded
            for (SpikeTower x: SpikeTowers){ //checks which SpikeTower, if any, is being upgraded
              if (x.getX() == cursorX && x.getY() == cursorY && x.canUpgrade()){
                x.upgrade();
                money -= UpgradePrice; //takes away upgrade price
                if (!x.canUpgrade() || money < UpgradePrice){ //game screen does not show upgrade info when the tower cannot be upgraded
                  s.putString(65,30,"                     ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
                  s.putString(79,28,"                 ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
                  s.putString(86,29,"          ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
                }
              }
            }

            for (TackShooter x: TackShooters){ //checks which TackShooter, if any, is being upgraded
              if (x.getX() == cursorX && x.getY() == cursorY && x.canUpgrade()){
                x.upgrade();
                money -= UpgradePrice; //takes away upgrade price
                if (!x.canUpgrade()){ //game screen does not show upgrade info when the tower cannot be upgraded
                  s.putString(65,30,"                     ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
                  s.putString(79,28,"              ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
                  s.putString(86,29,"          ",Terminal.Color.DEFAULT,Terminal.Color.DEFAULT);
                }
              }
            }

          }

        }

      }
    }
  }
}

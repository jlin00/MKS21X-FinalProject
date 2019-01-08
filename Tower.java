import java.util.*;

public abstract class Tower{
  private int x, y;
  private int cost;
  private int radius;
  private List<Tile> vicinity;

  /**A Tower constructor
  *@param int xCord is the x position of the tower, also its row in the array
  *@param int yCord is the y position of the tower, also its column in the array
  *@param int money is the cost
  *@param int rad is the radius
  */
  public Tower(int xCord, int yCord, int money, int rad){
    x = xCord;
    y = yCord;
    cost = money;
    radius = rad;
    findVicinity();
  }

  /**A method to get the x value, or the row of the tower in the array
  *@return int
  */
  public int getX(){
    return x;
  }

  /**A method to get the y value, or the column of the tower in the array
  *@return int
  */
  public int getY(){
    return y;
  }

  /**A method to get the cost of the tower
  *@return int
  */
  public int getCost(){
    return cost;
  }

  /**A method to get the radius of the tower
  *@return int
  */
  public int getRadii(){
    return radius;
  }

  /**A method to be written by the individual towers
  *attacks are different for each type of tower
  */
  abstract void attack();

  /**A method to run through the map array and find the tiles of the map that will be in the radius of the tower
  *this is done by taking the x and y coordinates of each tile (which is essentially their row and column)
  *and using the equation of a circle to determine whether the tile is within the radius of the tower
  *if the tile is within the radius, it will be added to the list of tiles
  */
  public void findVicinity(){
    int xCord, yCord;
    Tile temp;
    for(int i = 0; i < mapArray.length; i++){
      for(int y = 0; y < mapArray[i].length; y++){
        temp = mapArray[i][y];
        xCord = temp.getX();
        yCord = temp.getY();
        if(((Math.abs(this.getX() - xCord)) + (Math.abs(this.getY() - yCord))) <= (this.getRadii() * this.getRadii())){
          vicinity.add(temp);
        }
      }
    }
  }

  /**A method to find a balloon target for the tower
  *the balloon target is the nearest balloon within the radius of the tower
  *@return Balloon
  */
  abstract Balloon findTarget();
}

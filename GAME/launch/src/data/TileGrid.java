//package data;
   package GAME.launch.src.data;

   public class TileGrid {
   
      public Tile[][] map;
   
   
      public TileGrid()
      {
         map = new Tile[37][19];
         for(int i = 0; i<map.length; i++){
            for(int j = 0; j<map[i].length; j++)
            {
               map[i][j] = new Tile(i*25, j*25, 25, 25, TileType.Secondary);
            }
         }
      }
   
      public TileGrid(int[][] newMap)
      {
         map = new Tile[37][19];
         for(int i = 0; i<map.length; i++){
            for(int j = 0; j<map[i].length; j++)
            {
               if(newMap[j][i] == 0){
                  map[i][j] = new Tile(i*25, j*25, 25, 25, TileType.Transparent);
               }
               else
                  map[i][j] = new Tile(i*25, j*25, 25, 25, TileType.Impassable);
            }
         }
      }
   
      public void setTile(int x, int y, TileType t)
      {
         map[x][y] = new Tile(x*25, y*25, 25, 25, t);
      }
   
      public Tile getTile(int x, int y)
      {
         return map[x][y];
      }
   
      public Tile[][] getMap() {
         return map;
      }
   
   
      public void setMap(Tile[][] map) {
         this.map = map;
      }
   
   }

 //package data;
   package GAME.src.maps.data;

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
         map = new Tile[newMap.length][newMap[0].length];
         for(int i = 0; i<map.length; i++){
            for(int j = 0; j<map[i].length; j++)
            {
               if(newMap[i][j] == 0){
                  map[i][j] = new Tile(i*25, j*25, 25, 25, TileType.Transparent);
               }
               else
                  map[i][j] = new Tile(i*25, j*25, 25, 25, TileType.Impassable);
            }
         }
      }
   
   
      public Tile[][] getMap() {
         return map;
      }
   
   
      public void setMap(Tile[][] map) {
         this.map = map;
      }
   
   }

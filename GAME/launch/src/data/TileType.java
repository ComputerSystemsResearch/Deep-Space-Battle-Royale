//package data;
   package GAME.launch.src.data;

   public enum TileType {
      
      Primary("GAME/launch/src/res/primaryTile.png", true), Secondary("GAME/launch/src/res/secondaryTile.png", false),
      Background("GAME/launch/src/res/bg.gif", true), Transparent("GAME/launch/src/res/transparent.png", true),
      Impassable("GAME/launch/src/res/transparent.png", false), Ship("GAME/launch/src/res/spaceship.png", false),
      MapTex1("GAME/launch/src/res/map 1.png", true);
   
      String textureName;
      boolean passable;
   
      TileType(String textureName, boolean passable){
         this.textureName = textureName;
         this.passable = passable;
      }
   }

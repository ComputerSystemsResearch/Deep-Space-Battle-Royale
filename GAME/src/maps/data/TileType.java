//package data;
   package GAME.src.maps.data;

   public enum TileType {
   
      Primary("GAME/src/maps/res/primaryTile.png", true), Secondary("GAME/src/maps/res/secondaryTile.png", false), 
      Background("GAME/src/maps/res/bg.gif", true),Transparent("GAME/src/maps/res/transparent.png", true), 
      Impassable("GAME/src/maps/res/transparent.png", false), Ship("GAME/src/maps/res/spaceship.png", false), 
      MapTex1("GAME/src/maps/res/map 1.png", true);
   
      String textureName;
      boolean passable;
   
      TileType(String textureName, boolean passable){
         this.textureName = textureName;
         this.passable = passable;
      }
   }

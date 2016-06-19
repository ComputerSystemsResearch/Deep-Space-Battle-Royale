   //package data;
   package GAME.launch.src.data;

   import java.awt.Graphics;

   public class World {
   
      private TileGrid grid;
      private Tile bg;
      private Tile mapTex;
      private AudioPlayer gameMusic;
   
      public static final int WIDTH = 900, HEIGHT = 450; //Default
   
      public World(TileGrid g)
      {
         grid = g;
         bg = new Tile(-10, -10, WIDTH+20, HEIGHT+20, TileType.Background);
         mapTex = new Tile(-10, -10, WIDTH+20, HEIGHT+20, TileType.MapTex1);
         gameMusic = new AudioPlayer("GAME/launch/src/res/gameMusic.wav");
         if(MainMenu.musicOn == true)
            gameMusic.play();
      }
   
      public void update(Graphics g)
      {
         drawTile(g, bg);
         drawMap(g, grid);
         drawTile(g, mapTex);
      }
   
      public void drawMap(Graphics g, TileGrid t)
      {
         for(int i = 0; i<t.getMap().length; i++){
            for(int j = 0; j<t.getMap()[i].length; j++){
               Tile a = t.getMap()[i][j];
               g.drawImage(a.getImage(), a.getX(), a.getY(), a.getWidth(), a.getHeight(), null);
            }
         }
      }
      public void drawTile(Graphics g, Tile t)
      {
         g.drawImage(t.getImage(),t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);
      }
   }

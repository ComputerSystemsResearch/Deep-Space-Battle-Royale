   //package data;
   package GAME.launch.src.data;

   import java.awt.Graphics;
   import java.util.Observable;
	
   import GAME.src.TESTING.*;
   import GAME.src.maps.data.Boot;

   public class StateManager extends Observable{
   
      public static MapRepository repo = new MapRepository();
      public static int[][] map1 = (int[][]) repo.getStorage().get(0);
      public static TileGrid m = new TileGrid(map1);
      public boolean gameClosing;
      public java.util.concurrent.Semaphore semaphore;
      
      public StateManager( java.util.concurrent.Semaphore semaphore ){
         this.semaphore = semaphore;    
      }
   	
      public static enum GameState
      {
         MAINMENU, GAME, SETTINGS, EXIT, CHARACTERSELECT, MAPSELECT
      }
   
      public static GameState gameState = GameState.MAINMENU;
      public static MainMenu mainMenu;
      //public static World game;
      public static GameplayTester game;
      public static Settings settings;
      public static CharacterSelect characterSelect;
      public static MapSelect mapSelect;
   
      public void update(Graphics gee)
      {
         switch(gameState)
         {
            case MAINMENU:
               if (mainMenu == null)
                  mainMenu = new MainMenu();
               mainMenu.update(gee);
               break;
            case CHARACTERSELECT:
               if(characterSelect == null)
                  characterSelect = new CharacterSelect();
               characterSelect.update(gee);
               break;
            case MAPSELECT:
               if(mapSelect == null)
                  mapSelect = new MapSelect();
               mapSelect.update(gee);
               break;
            case GAME:
               if( game == null){
                  //should initialize game with data from characterSelect and mapSelect screens
                  game = new GameplayTester(mapSelect.mapName, characterSelect.playerOneChar, characterSelect.playerTwoChar );
                  gameClosing = false;   
                  gameChange();    
               	//game = new World(m);
               }
               //game.update(gee);
               if(game.isTesterDone()){
                  nullifyGameTester();
                  gameState = GameState.CHARACTERSELECT;
                  gameClosing = true;            
                  gameChange();
                  characterSelect = null;
               }
               break;
            case SETTINGS:
               if(settings == null)
                  settings = new Settings();
               settings.update(gee);
               break;
            case EXIT:
               System.exit(0);
               break;
         }
      }
   	
      public boolean isGameClosing(){
         return gameClosing;
      }
   	
      public void nullifyGameTester(){
         game = null;
      }
   	
      public void gameChange(){
         semaphore.release();
         setChanged();
         notifyObservers(); 
      }
   	
   }

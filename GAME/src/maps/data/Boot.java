   //package data;
   package GAME.src.maps.data;

   import java.awt.Canvas;
   import java.awt.Color;
   import java.awt.Graphics;
   import java.awt.image.BufferStrategy;
	
   public class Boot extends Canvas implements Runnable{ //Main class for Running
   
   /**
    * 
    */
      private static final long serialVersionUID = -7669774831421332541L;
      public static final int WIDTH = 900, HEIGHT = 450; //Default
   
      private boolean running = false; //is Running?
      private Thread thread;
   
      MapRepository repo;
      int[][] mapArray;
      TileGrid map;
    //Tile primary = new Tile(0, 0, 15, 15, TileType.Primary);
    //Tile secondary = new Tile(100, 0, 15, 15, TileType.Secondary);
      Tile bg;
      Tile mapTex;
   
      public Boot(){  // Default
      
         repo = new MapRepository();
         mapArray = (int[][]) repo.getStorage().get(0);
         map = new TileGrid( mapArray );
         bg = new Tile(-10, -10, WIDTH+20, HEIGHT+20, TileType.Background);
         mapTex = new Tile(-10, -10, WIDTH+20, HEIGHT+20, TileType.MapTex1);
      
      }
   	
      public int[][] getMap(){
         return mapArray;
      }
   	
      public synchronized void start(){
         if(running) //Safety precaution
            return;		
         running = true;
         thread = new Thread(this);	//Uses call in Window constructor to start thread
         thread.start();
      }
   
      public void run()	//game loop
      {
      
      /*
      
         long lastTime = System.nanoTime();
         double amountOfTicks = 25.0;
         double ns = 1000000000 / amountOfTicks;
         double delta = 0;
         long timer = System.nanoTime();
         int updates = 0;
         int frames = 0;
         while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
               tick();
               updates++;
               delta--;
            }
            render();
            frames++;
         		
            if(System.nanoTime() - timer > 1000000000){
               timer += 1000000000;
               System.out.println("FPS: " + frames + " TICKS: " + updates);
               frames = 0;
               updates = 0;
            }
         }
      
      */
      
      }
   	
   	
   	
   	
   //ticks = updates
      private void tick()
      {
      
      }
      private void render(){  //Reformat to take on a Graphics variable from the gameplay class
                              //and return it. Reason: map class can render outside of the gameplay class
      								//clearing up clutter from the gameplay class.
      								//
      								//Also need to outfit the Boot Class to have methods to obtain information about
      								//the map: tile locations/ configurations, map size, tile types, pasible/impassible,
      								//etc.
      								//
      								//Also, set up the Boot Class so that it is no longer extends the Canvas Class, 
      								//and so that it no longer implements the Runnable Interface.
      
         BufferStrategy bs = this.getBufferStrategy();	//loads upto 3 buffers if time allows
         if(bs == null){									//only displays top buffer
            this.createBufferStrategy(2);
            return;
         }
      
         Graphics g = bs.getDrawGraphics();
      
      //--------------Draw Here--------------\\
         g.setColor(Color.black);
         drawTile(g, bg);
         //drawMap(g, map);
         drawTile(g, mapTex);
         g.setColor(Color.white);
       //drawTile(g, primary);
       //drawTile(g, secondary);
      //--------------------------------------\\
         g.dispose();
         bs.show();
      
      }
   
      public void drawTile(Graphics g, Tile t)
      {
         g.drawImage(t.getImage(),t.getX(), t.getY(), t.getWidth(), t.getHeight(), null);
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
   	
   	
   	
   	
      public static void main(String[] args)
      {
         //~~~~~~~~~~~~~~~~~~~~~~~~~~~
         //new Window(WIDTH, HEIGHT, "Super Smash", new Boot()); //Calls Window constructor (sets up window and displays)
         //~~~~~~~~~~~~~~~~~~~~~~~~~~~   
      }
   	
   	
   	
   }

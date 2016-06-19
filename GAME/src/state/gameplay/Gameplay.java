   package GAME.src.state.gameplay;  

   import java.awt.*;
   import java.awt.event.KeyEvent;
   import java.awt.event.KeyListener;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import java.awt.geom.Area;
   import java.awt.geom.Path2D;
   import java.awt.geom.Rectangle2D;
   import java.awt.image.*;
   import java.awt.image.BufferStrategy;  
   import java.io.*;
   import java.lang.Runnable;
   import java.math.RoundingMode;
   import java.text.DecimalFormat;
   import java.util.*;

   import javax.imageio.ImageIO;
   import javax.swing.ImageIcon;
   import javax.swing.Timer;
   import javax.swing.JPanel;
   import javax.swing.JLabel;

   import GAME.resources.*;
   import GAME.src.state.gameplay.MatchConstants;
   import GAME.src.matchClasses.actor.Player;
   import GAME.src.matchClasses.object.Hitbox;
   import GAME.src.matchClasses.Vector2D;
   import GAME.src.maps.data.*;
   import GAME.src.maps.res.*;


   public class Gameplay extends JPanel implements Runnable {
   
      InputHandler keyboard = new InputHandler();
      private volatile boolean running;
      private boolean paused;
      private boolean gameOver;
      private boolean endScreenCleared = false;
   
      private boolean infiniteStock;
      private long nanoTimeAllotted;
      private int numStock;
   
      private Thread gameThread;
   //Collection of GameObjects
   
   //DISPLAY / TESTING
      Image originalBackground = null; 
      Image displayBackground = null;
   
      Image originalStartText = null;
      Image displayStartText = null;
   
      BufferedImage originalMap = null;
      Image displayMap = null;
   
      BufferedImage originalMario = null;
      Image displayMario = null;
   
      BufferedImage originalLuigi = null;
      Image displayLuigi = null;
   
   //Imaging variables & objects
   
   
   //Players
      private Player[] players;
      Player p1;
      Player p2;
   
      int p1DisplayWidth;
      int p1DisplayHeight;
      int p1XDisplayCoord;
      int p1YDisplayCoord;  
   
      int p2DisplayWidth;
      int p2DisplayHeight;
      int p2XDisplayCoord;
      int p2YDisplayCoord;
   
      String p1Char;
      String p2Char;
   
      Color redTint;
      Color greenTint;
   
   //Background and Maps;
      int bgDispWidth;
      int bgDispHeight;
   
      private Boot mapBoot;
      int[][] mapArray;
      Dimension mapSize;
   
      int[] mapPixelXCoords;
      int[] mapPixelYCoords;
      double[] mapXCoords;
      double[] mapYCoords;
   
   //HUD
      JLabel timeLabel;
      JLabel escMessage;
      JLabel mid;
      JLabel p1Stats;
      JLabel p2Stats;
      Font fTop;  
      Font fBottom;  
      FontMetrics fmTopRow;
      FontMetrics fmBottomRow;
      Dimension fSizeTopRow;
      Dimension fSizeBottomRow;
   
   //End Screen vars
      float flo = 0.0f;
   
   //Stage collision troubleshooting
      Path2D stagePath = new Path2D.Double(); 
   
   //Other
      int[][] keyIDs; 
   
   //TIMING
      long tStart;
      Timer clock;
      int tCount;
      DecimalFormat timeFormat;
      double timeSeconds;
      int timeMinutes;
   
   //~~~~~~~~~~~~~ 	
   //DEFAULT Gameplay Object
   //~~~~~~~~~~~~~
      public Gameplay( Boot boot, String map, String p1Character, String p2Character ) {
      
         mapBoot = boot;
         numStock = MatchConstants.DEFAULT_STOCK;
         init( map, p1Character, p2Character );    
      
      }
   
      public synchronized void start(){
         if(running)
            return;		
         running = true;
         gameThread = new Thread(this); // Come back to set up safe stopping and suspension of the gameThread
         gameThread.start();
         mapBoot.start();
      }
   
      public Thread getThread(){
         return gameThread;
      }
   
   //public Gameplay( Map map, ArrayList<Player> players, GameType type ){}
   
   //~~~~~~~~~~~~~~
   //DEFAULT Init method
   //~~~~~~~~~~~~~~
      public final void init( String mapName, String p1Char, String p2Char ){
      
         System.out.println("Initializing Gameplay...");
      //Threads/ Timing
         tStart = 0L;
         running = false;
         paused = false;
         gameOver = false;
      
      //JPanel Stuff
         requestFocus();
         requestFocusInWindow();
         setMinimumSize( new Dimension( 800, 450 ) );
         setMaximumSize( new Dimension( 1600, 900 ) );
         setBackground( Color.BLACK );
         setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
      
      //HUD
      
         fTop = new Font( "Impact", Font.ITALIC, 24 );
         fmTopRow = getFontMetrics(fTop);
         int w1 = fmTopRow.stringWidth("ELAPSED TIME:  00:00.00  ");
         int h1 = fmTopRow.getHeight();
         fSizeTopRow = new Dimension(w1,h1);
      ///*
         fBottom = new Font( "Impact", Font.PLAIN, 20 );
         fmBottomRow = getFontMetrics(fBottom);
         int w2 = fmBottomRow.stringWidth("P# STOCK");
         int h2 = fmBottomRow.getHeight()*4;
         fSizeBottomRow = new Dimension(w2,h2);
      //*/
      
         timeLabel = 
            new JLabel(""){
               @Override
               public Dimension getPreferredSize(){
                  if(isGameOver()){
                     int w = fmTopRow.stringWidth(timeLabel.getText());
                     int h = fmTopRow.getHeight();
                     return new Dimension(w,h);
                  }
                  else
                     return fSizeTopRow;
               }
            };
         timeLabel.setFont(fTop);
         timeLabel.setOpaque(false);
         timeLabel.setForeground(Color.WHITE);
         timeLabel.setHorizontalAlignment(JLabel.LEFT);
      
         escMessage = 
            new JLabel("DSBR"){
               @Override
               public Dimension getPreferredSize(){
                  return fSizeTopRow;
               }
            };
         escMessage.setFont(new Font("Impact", Font.PLAIN, 20));
         escMessage.setBackground(Color.GRAY);
      
         mid = 
            new JLabel(""){
               @Override
               public Dimension getPreferredSize(){
               //Font fMiddle = new Font( "Impact", Font.PLAIN, fBottom.getSize()*2 );
               //FontMetrics fmMiddleRow = getFontMetrics(fMiddle);
                  FontMetrics fmMiddleRow = getFontMetrics(mid.getFont());            
                  int w3, h3;
                  if(mid.getText().replaceAll("<.*?>","").equals("TIE"))
                     w3 = fmMiddleRow.stringWidth("TIE");
                  else
                     w3 = fmMiddleRow.stringWidth("P# DEFEATED");
                  h3 = fmMiddleRow.getHeight()*2 + fmMiddleRow.getLeading();
                  return new Dimension(w3,h3);
               }
            };
         mid.setHorizontalAlignment(JLabel.CENTER);
         mid.setFont(new Font("Impact", Font.PLAIN, 40));
         mid.setForeground(Color.WHITE);
      
         p1Stats = 
            new JLabel(""){
               @Override
               public Dimension getPreferredSize(){
                  return fSizeBottomRow;
               }
            };
         p1Stats.setFont(fBottom);
         p1Stats.setForeground(Color.WHITE);
         p1Stats.setBackground(Color.BLACK);
         p1Stats.setHorizontalAlignment(JLabel.CENTER);
         p1Stats.setOpaque(true); 
      
         p2Stats = 
            new JLabel(""){
               @Override
               public Dimension getPreferredSize(){
                  return fSizeBottomRow;
               }
            };
         p2Stats.setFont(fBottom);      
         p2Stats.setForeground(Color.WHITE);
         p2Stats.setBackground(Color.BLACK);
         p2Stats.setHorizontalAlignment(JLabel.CENTER);
         p2Stats.setOpaque(true); 
      
      //Adding the HUD to the panel
      
      //Buffer - top left
         c.gridx = 0;
         c.gridy = 0;
         c.weightx = 0.5;
         c.weighty = 1.0; 
         c.fill = GridBagConstraints.NONE;
         c.anchor = GridBagConstraints.PAGE_START;
         add(escMessage, c);
      
      //Time
         c.gridx = 1;
         c.gridy = 0;
         c.weightx = 0.5;
         c.fill = GridBagConstraints.NONE;
         c.anchor = GridBagConstraints.FIRST_LINE_START;
         add(timeLabel, c);
      //Row - mid
         c.gridx = 0;
         c.gridy = 1;
         c.weightx = 1.0;
         c.gridwidth = 2;
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
         add(mid, c);
         mid.setVisible(false);
      //Row - bottom LEFT     
         c.gridx = 0;
         c.gridy = 2;
         c.weightx = 1.0;
         c.weighty = 1.0;
         c.gridwidth = 1;
         c.insets = new Insets(0,0,0,5);
         c.fill = GridBagConstraints.NONE;
         c.anchor = GridBagConstraints.LAST_LINE_END;
         add(p1Stats, c);
         p1Stats.setVisible(false);
      // Row - bottom RIGHT     
         c.gridx = 1;
         c.gridy = 2;
         c.weightx = 1.0;
         c.weighty = 1.0;
         c.gridwidth = 1;
         c.insets = new Insets(0,5,0,0);
         c.fill = GridBagConstraints.NONE;
         c.anchor = GridBagConstraints.LAST_LINE_START;
         add(p2Stats, c);
         p2Stats.setVisible(false);
      
      //Hookup keyboard polling
         this.addKeyListener( keyboard );
      
      //Initialize the Players Characters
      //TODO
      
      //IMAGE Test
         try{
            originalStartText = new ImageIcon(getClass().getClassLoader().getResource( "GAME/resources/matchCountdown.gif" )).getImage();  
            displayStartText = originalStartText;
         }
            catch(Exception e){
               e.printStackTrace();
            }      
      
      //Background and Map/ Stage
      
         try{
            if(mapName.equals(MatchConstants.MAP1_NAME)){
               originalBackground = new ImageIcon(getClass().getClassLoader().getResource( "GAME/src/maps/res/bg.gif" )).getImage();
               displayBackground = originalBackground;
               originalMap = ImageIO.read( new File( "GAME/src/maps/res/map 1.png" ));
               displayMap = originalMap;
               mapPixelXCoords = MatchConstants.MAP1_XCOORDS;
               mapPixelYCoords = MatchConstants.MAP1_YCOORDS;
            }
            else if(mapName.equals(MatchConstants.MAP2_NAME)){
               originalBackground = new ImageIcon(getClass().getClassLoader().getResource( "GAME/src/maps/res/flametest.gif" )).getImage();
               displayBackground = originalBackground;
               originalMap = ImageIO.read( new File( "GAME/src/maps/res/map2tex.png" ));
               displayMap = originalMap;
               mapPixelXCoords = MatchConstants.MAP2_XCOORDS;
               mapPixelYCoords = MatchConstants.MAP2_YCOORDS;
            }
            else{
               originalBackground = new ImageIcon(getClass().getClassLoader().getResource( "GAME/src/maps/res/bg.gif" )).getImage();
               displayBackground = originalBackground;
               originalMap = ImageIO.read( new File( "GAME/src/maps/res/map 1.png" ));
               displayMap = originalMap;
               mapPixelXCoords = MatchConstants.MAP1_XCOORDS;
               mapPixelYCoords = MatchConstants.MAP1_YCOORDS;
            }
         
         }
            catch(IOException e){
               e.printStackTrace();
            }
      
         bgDispWidth = (int) this.getPreferredSize().getWidth();
         bgDispHeight = (int) this.getPreferredSize().getHeight();
      
         mapArray = mapBoot.getMap();
         mapSize = new Dimension( mapArray[0].length, mapArray.length );
      
         mapXCoords = new double[4];
         mapYCoords = new double[4];
      
         for( int i=0; i<4; i++ ){
            mapXCoords[i] = (1.0*mapSize.getWidth()/originalMap.getWidth())*(mapPixelXCoords[i]-originalMap.getWidth()*0.5);
            mapYCoords[i] = (1.0*mapSize.getHeight()/originalMap.getHeight())*(originalMap.getHeight()*0.5-mapPixelYCoords[i]);
         }
      
         stagePath.moveTo( mapXCoords[0], mapYCoords[0] );
         for( int i=1; i<mapXCoords.length; i++ ){
            stagePath.lineTo( mapXCoords[i], mapYCoords[i] );
         }
         stagePath.closePath();
      
      //Initialize the new Players & Their Characters
      
         try{
         //originalMario = ImageIO.read( new File( "GAME/src/TESTING/8-bit-mario.jpg"));
         //originalLuigi = ImageIO.read( new File( "GAME/src/TESTING/8-bit-luigi.png"));
         
         //originalMario = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            originalMario = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            originalLuigi = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            displayMario = originalMario;
            displayLuigi = originalLuigi;
         
         }
            catch(IOException e){
               e.printStackTrace();
            }
      
         this.p1Char = p1Char;
         this.p2Char = p2Char;
      
         System.out.println("P1 is " + p1Char);
         System.out.println("P2 is " + p2Char);
      
         players = new Player[2];
         p1 = new Player( 1, new Vector2D(1.9,2.20161291), new Vector2D(-0.25*mapSize.getWidth(), 0.25*mapSize.getHeight()));     
         p2 = new Player( 2, new Vector2D(1.9,2.20161291), new Vector2D(0.25*mapSize.getWidth(), 0.25*mapSize.getHeight()));
         players[0] = p1;
         players[1] = p2;
      
         p1DisplayWidth = (int)( p1.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      
         p2DisplayWidth = (int)( p2.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p2DisplayHeight = (int)( p2.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      
         redTint = new Color(120,0,0);
         greenTint = new Color(0,120,0);
      
         keyIDs = new int[2][7];
         keyIDs[0] = MatchConstants.P1_KEY_CODES;
         keyIDs[1] = MatchConstants.P2_KEY_CODES;
         
      //Timing   
         timeFormat = new DecimalFormat( "0.00" );
         timeFormat.setRoundingMode(RoundingMode.DOWN);
      
         tCount = 0;
         timeSeconds = 0.0;
         timeMinutes = 0;
      
         clock = new Timer( 1000, 
               new ActionListener(){
                  public void actionPerformed( ActionEvent e ){
                     tCount++;
                  }
               });
      
         System.out.println("Gameplay Initialized");
      
      }
   
      public double getDesiredAspectRatio(){
         return MatchConstants.VIEWPORT_ASPECT_RATIO;
      }
   
      public void resizePanel(){
      
         int parentX = (int)( this.getParent().getWidth() );
         int parentY = (int)( this.getParent().getHeight() );
         double currentAspectRatio = 1.0*parentX/parentY;
         int xMax = (int)( this.getMaximumSize().getWidth() );
         int yMax = (int)( this.getMaximumSize().getHeight() );
         Dimension d;        	
      
         if( parentX >= xMax && parentY >= yMax ){
            this.setPreferredSize( this.getMaximumSize() );
         }
         else {
         
            if( currentAspectRatio >= this.getDesiredAspectRatio() )
               d = new Dimension( (int)( 1.0*parentY * this.getDesiredAspectRatio() ), parentY );
            else
               d = new Dimension( parentX, (int)(1.0*parentX / this.getDesiredAspectRatio() ) );
         
            if( d.getWidth() <= xMax && d.getHeight() <= yMax ){ 
               this.setPreferredSize( d );
               this.setMinimumSize( d );
            }
         
         }
      
         this.revalidate();
         this.resizeGraphics();
      
      }
   
      public boolean isMatchPaused(){
         return paused;
      }
   
      public boolean isGameOver(){ 
         return gameOver;
      }
   
      public void startGame( Timer startTimer){
         startTimer.start();
         while( tCount <=3 && running ){
            renderGame( 0 );
         }
         startTimer.stop();
         tStart = System.nanoTime();
         System.out.println("GAME START");
         p1Stats.setVisible(true);
         p2Stats.setVisible(true);
         updatePlayerStats();
      }
   
      public void endGame( boolean forceClosed ){
         if(forceClosed){
            running = false;
            gameOver = true;
         //System.out.println("gameplayThread closed Forcefully");
            endScreenCleared = true;
            return;
         }
         else{
            running = false;
            System.out.println("GAME OVER");
         //...
         //...
         //Do end sequence 
         //Finalize any endgame data necessary
         
            showEndScreen();
         
         /*
         this.addKeyListener(
               new KeyListener(){
                  @Override
                  public void keyReleased(KeyEvent e){
                     if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
                        //gameThread.interrupt();
                        System.out.println("gameplayThread Interrupted Naturally");
                        endScreenCleared = true;
                     }
                  }
                  public void keyPressed(KeyEvent e){}
                  public void keyTyped(KeyEvent e){}
               }
            );
         	*/
         }
      }
   
      public void showEndScreen(){
      
       //Update JLabel text fields
         if( (p1.isDefeated() && p2.isDefeated()) || (!p1.isDefeated() && !p2.isDefeated()) )
            mid.setText("<html><body><center>TIE</body></html>");
         else if(p1.isDefeated())
            mid.setText("<html><body><center>P1 DEFEATED<br>P2 WINS</body></html>");
         else
            mid.setText("<html><body><center>P2 DEFEATED<br>P1 WINS</body></html>");
      		
         for(Player p: players){
            int num = p.getPlayerNumber();
            int stock = numStock - p.getLivesLost();
            int damage = (int)(p.getTotalDamagePercentage());
            String text = "<html>"+
                       "<body>"+
                       //"<p>"+
               		  "<center>"+
                       "P"+num+" FINAL STOCK<br>"+
               		  stock+"<br>"+
               		  " TOTAL DAMAGE<br>"+
               		  damage+"%"+
                       //"</p>"+
                       "</body>"+
                       "</html>";
         
            switch (num){
               case 1:   p1Stats.setText(text);
                  break;
               case 2:   p2Stats.setText(text);
                  break;
               default: 
                  break;
            }
         }
      
         
         //getLayout().removeLayoutComponent(escMessage);
         //getLayout().removeLayoutComponent(timeLabel);
         //getLayout().removeLayoutComponent(mid);
         //getLayout().removeLayoutComponent(p1Stats);
         //getLayout().removeLayoutComponent(p2Stats);
         removeAll();      
         setLayout(new GridBagLayout());
      
         GridBagConstraints c = new GridBagConstraints();
      
      //Top Row ("mid" label)
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 3;
         c.weightx = 1.0;
         c.weighty = 1.0;
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
         add(mid, c);
      //Bottom Left (P1 Stats) 
         c.gridx = 0;
         c.gridy = 1;
         c.gridwidth = 1;
         c.weightx = 0.75;  
         c.weighty = 1.0;    
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.LINE_END;
         //p1Stats.setOpaque(true);
         //p1Stats.setBackground(Color.BLUE);
         add(p1Stats, c);
      //Bottom Center (P2 Stats)   
         c.gridx = 1;
         c.gridy = 1;
         c.gridwidth = 1;
         c.weightx = 0.75; 
         c.weighty = 1.0;     
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.LINE_START;
         //p2Stats.setOpaque(true);
         //p2Stats.setBackground(Color.RED);      
         add(p2Stats, c);
      //Bottom Right (Timer)
         c.gridx = 2;
         c.gridy = 1;
         c.gridwidth = 1;
         c.weightx = 1.0;  
         c.weighty = 1.0;    
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
      //timeLabel.setOpaque(true);
		   timeLabel.setHorizontalAlignment(JLabel.CENTER);
         add(timeLabel, c);
      
         revalidate();
      
         mid.setSize(mid.getPreferredSize());
         mid.setOpaque(false);   
         mid.setVisible(true);
      }
   
      public void pauseMatch(){ 
         paused = true;
         System.out.println("PAUSED");
      }
   
      public void resumeMatch(){
         paused = false;
         System.out.println("RESUMED");
      }       	
   
   
   //For the run() method
   //
   //To initialize
   //1	Background
   //2	Map (Then combine 1&2 to make the backBuffer) --(sidenote: backbuffer may need to be alterable for dynamic match viewport)
   //3	Characters
   //3' 	Particle Effects 
   //4	HUD
   //5	Add Listeners
   //6	Countdown to Match Start
   //7	Activate Listener Control
   //7'	START MATCH
   
   //Gameplay update()->
   //2, 3, 3', & 4
   
   //End Game Sequence
   //1	Stop gameplay	
   //2	Cleanup & Exit
   //3	Show Match Results
   
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
      @Override
      public void run() {	  
      
         long next_game_tick = System.nanoTime();
         int loops;
         float interpolation;
      
         while( running ){
         //Before game
            startGame( clock );
            while( !(gameOver) ){
            
               while(!(paused || gameOver)){
               
                  loops = 0;
               
                  while( System.nanoTime() > next_game_tick && loops < MatchConstants.MAX_FRAMESKIP ){
                     updateGame();
                  
                     next_game_tick += MatchConstants.SKIP_TICKS;
                     loops++;
                  }
               
                  interpolation = (float)( System.nanoTime() + MatchConstants.SKIP_TICKS - next_game_tick) / (float)( MatchConstants.SKIP_TICKS );           
                  renderGame( interpolation );
               }
            
            }
            endGame(false);
         }
         while(!endScreenCleared){
            repaint();
            try{
               gameThread.sleep(50);
            }
               catch(InterruptedException e){
                  e.printStackTrace();
               } 
         }
      }
   
      public void updateGame(){
      
      // 1) Poll keyboard to allow for processing   
      
         keyboard.poll();
      
      // 2) Simulate Physics for each Player & any moving game objects. 
      //    (Don't do anything for stationary objects, as those will be handled by the other objects collision detections).
      // a) Read the keyboard poll and update player 1
      // b) Read the keyboard poll and update player 2  
      // c) Simulate physics for any other moving objects
      // These Steps involve reading the current states of the objects
      
         updatePlayers(); 
      
      // 3) Detect Collisions
      //   (when players sprites are updated, they will play any sounds from their player objects)
      // a) Detect Collisions for player 1, then player 2
      // b) Update states of objects if necessary
      
         detectCollisions();     	
      
      // Find where to loop background music, and any other higher level sounds
      // within the Gameplay Class (e.g. match start and game over sounds)
      
      // 4) Update Game Statistics (e.g. stock, deaths, respawns, time elapsed, 
      //                       other info included in Match Results, etc.)
      
      }
   
   
   
   // PLAYER METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
   
      public void updatePlayers(){
      
         for( Player p: players ){
            if( p.getLifeState() == Player.LifeState.ALIVE ){ 
            //ALIVE
               move(p);
            }
            else if( p.getLifeState() == Player.LifeState.RESPAWNING ){
            //RESPAWNING
               if( anyKeysDown(p) ){
                  move(p);
                  p.setLifeState( Player.LifeState.ALIVE );
               }
            }
            else{
            //DEAD
               if( p.getLivesLost() < numStock ){
                  p.respawn();
               }
               else{
                  System.out.println("P" + p.getPlayerNumber() + " DEFEATED");
                  p.setDefeated();
                  gameOver = true;
               }
            }
         }
      
      }
   
      public void move( Player p ){
         int i = p.getPlayerNumber() - 1;
         p.doMove( new boolean[] { keyboard.keyDown( keyIDs[i][0] ), keyboard.keyDown( keyIDs[i][1] ), 
               keyboard.keyDown( keyIDs[i][2] ), keyboard.keyDown( keyIDs[i][3] ), 
               keyboard.keyDown( keyIDs[i][4] ), keyboard.keyDown( keyIDs[i][5] ), 
               keyboard.keyDown( keyIDs[i][6] ) } );
      }
   
      public void detectCollisions(){
      
      //PLAYER LEFT MAP
         for( Player p: players){
            if( hasLeftMap(p) && p.getLifeState() == Player.LifeState.ALIVE ){
               p.die();
               updatePlayerStats();
            }
         }
      
      //PLAYER COLLISIONS
         if( p1.getLifeState() == Player.LifeState.ALIVE && p2.getLifeState() == Player.LifeState.ALIVE ){
         //Player to Player
            if( p2pCollision() ){
               resolveP2Pcollision();
            }
            doP2PSecondaryCollisions();
            updatePlayerStats();
         }
      
      //Player to Stage
         for( Player p: players ){
            if( p.getVerticalMotion() != Player.VerticalMotion.NONE ){
            //Checks and Resolves Player-Stage Collisions
               if( p.getLifeState() == Player.LifeState.ALIVE )
                  evalStageCollisions( p );
            }
         }
      
      }
   
      public boolean anyKeysDown( Player p ){
         int i = p.getPlayerNumber() - 1;
         if( keyboard.keyDown( keyIDs[i][0] ) || keyboard.keyDown( keyIDs[i][1] ) || keyboard.keyDown( keyIDs[i][2] ) || keyboard.keyDown( keyIDs[i][3] ) || 
         keyboard.keyDown( keyIDs[i][4] ) || keyboard.keyDown( keyIDs[i][5] ) || keyboard.keyDown( keyIDs[i][6] ) )
            return true;
         return false;
      }
   
      public boolean hasLeftMap( Player p ){
         if( p.getPos().getX() + p.width() < -0.5*mapSize.getWidth()-4 || p.getPos().getX() > 0.5*mapSize.getWidth()+4 
         || p.getPos().getY() + p.height() < -0.5*mapSize.getHeight()-2.25 || p.getPos().getY() > 0.5*mapSize.getHeight()+2.25)
            return true;
         return false;
      }
   
      public boolean p2pCollision(){
      
         if( (p1.getPos().getX() + p1.width()) < p2.getPos().getX() || p1.getPos().getX() > (p2.getPos().getX() + p2.width()) ) 
            return false;
         if( (p1.getPos().getY() + p1.height()) < p2.getPos().getY() || p1.getPos().getY() > (p2.getPos().getY() + p2.height()) ) 
            return false;
      
         return true;
      }
   
      public void doP2PSecondaryCollisions(){
      
         if(p1.getLifeState() == Player.LifeState.ALIVE && p2.getLifeState() == Player.LifeState.ALIVE){
         
            Rectangle2D p1BodyBounds = new Rectangle2D.Double(p1.getPos().getX(), p1.getPos().getY() + p1.height(), p1.width(), p1.height());        
            Rectangle2D p2BodyBounds = new Rectangle2D.Double(p2.getPos().getX(), p2.getPos().getY() + p2.height(), p2.width(), p2.height());
            Rectangle2D p1SecondaryAttackArea;
            Rectangle2D p2SecondaryAttackArea;
            Rectangle2D resultP1HitP2 = new Rectangle2D.Double();
            Rectangle2D resultP2HitP1 = new Rectangle2D.Double();
            boolean p1Hitp2 = false;
            boolean p2Hitp1 = false;
         	
            if(p1.doingSecondary){
               if( p1.getDirection() == Player.Direction.FACING_RIGHT )
                  p1SecondaryAttackArea = new Rectangle2D.Double( p1.getPos().getX() + p1.width(), p1.getPos().getY() + p1.height(), p1.width()*0.75, p1.height()/2.0 );
               else
                  p1SecondaryAttackArea = new Rectangle2D.Double( p1.getPos().getX() - p1.width()*0.75, p1.getPos().getY() + p1.height(), p1.width()*0.75, p1.height()/2.0 );
            
               Rectangle2D.intersect(p2BodyBounds, p1SecondaryAttackArea, resultP1HitP2);
            
               if(resultP1HitP2.getWidth() > 0.0 && resultP1HitP2.getHeight() > 0){
                  p1Hitp2 = true;
               } 
            }
            else if(p2.doingSecondary){
               if( p2.getDirection() == Player.Direction.FACING_RIGHT )
                  p2SecondaryAttackArea = new Rectangle2D.Double( p2.getPos().getX() + p2.width(), p2.getPos().getY() + p2.height(), p2.width()*0.75, p2.height()/2.0 );
               else
                  p2SecondaryAttackArea = new Rectangle2D.Double( p2.getPos().getX() - p2.width()*0.75, p2.getPos().getY() + p2.height(), p2.width()*0.75, p2.height()/2.0 );
            
               Rectangle2D.intersect(p1BodyBounds, p2SecondaryAttackArea, resultP2HitP1);
               System.out.println(resultP2HitP1.toString());
            
               if(resultP2HitP1.getWidth() > 0.0 && resultP2HitP1.getHeight() > 0){
                  p2Hitp1 = true;
               }
            }
         	
            if( p1Hitp2 && !(p2Hitp1) )
               p2.stun( 10*Math.max(resultP1HitP2.getWidth(), resultP1HitP2.getHeight()), false );
            else if( !(p1Hitp2) && p2Hitp1 )
               p1.stun( 10*Math.max(resultP2HitP1.getWidth(), resultP2HitP1.getHeight()), false );
            else if( p1Hitp2 && p2Hitp1 ){}
         	
         }
      
      }
   
      public void resolveP2Pcollision(){
      
         double penHalfWidth = 0.0;
         double penHalfHeight = 0.0;
      //PENETRATION WIDTH            
         if( p1.getVel().getX() > p2.getVel().getX() )
            penHalfWidth = ((p1.getPos().getX() + p1.width()) - p2.getPos().getX()) *0.5;
         else
            penHalfWidth = ((p2.getPos().getX() + p2.width()) - p1.getPos().getX()) *0.5;
      //PENETRATION HEIGHT
         if( p1.getVel().getY() > p2.getVel().getY() )
            penHalfHeight = ((p1.getPos().getY() + p1.height()) - p2.getPos().getY()) *0.5;
         else
            penHalfHeight = ((p2.getPos().getY() + p2.height()) - p1.getPos().getY()) *0.5;
      
      //Apply Collision Impulse
         if( penHalfHeight > penHalfWidth ){
            if( p1.getVel().getX() > p2.getVel().getX() ){
               p1.setPos( p1.getPos().getX() - penHalfWidth, p1.getPos().getY() );
               p2.setPos( p2.getPos().getX() + penHalfWidth, p2.getPos().getY() );
            }
            else{
               p1.setPos( p1.getPos().getX() + penHalfWidth, p1.getPos().getY() );
               p2.setPos( p2.getPos().getX() - penHalfWidth, p2.getPos().getY() );
            }
            double p1xTemp = p1.getVel().getX();
            p1.setVel( p2.getVel().getX(), p1.getVel().getY() );
            p2.setVel( p1xTemp, p2.getVel().getY() );
         }
         else if( penHalfWidth >= penHalfHeight ){
            if( p1.getVel().getY() > p2.getVel().getY() ){
               p1.setPos( p1.getPos().getX(), p1.getPos().getY() - penHalfHeight);
               p2.setPos( p2.getPos().getX(), p2.getPos().getY() + penHalfHeight);
            }
            else{
               p1.setPos( p1.getPos().getX(), p1.getPos().getY() + penHalfHeight);
               p2.setPos( p2.getPos().getX(), p2.getPos().getY() - penHalfHeight);
            }
            double p1yTemp = p1.getVel().getY();
            p1.setVel( p1.getVel().getX(), p2.getVel().getY() );
            p2.setVel( p2.getVel().getX(), p1yTemp );
         }
      
      }
   
      public void evalStageCollisions( Player p ){
      
         Area cross = new Area(new Rectangle2D.Double(p.getPos().getX(), p.getPos().getY(), p.width(), p.height()));
         cross.intersect(new Area(stagePath));
         if(cross.isEmpty())
            return;
      
         for( int i=0; i<mapXCoords.length; i++ ){
         
            double nearX, farX, nearY, farY;
            double Ax = mapXCoords[i];
            double Ay = mapYCoords[i];
            double Bx, By;
         
            if( i == mapXCoords.length-1 ){
               Bx = mapXCoords[0];
               By = mapYCoords[0];
            }
            else{
               Bx = mapXCoords[i+1];
               By = mapYCoords[i+1];
            }
         
            double deltaX = Bx-Ax;
            double deltaY = By-Ay;
            double scaleX = 1.0/deltaX;
            double scaleY = 1.0/deltaY;
         
            if( deltaX >= 0 ){
               nearX = p.getPos().getX();
               farX = nearX + p.width();
            }
            else{
               farX = p.getPos().getX();
               nearX = farX + p.width();
            }
            if( deltaY >= 0 ){
               nearY = p.getPos().getY();
               farY = nearY + p.height();
            }
            else{
               farY = p.getPos().getY();
               nearY = farY + p.height();
            }
         
            double nearTimeX = scaleX * ( nearX - Ax ); 
            double nearTimeY = scaleY * ( nearY - Ay );
            double farTimeX = scaleX * ( farX - Ax ); 
            double farTimeY = scaleY * ( farY - Ay );
         
            if( nearTimeX > farTimeY || nearTimeY > farTimeX )
               continue;
         
            double nearTime = Math.max( nearTimeX, nearTimeY );
            double farTime = Math.min( farTimeX, farTimeY );
         
            if( nearTime >= 1 || farTime <= 0 )
               continue;
         
            double x = p.getPos().getX();
            double y = p.getPos().getY();
         
         outerloop:
            switch (i){
            //TOP
               case 0:
                  if( p.getVel().getY() <= 0 ){
                     if( x > 0 ){
                        if( x < mapXCoords[1] && mapXCoords[1] < x+p.width() ){
                           double penX = mapXCoords[1] - x;
                           double penY = mapYCoords[1] - y;
                           if( penX <= penY )
                              break outerloop;
                        } 
                     }
                     else if( x < 0){
                        if( x < mapXCoords[0] && mapXCoords[0] < x+p.width() ){
                           double penX = x+p.width()-mapXCoords[0];
                           double penY = mapYCoords[0] - y;
                           if( penX <=penY )
                              break outerloop;
                        }            
                     }
                     p.setPos( x, mapYCoords[0] );
                     p.setVel( p.getVel().getX(), 0.0 );
                     p.setVerticalMotion( Player.VerticalMotion.NONE );
                  }
                  break;
            
            //RIGHT
               case 1:
                  p.setPos( Ax + nearTimeY*deltaX, y );
                  double magScale = 1.0/(Math.sqrt( deltaX*deltaX + deltaY*deltaY ) );
                  double unitABx = -1 * deltaX * magScale;
                  double unitABy = -1 * deltaY * magScale;
                  double newXVel = unitABx * p.getVel().getX();
                  double newYVel = unitABy * p.getVel().getY();
                  p.setVel( newXVel, newYVel );
                  break;
            
            //BOTTOM
               case 2:
                  p.setPos( x, mapYCoords[2] - p.height() );
                  p.setVel( p.getVel().getX(), 0.0 );
                  p.setVerticalMotion( Player.VerticalMotion.NONE );
                  break;
            
            //LEFT
               case 3:
                  p.setPos( (Ax + farTimeY*deltaX) - p.width(), y );
                  magScale = 1.0/(Math.sqrt( deltaX*deltaX + deltaY*deltaY ) );
                  unitABx = deltaX * magScale;
                  unitABy = deltaY * magScale;
                  newXVel = unitABx * p.getVel().getX();
                  newYVel = unitABy * p.getVel().getY();
                  p.setVel( newXVel, newYVel );
                  break;
            
            //DEFAULT/ ERROR
               default: System.out.println( "ERROR: STAGE COLLISION DETECTION" );
                  break;
            
            }
         
         }
      
      }
   
   
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
   
      public void updatePlayerStats(){ 
         for(Player p: players){
            int num = p.getPlayerNumber();
            int stock = numStock - p.getLivesLost();
            int damage = (int)(p.getDamagePercentage());
            String text = "<html>"+
                       "<body>"+
                       //"<p>"+
               		  "<center>"+
                       "P"+num+" STOCK<br>"+
               		  stock+"<br>"+
               		  "DAMAGE<br>"+
               		  damage+"%"+
                       //"</p>"+
                       "</body>"+
                       "</html>";
         
            switch (num){
               case 1:   p1Stats.setText(text);
                  break;
               case 2:   p2Stats.setText(text);
                  break;
               default: 
                  break;
            }
         }
      }
   
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
   
      public synchronized void renderGame( float interpolation ){
      
      //Choose sprites & correct display locations to preserve hitbox locations
      
      //Calculate Display Coords for Moving Objects
         p1XDisplayCoord = (int)(this.getWidth()*0.5 + (this.getWidth()*(p1.getPos().getX() + p1.getVel().getX()*interpolation))/mapSize.getWidth() );      
         p1YDisplayCoord = (int)(this.getHeight()*0.5 - (this.getHeight()*(p1.getPos().getY() + p1.getVel().getY()*interpolation))/mapSize.getHeight()-p1DisplayHeight );
      
         p2XDisplayCoord = (int)(this.getWidth()*0.5 + (this.getWidth()*(p2.getPos().getX() + p2.getVel().getX()*interpolation))/mapSize.getWidth() );      
         p2YDisplayCoord = (int)(this.getHeight()*0.5 - (this.getHeight()*(p2.getPos().getY() + p2.getVel().getY()*interpolation))/mapSize.getHeight()-p2DisplayHeight ); 
      
         characterGraphics();
           
      //Repaint
         repaint();
      
      }
   
      public void resizeGraphics(){
      
         pauseMatch();
      
         System.out.println("RESIZING GRAPHICS");
      
      //Just tell the SpriteLoader to resize all of its graphical elements
      //That way, images get handled outside of the Gameplay Class
      //Also helps to disjoint hitboxsize/playercoordinates from displaysize/ display coordinates
      //and allows for hitbox resizing without messing with the images.
         
      //PLAYERS
         p1DisplayWidth = (int)( p1.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
         p2DisplayWidth = (int)( p2.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p2DisplayHeight = (int)( p2.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      
         characterGraphics();
      
      //BACKGROUND
         bgDispWidth = (int) this.getPreferredSize().getWidth();
         bgDispHeight = (int) this.getPreferredSize().getHeight();
         displayBackground = originalBackground.getScaledInstance( bgDispWidth, bgDispHeight, Image.SCALE_FAST );
      //Start text gif
         displayStartText = originalStartText.getScaledInstance( (int)(bgDispWidth*0.5), (int)(bgDispHeight*0.5), Image.SCALE_FAST );
      //MAP TEXTURE
         displayMap = originalMap.getScaledInstance( bgDispWidth, bgDispHeight, Image.SCALE_SMOOTH );
      
      //HUD: JLabels
         timeLabel.setFont( timeLabel.getFont().deriveFont((float)(24.0/450*this.getPreferredSize().getHeight())) );
         escMessage.setFont( escMessage.getFont().deriveFont((float)(20.0/450*this.getPreferredSize().getHeight())) );
         p1Stats.setFont( p1Stats.getFont().deriveFont((float)(20.0/450*this.getPreferredSize().getHeight())) );
         p2Stats.setFont( p2Stats.getFont().deriveFont((float)(20.0/450*this.getPreferredSize().getHeight())) );
         mid.setFont( mid.getFont().deriveFont((float)(40.0/450*this.getPreferredSize().getHeight())) );;
      //HUD: Font Metrics
         fmTopRow = getFontMetrics(timeLabel.getFont());
         int w = fmTopRow.stringWidth("ELAPSED TIME:  00:00.00  ");
         int h = fmTopRow.getHeight();
         fSizeTopRow = new Dimension(w,h);
      
         fmBottomRow = getFontMetrics(p1Stats.getFont());
         int w2 = fmBottomRow.stringWidth("P# STOCK");
         int h2 = fmBottomRow.getHeight()*4 + fmBottomRow.getLeading()*3;
         fSizeBottomRow = new Dimension(w2,h2);
      
         mid.revalidate();
      
         resumeMatch();
      
      }
   
      public void characterGraphics(){
         if( p1.getActivity() == Player.Activity.STUNNED ){}
         if( p1.getActivity() == Player.Activity.IDLE )
            try{
               originalMario = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         else if( p1.doingSecondary == true )
            try{
               originalMario = ImageIO.read( new File("GAME/resources/mrgw_secondary.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         else if( p1.getActivity() == Player.Activity.DIVE || p1.getHorizontalMotion() != Player.HorizontalMotion.NONE )
            try{
               originalMario = ImageIO.read( new File("GAME/resources/mrgw_walking.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         else
            try{
               originalMario = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
       
         if( p2.getActivity() == Player.Activity.STUNNED ){} 
         if( p2.getActivity() == Player.Activity.IDLE )
            try{
               originalLuigi = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         else if( p2.doingSecondary == true )
            try{
               originalLuigi = ImageIO.read( new File("GAME/resources/mrgw_secondary.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         else if( p2.getActivity() == Player.Activity.DIVE || p2.getHorizontalMotion() != Player.HorizontalMotion.NONE )
            try{
               originalLuigi = ImageIO.read( new File("GAME/resources/mrgw_walking.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         else
            try{
               originalLuigi = ImageIO.read( new File("GAME/resources/mrgw_idle.png"));
            } 
               catch(Exception e){
                  e.printStackTrace();
               }
         
         displayMario = originalMario.getScaledInstance( p1DisplayWidth, p1DisplayHeight, Image.SCALE_SMOOTH );
         displayLuigi = originalLuigi.getScaledInstance( p2DisplayWidth, p2DisplayHeight, Image.SCALE_SMOOTH );
      }
   
   //TEST ONLY
      @Override
      public synchronized void paintComponent(Graphics g) {
         super.paintComponent(g);
      
         Graphics2D g2d = (Graphics2D) g;
      
         if(!running && endScreenCleared){
            g2d.setBackground(Color.BLACK);
         }
         else if(isGameOver()){
            for (int i = 0; i < this.getWidth(); i++) {
               for (int j = mid.getY(); j < mid.getY()+mid.getHeight(); j++) {
                  g2d.setColor(Color.getHSBColor(flo, 1.0f-(float)i/this.getWidth(), 1.0f-(float)(j-mid.getY())/mid.getHeight()));
                  g2d.drawLine(i,j,i,j);
               }
            }
            if( flo>= 1.0f )
               flo = 0.0f;
            else
               flo += 1.0/256f;   
         }
         else{
         
         //Background 
            g2d.drawImage( displayBackground, 0, 0, null );
         //Stage Image
            g2d.drawImage( displayMap, 0, 0, null );
         //Players (Hitboxes, Images, and Stats)
            for( Player p: players ){
               if( p.getPlayerNumber() == 1 ){
                  if(p1Char.equals("RED"))
                     g2d.setXORMode(redTint);
                  else
                     g2d.setXORMode(greenTint);
                  if(p1.getActivity() == Player.Activity.STUNNED)
                     g2d.setXORMode( new Color(200,200,200) );
                  if(p1.getDirection() == Player.Direction.FACING_RIGHT)
                     g2d.drawImage( displayMario, p1XDisplayCoord, p1YDisplayCoord, null);
                  else
                     g2d.drawImage( displayMario, p1XDisplayCoord + p1DisplayWidth, p1YDisplayCoord, -1*p1DisplayWidth, p1DisplayHeight, null);
               }
               else{
                  if(p2Char.equals("RED"))
                     g2d.setXORMode(redTint);
                  else
                     g2d.setXORMode(greenTint);
                  if(p2.getActivity() == Player.Activity.STUNNED)
                     g2d.setXORMode( new Color(200,200,200) );
                  if(p2.getDirection() == Player.Direction.FACING_RIGHT)
                     g2d.drawImage( displayLuigi, p2XDisplayCoord, p2YDisplayCoord, null);
                  else
                     g2d.drawImage( displayLuigi, p2XDisplayCoord + p2DisplayWidth, p2YDisplayCoord, -1*p2DisplayWidth, p2DisplayHeight, null);
               }
            }
         
            g2d.setPaintMode();
         
         //Intro
            if( tCount <= 3 ){
               g2d.drawImage( displayStartText, (int)((bgDispWidth-displayStartText.getWidth( null ))*0.5), 0, null );  
            }
            else{    
            //Match Time     
               timeSeconds = (System.nanoTime()-tStart)*0.000000001;
               if( timeSeconds >= (timeMinutes+1)*60 ){
                  timeMinutes++;
               }
               switch( timeMinutes ){
                  case 0:  
                     timeLabel.setText("ELAPSED TIME:  " + timeFormat.format(timeSeconds) + "  " );   
                     break;
                  default: 
                     String tSecText = timeFormat.format(timeSeconds-60*timeMinutes);
                     if( tSecText.charAt(1) == '.' )
                        tSecText = '0' + tSecText; 
                     timeLabel.setText("ELAPSED TIME:  " + timeMinutes + ": " + tSecText + "  " );
                     break;
               }   
            }         		
         }
         
      }
   
   }
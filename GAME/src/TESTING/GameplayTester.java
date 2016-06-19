   package GAME.src.TESTING;  

   import javax.swing.JFrame;
   import javax.swing.JPanel;

   import java.awt.GridBagConstraints;
   import java.awt.GridBagLayout;
   import java.awt.BorderLayout;
   import java.awt.Color;
   import java.awt.EventQueue;
   import java.awt.Dimension;
   import java.awt.Component;
   import java.awt.event.*;
   import java.awt.Rectangle;

   import java.util.Scanner;
   import java.util.Observable;
   import java.lang.Thread;

   import GAME.src.state.gameplay.*;
   import GAME.src.maps.data.*;

   public class GameplayTester extends Observable{
   
      public JFrame frame;
      private JPanel container;
      private Gameplay gameplay;
      private GAME.src.maps.data.Boot mapBoot;
      private boolean testerIsOver;
      private String mapName;
      String character1;
      String character2;
   
      public static void main(String[] args) {
      
         EventQueue.invokeLater(
               new Runnable() {
                  public void run() {
                     try {
                        GameplayTester window = new GameplayTester();
                        window.frame.setVisible(true);
                     } 
                        catch (Exception e) {
                           e.printStackTrace();
                        }
                  }
               });
      
      }
   
      public GameplayTester(){
         mapName = MatchConstants.DEFAULT_MAP_NAME;
         character1 = "RED";
         character2=  "GREEN";      
         initializeTester();
      }
   
      public GameplayTester(String mapName, String character1, String character2){
         this.mapName = mapName;
         this.character1 = character1;
         this.character2 = character2;
         initializeTester();
      }
   
      private void initializeTester(){
      
         System.out.println("Initializing GameplayTester...");
      
      //main frame
         frame = new JFrame( "Deep Space Battle Royale --- " + mapName );
         frame.addWindowListener( 
               new WindowAdapter(){
                  public void windowClosing(WindowEvent e){
                     testerIsOver = true;
                     if(gameplay.getThread().getState()!=Thread.State.TERMINATED){
                        gameplay.endGame(true);
                        try{
                           System.out.println("Waiting to join Game Thread");
                           gameplay.getThread().join(3000);
                           System.out.println("Game Thread Joined");
                        } 
                           catch(InterruptedException exc){
                              exc.printStackTrace();
                           }
                        System.out.println("Gameplay force closed");
                     }
                     frame.setVisible(false);
                     frame.dispose();
                     System.out.println("Tester frame cleared");
                     if(countObservers() == 0){
                        System.out.println("Tester force closed");
                        System.exit(0);
                     }
                     else{
                        System.out.println("***Tester has observers. Starting shutdown***");
                        setChanged();
                        notifyObservers();
                        System.out.println("Tester observers finished recieving notifications");   
                        System.out.println("Tester Completion");
                     }    
                  }
               });
         //frame.setSize(new Dimension(1200, 800));
         frame.setExtendedState(JFrame.MAXIMIZED_BOTH);      
         frame.setMinimumSize(new Dimension(800, 394));
         frame.setLocationRelativeTo(null);     
         frame.setResizable(true);
         frame.setVisible(true);
         frame.toFront();
         frame.requestFocus();     
         frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
      
      //container border frame
         container = new JPanel();
         container.setLayout( new GridBagLayout() );      
         container.setPreferredSize( new Dimension( 800, 450 ) );  
      //container.setBackground( Color.WHITE ); 
      
         container.addComponentListener( 
               new ComponentAdapter()
               {
                  @Override
                  public void componentResized(ComponentEvent evt) {
                     resizeInnerPanel();
                  }
               });
      
      //Boot
         mapBoot = new GAME.src.maps.data.Boot(); // needs: chosen map
      
      //gameplay screen
         gameplay = new Gameplay( mapBoot, mapName, character1, character2 ); // needs: characters, map, matchType and matchInfo ( time allotted or stock )
         gameplay.setFocusable( true );
      
      //Displaying the panels    
         frame.getContentPane().add(container);
         container.add( gameplay );
         gameplay.add( mapBoot );  //necessary while Boot is generatng a buffer strategy
         frame.pack();
      
         frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
         frame.setResizable(false);
      
         gameplay.requestFocus();
         testerIsOver = false;
      
         System.out.println("GameplayTester Initialized");
      
         gameplay.start();
      
      }
   
      public void resizeInnerPanel(){
         gameplay.resizePanel();
      }
   
      public boolean isTesterDone(){
      //return testerIsOver || gameplay.isGameOver();
         return testerIsOver;    
      }
   
      public Thread getGameThread(){
         return  gameplay.getThread();
      }
   
      public void testerInterrupt(){
         try{
            Thread.currentThread().interrupt();
         } 
            catch(Exception e){
               e.printStackTrace();
            }
         System.out.println("Tester Interrupted & Closed");
      }
   
   }
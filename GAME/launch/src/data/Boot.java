//package data;
package GAME.launch.src.data;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Observer;
import java.util.Observable;

import javax.swing.JFrame;

public class Boot extends Canvas implements Runnable, Observer{ //Main class for Running

/**
* 
*/
   private static final long serialVersionUID = -7669774831421332541L;
   public static final int WIDTH = 900, HEIGHT = 450; //Default
   public static final String TITLE = "Deep Space Battle Royale";

   private boolean running = false; //is Running?
   private Thread thread;

   public java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(0);

   public StateManager stateManager;

   JFrame mainFrame;

   public static void main(String[] args) {
   
      EventQueue.invokeLater(
               new Runnable() {
                  public void run() {
                     try {
                        Boot window = new Boot();
                        window.mainFrame.setVisible(true);
                     } 
                     catch (Exception e) {
                        e.printStackTrace();
                     }
                  }
               });
   }

   public Boot(){
      initializeBoot();
   }

   public void initializeBoot(){
      Dimension d = new Dimension(WIDTH, HEIGHT);  
      this.setPreferredSize(d);
      this.setMaximumSize(d);
      this.setMinimumSize(d);
   
      mainFrame = new JFrame(TITLE);
      mainFrame.add(this);
      mainFrame.pack();
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setResizable(false);
      mainFrame.setLocationRelativeTo(null);
      mainFrame.setVisible(true);
   
      stateManager = new StateManager(semaphore);
      stateManager.addObserver(this);
   
      this.start();  
   }

   public synchronized void start(){
      if(running) //Safety precaution
         return;		
      running = true;
      this.addMouseListener(new MouseInput());
      thread = new Thread(this);	//Uses call in Window constructor to start thread
      thread.start();
   }

   public void run()	//game loop
   {
      long lastTime = System.nanoTime();
      double amountOfTicks = 25.0;
      double ns = 1000000000 / amountOfTicks;
      double delta = 0;
      long timer = System.currentTimeMillis();
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
            render();
            frames++;
         }
      //render();
      //frames++;
      
         if(System.currentTimeMillis() - timer > 1000){
            timer += 1000;
            System.out.println("\t\tFPS: " + frames + "\tTICKS: " + updates);
         //System.out.println("...fps...");          
            frames = 0;
            updates = 0;
         }
      }
   }
//ticks = updates
   private void tick()
   {
   //
   }
   private void render()
   {
      BufferStrategy bs = this.getBufferStrategy();	//loads upto 3 buffers if time allows
      if(bs == null){									//only displays top buffer
         this.createBufferStrategy(2);
         return;
      }
   
      Graphics g = bs.getDrawGraphics();
   
   //--------------Draw Here--------------\\
      g.setColor(Color.black);
      stateManager.update(g);
      g.setColor(Color.white);
   //--------------------------------------\\
      g.dispose();
      bs.show();
   
   }

   @Override
   public void update( Observable observable, Object arg){
      if(!stateManager.isGameClosing()){
         mainFrame.setVisible(false);
         stateManager.game.addObserver(this);          
      ///*
         if(!stateManager.game.getGameThread().isInterrupted()){
            try{
               System.out.println("Waiting to join Game Thread");
               stateManager.game.getGameThread().join(3000);
               System.out.println("Game Thread Joined");
            } 
            catch(InterruptedException e){
               e.printStackTrace();
            }
         }
      //*/
      
         semaphore.acquireUninterruptibly();
         semaphore.drainPermits();
      
      //if( stateManager.game.getGameThread().isInterrupted() )
      //   System.out.println("Gameplay Interrupted");
      
      //stateManager.game.testerInterrupt();
      }
      else{
         mainFrame.setVisible(true);
      //stateManager.game.frame.setVisible(false);
      }
   }	

}

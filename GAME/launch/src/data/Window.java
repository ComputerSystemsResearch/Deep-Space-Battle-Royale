   //package data;
   package GAME.launch.src.data;

   import java.awt.Dimension;

   import javax.swing.JFrame;

   public class Window {	//Sets window size, properties; Starts thread
   
      public Window(int w, int h, String title, Boot gameBoot)
      {
         gameBoot.setPreferredSize(new Dimension(w,h));
         gameBoot.setMaximumSize((new Dimension(w,h)));
         gameBoot.setMinimumSize((new Dimension(w,h)));
      
         JFrame mainFrame = new JFrame(title);
      	
         mainFrame.add(gameBoot);
         mainFrame.pack();
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         mainFrame.setResizable(true);
         mainFrame.setLocationRelativeTo(null);
         mainFrame.setVisible(true);
      
         gameBoot.start();
      }
   
   }

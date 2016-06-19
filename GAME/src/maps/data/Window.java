   //package data;
	package GAME.src.maps.data;

   import java.awt.Dimension;

   import javax.swing.JFrame;

   public class Window {	//Sets window size, properties; Starts thread
   
      public Window(int w, int h, String title, Boot game)
      {
         game.setPreferredSize(new Dimension(w,h));
         game.setMaximumSize((new Dimension(w,h)));
         game.setMinimumSize((new Dimension(w,h)));
      
         JFrame frame = new JFrame(title);
         frame.add(game);
         frame.pack();
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setResizable(false);
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
      
         game.start();
      }
   
   }

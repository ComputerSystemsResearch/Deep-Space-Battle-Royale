   //package data;
	package GAME.launch.src.data;

   import java.awt.Graphics;
   import java.awt.Image;


   import javax.swing.ImageIcon;

   public class MainMenu {
   
      public static final int WIDTH = 900, HEIGHT = 450; //Default
      public static boolean musicOn;
      public static boolean sfxOn;
   
      private Image menuBG;
      public static AudioPlayer music;
      private UI menuUI;
      private Image play, settings, exit;
   
      public MainMenu()
      {
         this.menuBG = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/mainmenu.png")).getImage();
         this.play = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/play.png")).getImage();
         this.settings = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/settings.png")).getImage();
         this.exit = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/exit.png")).getImage();
         MainMenu.music = new AudioPlayer("GAME/launch/src/res/sciFi.wav");
         MainMenu.musicOn = true;
         MainMenu.sfxOn = true;
         this.menuUI = new UI();
         menuUI.addButton("Play", play, 650, 135, 167, 100);
         menuUI.addButton("Settings", settings, 637, 240, 200, 100);
         menuUI.addButton("Exit", exit, 650, 345, 167, 100);
         if(MainMenu.musicOn == true)
            music.play(); 
      //if(MainMenu.sfxOn == true)
      }
   
      private void updateButtons()
      {
      
      }
   
      public void update(Graphics g) 
      {
         g.drawImage(menuBG, 0, 0, WIDTH, HEIGHT, null);
         menuUI.draw(g);
      }
   
   }

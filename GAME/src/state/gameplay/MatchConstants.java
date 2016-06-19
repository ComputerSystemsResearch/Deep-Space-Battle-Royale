   package GAME.src.state.gameplay;  

   import java.awt.event.KeyEvent;  

   public final class MatchConstants{
   
      public static final double VIEWPORT_ASPECT_RATIO = (16.0/9);
   //KEYBOARD
      public static final int PAUSE_KEY_CODE = KeyEvent.VK_ESCAPE;
      public static final int[] P1_KEY_CODES = new int[] { KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
                                            KeyEvent.VK_I, KeyEvent.VK_O, KeyEvent.VK_P };
      public static final int[] P2_KEY_CODES = new int[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                                            KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3 };
   //GAME SPEED
      public static final int TICKS_PER_SECOND = 25;
      public static final int SKIP_TICKS = 1000000000 / TICKS_PER_SECOND;
      public static final int MAX_FRAMESKIP = 5;
   //MAP VALUES
	   public static final String DEFAULT_MAP_NAME = "Freefall";
      public static final String MAP1_NAME = "Freefall";
      public static final String MAP2_NAME = "Hellfire";
      public static final int[] MAP1_XCOORDS = new int[] {73, 827, 700, 190};
      public static final int[] MAP1_YCOORDS = new int[] {250, 250, 450, 450};
      public static final int[] MAP2_XCOORDS = new int[] {150, 740, 740, 150};
      public static final int[] MAP2_YCOORDS = new int[] {300, 300, 450, 450};
   //PHYSICAL CONSTANTS	
      public static final double MAX_RUNNING_SPEED = 12.0/MatchConstants.TICKS_PER_SECOND;
      public static final double RUNNING_ACCELERATION = (9.0/3)/(MatchConstants.TICKS_PER_SECOND);
      public static final double FRICTION = (5.0/6)/(MatchConstants.TICKS_PER_SECOND);
      public static final double GRAVITY = 1.5/(TICKS_PER_SECOND);
      public static final double REG_JUMP_VEL = 15.0 / MatchConstants.TICKS_PER_SECOND;
   //COOLDOWNS
      public static final long JUMP_CD = 500000000L;    // 0.50 seconds
      public static final long BIG_STUN_CD = 1000000000L;     // 1.00 seconds
      public static final long SMALL_STUN_CD = 250000000L;    // 0.25 seconds
      public static final long PRIMARY_CD = 250000000L;       // 0.25 seconds
      public static final long SECONDARY_CD = 500000000L;     // 0.50 seconds
   //DEFAULT MATCH VALUES
      public static final int DEFAULT_STOCK = 3;
   
      private MatchConstants(){
         throw new AssertionError();
      }
   }
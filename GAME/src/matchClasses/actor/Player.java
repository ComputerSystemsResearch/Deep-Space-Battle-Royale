   package GAME.src.matchClasses.actor;

   import java.awt.Dimension;
   import java.awt.geom.Point2D;
   import java.awt.event.KeyEvent;

   import GAME.src.state.gameplay.MatchConstants;
   import GAME.src.matchClasses.Vector2D;
   import GAME.src.matchClasses.object.Hitbox;

   public class Player{
   
   // Character character
      private Hitbox hitbox;
      private int playerNum, KOs, livesLost, jumpNumber, screenOrder;
      private double damagePercentage, totalDamagePercentage;
      private int[] playerKeyCodes;
   // 0 - UP
   // 1 - DOWN
   // 2 - LEFT
   // 3 - RIGHT
   // 4 - PRIMARY
   // 5 - SECONDARY
   // 6 - BLOCK
   
   //Cooldown Variables
      public boolean jumpCD, doingPrimary, doingSecondary, stunCD, struckHard;
      private long jumpT, primaryT, secondaryT, stunT;
   
   //Player Qualities & Variables
      private Vector2D characterSize;  // units
      private Vector2D originalSize;
      private Vector2D startingPosition;    
   
      private Vector2D pos_left_feet; // units
      private Vector2D vel_total;      // units/s
   
      private boolean defeated;
   //private Vector2D acc_walking;    // units/s/s
   //private Vector2D acc_total;      // units/s/s
   
      private double vel_max_running;  //(possibly) read from Chosen Character's stats
      private double acc_running;      //^^^ same
      private double acc_friction;     //^^^ same
      private double acc_gravity;
   
      public enum Direction { FACING_LEFT, FACING_RIGHT };
      public enum Activity { IDLE, CROUCH, DIVE, BLOCK, PRIMARY, SECONDARY, STUNNED };
      public enum VerticalMotion { NONE, UP, DOWN };
      public enum HorizontalMotion { NONE, LEFT, RIGHT };
      public enum LifeState { ALIVE, DEAD, RESPAWNING };
   
      private Direction facing;
      private Activity activity;
      private VerticalMotion yMotion;
      private HorizontalMotion xMotion;
      private LifeState lifeStatus;
   
   //Default Constructor
      public Player ( int playerNumber, Vector2D characterSize, Vector2D startingPosition ){
      
         playerNum = playerNumber;
         KOs = livesLost = jumpNumber = screenOrder = 0;
         damagePercentage = totalDamagePercentage = 0.0;
      
         jumpCD = doingPrimary = doingSecondary = stunCD = struckHard = defeated = false;
         jumpT = primaryT = secondaryT = stunT = 0L;
      
         this.characterSize = characterSize;
         originalSize = characterSize;
         this.startingPosition = new Vector2D( startingPosition );
      
         pos_left_feet = new Vector2D( startingPosition );
         vel_total = new Vector2D( 0.0, 0.0 );
      //acc_total = new Vector2D( 0.0, 0.0 );
      
         vel_max_running = MatchConstants.MAX_RUNNING_SPEED;
         acc_running = MatchConstants.RUNNING_ACCELERATION;
         acc_friction = MatchConstants.FRICTION;
         acc_gravity = MatchConstants.GRAVITY;
      
         if( playerNum == 1 ){
            facing = Direction.FACING_RIGHT;
            playerKeyCodes = MatchConstants.P1_KEY_CODES;
         //...
         }
         else if( playerNum == 2 ){
            facing = Direction.FACING_LEFT;
            playerKeyCodes = MatchConstants.P2_KEY_CODES;
         //...
         }
      
      //hitbox = new Hitbox( new Vector2D( pos_left_feet.getX()-(characterSize.getX()/2.0), pos_left_feet.getY() ), 
      //                     new Vector2D( pos_left_feet.getX()+(characterSize.getX()/2.0), pos_left_feet.getY() + characterSize.getY() ));
      
         activity = Activity.IDLE;
         xMotion = HorizontalMotion.NONE;
         yMotion = VerticalMotion.NONE;
         lifeStatus = LifeState.ALIVE;
      
      }
   
   //public Player ( int playerNumber, int characterID ){}
   
   
   //GET METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      public int[] getPlayerKeyCodes(){
         return playerKeyCodes;
      }
      public int getPlayerNumber(){
         return playerNum;
      }
      public Direction getDirection(){
         return facing;
      }
      public Activity getActivity(){
         return activity;
      }
      public HorizontalMotion getHorizontalMotion(){
         return xMotion;
      }
      public VerticalMotion getVerticalMotion(){
         return yMotion;
      }
      public LifeState getLifeState(){
         return lifeStatus;
      }
   
      public double width(){
         return characterSize.getX();
      }
      public double height(){
         return characterSize.getY();
      }
   
      public int getLivesLost(){
         return livesLost;
      }
      public int getKOs(){
         return KOs;   
      }
      public double getDamagePercentage(){
         return damagePercentage;
      }
      public double getTotalDamagePercentage(){
         return totalDamagePercentage;
      }
      public boolean isDefeated(){
         return defeated;
      }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   //SET METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
      public void setDirection( Direction direction ){ 
         facing = direction;
      }
      public void setActivity( Activity activity ){ 
         this.activity = activity;
      }
      public void setHorizontalMotion( HorizontalMotion xMotion ){ 
         this.xMotion = xMotion;
      }
      public void setVerticalMotion( VerticalMotion yMotion ){ 
         this.yMotion = yMotion;
      }
      public void setLifeState( LifeState lifeState){ 
         lifeStatus = lifeState;
      }
      public void setDefeated(){
         defeated = true;
      }
      public void setSize( Vector2D newSize ){
         characterSize.setX( newSize.getX() );
         characterSize.setY( newSize.getY() );
      }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   //PLAYER CONTROL
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   //POSITION
      public Vector2D getPos(){
         return pos_left_feet;
      }
      public void setPos( double xComp, double yComp ){
         pos_left_feet.set( xComp, yComp );
      }
   
   //VELOCITY	
      public Vector2D getVel(){
         return vel_total;
      } 	
      public void setVel( double xComp, double yComp ){
         vel_total.set( xComp, yComp );
      }
   
   //ACCELERATION
   /*public Vector2D getAcc(){
   return acc_total;
   }
   public void setAcc( double xComp, double yComp ){
   acc_total.set( xComp, yComp );
   }*/
   
   
   //This is where the action happens
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      public void doMove( boolean[] keysDown ){
      
      // - Check for keysDown
      // - Move Player/ Do Corresponding Actions
      // - Update Player's States
      
      //ASSESS COOLDOWNS
         updateCDs();
      
      //ASSESS COMMANDS
         if( activity != Activity.STUNNED ){   
         
         //Primary Attack
            if( !( doingPrimary || doingSecondary ) ){
               if( keysDown[4] ){
                  primaryAttack();
               }
               //Secondary Attack
               else if( keysDown[5] ){
                  secondaryAttack();
               }
            }
            //Block
            else if( keysDown[6] ){
               block();
            }
            //Idle
            else if( !doingPrimary && !doingSecondary){
               activity = Activity.IDLE;
            }	
         
         
         //Horizontal Motion Commands
            if( !( keysDown[3] && keysDown[2])){ //NOT RIGHT and LEFT
            //RIGHT ONLY
               if ( keysDown[3]){ 
                  moveRight();
               }
               //LEFT ONLY
               else if( keysDown[2]){
                  moveLeft();
               }
               else if ( yMotion == VerticalMotion.NONE && vel_total.getX() != 0 ){ 
                  slide(); 
               }
            }
         
         //Vertical Motion Commands
         //UP ONLY   	
            if( keysDown[0] && !jumpCD){
               jump();
            }
            //DOWN ONLY
            else if( keysDown[1] ){
               if( yMotion == VerticalMotion.NONE )
                  crouch();
               else
                  dive();  
            }
            else{
            //FALL
               vel_total.deltaY( -1*MatchConstants.GRAVITY );
            }
         
         }
      
      //Update Player's Position
         pos_left_feet.deltaX( vel_total.getX() );
         pos_left_feet.deltaY( vel_total.getY() );
      
      // - Update Player's States
         updateStates();
      
      }	 
   
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   //UPDATES
      public void updateCDs(){
         long tNow = System.nanoTime();
      
         if( jumpCD )
            if( tNow > jumpT + MatchConstants.JUMP_CD )
               jumpCD = false;
      
         if( stunCD )
            if( struckHard ){
               if( tNow > stunT + MatchConstants.BIG_STUN_CD ){
                  stunCD = false;
                  activity = Activity.IDLE;
               }
            }
            else if( tNow > stunT + MatchConstants.SMALL_STUN_CD ){
               stunCD = false;
               activity = Activity.IDLE;
            }
      
         if( doingPrimary )
            if( tNow > primaryT + MatchConstants.PRIMARY_CD )
               doingPrimary = false;
      
         if( doingSecondary )
            if( tNow > secondaryT + MatchConstants.SECONDARY_CD )
               doingSecondary = false;
      }
   
      public void updateStates(){
         if( vel_total.getY() < 0 )
            yMotion = VerticalMotion.DOWN;
         else if( vel_total.getY() == 0 )
            yMotion = VerticalMotion.NONE;
         else
            yMotion = VerticalMotion.UP;
      
         if( vel_total.getX() == 0 )
            xMotion = HorizontalMotion.NONE;
      } 
   
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   //ACTIONS
      public void moveRight(){
         if( vel_total.getX() >= vel_max_running )
            vel_total.setX( vel_max_running );
         else
            vel_total.deltaX( acc_running );	
         xMotion = HorizontalMotion.RIGHT;
         facing = Direction.FACING_RIGHT;
      }
   
      public void moveLeft(){
         if( vel_total.getX() <= -1*vel_max_running )
            vel_total.setX( -1*vel_max_running );
         else
            vel_total.deltaX( -1* acc_running );
         xMotion = HorizontalMotion.LEFT;
         facing = Direction.FACING_LEFT;
      }
   
      public void slide(){
         if( vel_total.getX() > 0 ){
            if( vel_total.getX() <= Math.abs(acc_friction) || (vel_total.getX() - acc_friction )<= 0)
               vel_total.setX( 0 );
            else
               vel_total.deltaX( -1*acc_friction );
         }
         else if( vel_total.getX() < 0 ){
            if( vel_total.getX() >= acc_friction )
               vel_total.setX( 0 );
            else
               vel_total.deltaX( acc_friction );
         }
      }
   
      public void stun( double damageDealt, boolean hardHit ){
         struckHard = hardHit;
         stunCD = true;
         jumpCD = doingPrimary = doingSecondary = false;
         stunT = System.nanoTime();
         activity = Activity.STUNNED;
         damagePercentage += damageDealt;
         totalDamagePercentage +=damageDealt;
      }
   
      public void jump(){
         jumpCD = true;
         jumpT = System.nanoTime();
         jumpNumber++;
         vel_total.setY( MatchConstants.REG_JUMP_VEL );
         yMotion = VerticalMotion.UP;
      }
   
      public void dive(){
         vel_total.setY( -1* MatchConstants.REG_JUMP_VEL );
         yMotion = VerticalMotion.DOWN;
         activity = Activity.DIVE;
      }
   
      public void crouch(){ // may need a stand method to reset Hitbox dimensions
         activity = Activity.CROUCH;
      }
   
      public void primaryAttack(){
      //Directions: up, down, left, right, or neutral
         doingPrimary = true;
         primaryT = System.nanoTime();
         activity = Activity.PRIMARY; 
      }
   
      public void secondaryAttack(){
      //Directions: up, down, left, right, or neutral
         doingSecondary = true;
         secondaryT = System.nanoTime();
         activity = Activity.SECONDARY;
      }
   
      public void block(){
      //Directions: left, right
         activity = Activity.BLOCK;
      }
   
   //HIGH SCOPE / STATE CHANGES
      public void die(){
         damagePercentage = 0.0;
         lifeStatus = LifeState.DEAD;
         livesLost++;
      }
   
      public void respawn(){
         lifeStatus = LifeState.RESPAWNING;
         activity = Activity.IDLE;
         xMotion = HorizontalMotion.NONE;
         yMotion = VerticalMotion.NONE;
      
         jumpCD = doingPrimary = doingSecondary = stunCD = false;
         jumpNumber = 0;
      
         pos_left_feet.set( startingPosition );
         vel_total.set( 0.0, 0.0 );
      }
   
   }
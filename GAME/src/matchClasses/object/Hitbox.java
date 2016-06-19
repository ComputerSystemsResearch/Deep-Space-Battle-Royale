   package GAME.src.matchClasses.object;
	
   import GAME.src.matchClasses.Vector2D;

   public class Hitbox {
      
      private Vector2D lowerLeft;
      private Vector2D upperRight;
   	   
      public Hitbox(){
         lowerLeft = new Vector2D( 0.0, 0.0 );
         upperRight = new Vector2D( 0.0, 0.0 );
      }
   	
      public Hitbox( Vector2D lowerLeft, Vector2D upperRight ){
         this.lowerLeft = lowerLeft;
         this.upperRight = upperRight;
      }
   	
      public Vector2D min(){
         return lowerLeft;
      }
   	
      public Vector2D max(){
         return upperRight;
      }
   	
      public void scaleCrouch( double scale ){
         upperRight.setY( upperRight.getY() * scale );
      }
   
   }
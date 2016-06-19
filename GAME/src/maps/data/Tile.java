   //package data;
   package GAME.src.maps.data;

   import java.awt.Image;
   import javax.swing.ImageIcon;

   public class Tile {
   
      private int x;
      private int y;
      private int width;
      private int height;
      private Image image;
      private TileType t;
   
      public Tile(int x, int y, int width, int height, TileType t)
      {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.image = new ImageIcon(getClass().getClassLoader().getResource(t.textureName)).getImage();
      }
   
      public int getX() {
         return x;
      }
   
      public void setX(int x) {
         this.x = x;
      }
   
      public int getY() {
         return y;
      }
   
      public void setY(int y) {
         this.y = y;
      }
   
      public int getWidth() {
         return width;
      }
   
      public void setWidth(int width) {
         this.width = width;
      }
   
      public int getHeight() {
         return height;
      }
   
      public void setHeight(int height) {
         this.height = height;
      }
   
      public Image getImage() {
         return image;
      }
   
      public void setImage(Image image) {
         this.image = image;
      }
   
      public TileType getT() {
         return t;
      }
   
      public void setT(TileType t) {
         this.t = t;
      }
   }

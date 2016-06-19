package GAME.src.state.gameplay; 

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class InputHandler implements KeyListener{

   private static final int KEY_COUNT = 256;
   private enum KeyState{ RELEASED, PRESSED, ONCE }
   private boolean[] currentKeys = null;
   private KeyState[] keys = null; 	

   public InputHandler(){
      currentKeys = new boolean[ KEY_COUNT ];
      keys = new KeyState[ KEY_COUNT ];
      for( int i=0; i<KEY_COUNT; i++ ){
         keys[ i ] = KeyState.RELEASED; 
      }
   }

   public synchronized void poll() {
      for( int i = 0; i < KEY_COUNT; ++i ) {
         if( currentKeys[ i ] ) {
            if( keys[ i ] == KeyState.RELEASED )
               keys[ i ] = KeyState.ONCE;
            else
               keys[ i ] = KeyState.PRESSED;
         } 
         else {
            keys[ i ] = KeyState.RELEASED;
         }
      }
   }

   public boolean keyDown( int keyCode ){
      return keys[keyCode] == KeyState.ONCE || keys[keyCode] == KeyState.PRESSED;
   }

   public boolean keyDownOnce( int keyCode ){
      return keys[keyCode] == KeyState.ONCE;
   }

   @Override 	
   public synchronized void keyPressed( KeyEvent e ){
      int keyCode = e.getKeyCode();
      if( keyCode >=0 && keyCode < KEY_COUNT )
         currentKeys[keyCode] = true;
   }

   @Override
   public synchronized void keyReleased( KeyEvent e ){
      int keyCode = e.getKeyCode();
      if( keyCode >=0 && keyCode < KEY_COUNT )
         currentKeys[keyCode] = false;
   }

   public synchronized void keyTyped( KeyEvent e ){}

}

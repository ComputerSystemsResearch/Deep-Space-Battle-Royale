# Deep-Space-Battle-Royale
McLean HS '16, Computer Systems Research, Period 6, Group "Numbah One"
The final submitted version of the game before presentation.


##### Description
Deep Space Battle Royale is a two player fighting game created (_attempted_) in the style of Super Smash Bros. 


##### Running the Game
Download the game by downloading the zip folder, saving, then unzipping the files.
To boot the game navigate to 
>_Deep-Space-Battle-Royale-master->GAME->launch->src->data->Boot.java_

and open the file.
To run you wil need a Java IDE such as jGrasp to run Boot.java.
Menus are navigated with the mouse, and the game is played on the keyboard (keyboard needs a numpad and arrow keys).


**Player One Controls**
 - W-A-S-D: jump, left, right, dive
 - O: Attack

**Player Two Controls**
 - ↑ ← → ↓: jump, left, right, dive 
 - NUMPAD_2: Attack


##### Some Known Bugs/ Issues
 - If certain player controls are held down while the match is starting the match will end immedeatley after it starts.
 - Match start countdown animation doesn't play correctly at match start (may even loop a few times)- looks like the game is about to crash (it's not).
 - Resizing the Gameplay window after match start freezes the gameplay window, which then must be aborted by clicking the X in the upper right corner.
 - The Boot thread sometimes continues to loop in the background behind the gameplay thread uninhibited, causing slowness.
 - Characters colors are difficult to discern on the Hellfire stage because of the high red saturation saturation in the Hellfire stage background image.

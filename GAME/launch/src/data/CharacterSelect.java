//package data;
	package GAME.launch.src.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class CharacterSelect {

	public static final int WIDTH = 900, HEIGHT = 450; //Default
	private Image selectBG;
	private Image back;
	private Image playGame;
	private UI charSel;;
	public static String playerOneChar;
	public static String playerTwoChar;
	private Font trb = new Font("Bazooka", Font.BOLD, 30);
	public CharacterSelect(){
		this.selectBG = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/CharacterSelectTemp.jpg")).getImage();
		this.back = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/back.png")).getImage();
		this.playGame = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/PlayGame.png")).getImage();
		this.charSel = new UI();
		charSel.addButton("Back", back, 830, 400, 60, 35);
		charSel.addButton("Play", playGame, 380, 400, 130, 35);
	}
	
	public void update(Graphics g)
	{
		g.drawImage(selectBG, 0, 0, WIDTH, HEIGHT, null);
		charSel.draw(g);
		g.setColor(Color.WHITE);
		g.setFont(trb);
		if(playerOneChar!=null)
			g.drawString(playerOneChar, 407, 170);
		if(playerTwoChar!=null)
			g.drawString(playerTwoChar, 407, 330);
	}

}

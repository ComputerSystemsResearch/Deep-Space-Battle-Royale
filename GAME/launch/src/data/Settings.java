//package data;
	package GAME.launch.src.data;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Settings {
	
	public static final int WIDTH = 900, HEIGHT = 450; //Default
	
	private Image settingsBG;
	private UI menuUI;
	private Image back;
	private Image check;
	
	public Settings()
	{
		this.settingsBG = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/settingsMenu.png")).getImage();
		this.back = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/back.png")).getImage();
		this.check = new ImageIcon(getClass().getClassLoader().getResource("GAME/launch/src/res/check.png")).getImage();
		MainMenu.music = new AudioPlayer("GAME/launch/src/res/menuMusic.wav");
		this.menuUI = new UI();
		menuUI.addButton("Back", back, 820, 15, 60, 35);
		if(MainMenu.musicOn==true)
			MainMenu.music.play(); 
	}

	private void updateButtons()
	{
		
	}
	
	public void update(Graphics g) 
	{
		g.drawImage(settingsBG, 0, 0, WIDTH, HEIGHT, null);
		menuUI.draw(g);
		if(MainMenu.musicOn == true)
			g.drawImage(check, 585, 147, 100, 100, null);
		if(MainMenu.sfxOn == true)
			g.drawImage(check, 585, 280, 100, 100, null);
	}

}

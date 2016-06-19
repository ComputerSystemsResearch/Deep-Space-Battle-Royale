//package data;
	package GAME.launch.src.data;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;

public class UI {

	private ArrayList<Button> buttonList;
	public static final int WIDTH = 900, HEIGHT = 450; //Default

	public UI()
	{
		buttonList = new ArrayList<Button>();
	}
	
	public void addButton(String name, Image image, int x, int y)
	{
		buttonList.add(new Button(name, image, x, y));
	}
	
	public void addButton(String name, Image image, int x, int y, int width, int height)
	{
		buttonList.add(new Button(name, image, x, y, width, height));
	}
	
	public boolean isButtonClicked(String buttonName)
	{
		Button b = getButton(buttonName);
		Point p = MouseInfo.getPointerInfo().getLocation();
		float mouseY = (float) (HEIGHT - p.getY()-1);
		if(p.getX() > b.getX() && p.getX() < b.getX()+b.getWidth() && mouseY > b.getY() && mouseY < b.getY()+b.getHeight())
			return true;
		return false;
	}
	
	private Button getButton(String buttonName)
	{
		for(Button b: buttonList)
		{
			if(b.getName().equals(buttonName))
				return b;
		}
		return null;
	}
	
	public void draw(Graphics g)
	{
		for(Button b: buttonList)
		{
			g.drawImage(b.getImage(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), null);

		}
			
	}
}

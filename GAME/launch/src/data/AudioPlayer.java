//package data;
	package GAME.launch.src.data;

import java.net.URL;

import javax.sound.sampled.*;

public class AudioPlayer {

	private Clip clip;
	
	public AudioPlayer(String urly)
	{
		try
		{
			URL url = this.getClass().getClassLoader().getResource(urly);		
			AudioInputStream aud = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(aud);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	public void play()
	{
		if(clip==null)
			return;
		stop();		
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void stop()
	{
		if(clip.isRunning())
			clip.stop();
	}
	
	public void close()
	{
		stop();
		clip.close();
	}
	
}

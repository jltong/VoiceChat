import java.io.Serializable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class audio implements Serializable{
	mainclass fa = null ;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	int bufferSize=16000;
	int yymode=0;
	
	audio(mainclass father){
		this.fa=father ;
	}
	
	public void setmode(int mode){
		this.yymode=mode;
	}
	
	void capture(String ipaddr) {
		try {
			Thread captureThread = new CaptureThread(ipaddr);
			captureThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	void play(byte[] context) {
		try {
			byte[] audioData=new byte[bufferSize];
			audioData=context;
			Thread playThread = new Thread(new PlayThread(audioData));
			playThread.start();
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 16000;//8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;//8,16
		int channels = 1;//1,2
		boolean signed = true;//true,false
		boolean bigEndian = false;//true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	}

	class PlayThread extends Thread implements Serializable{
	  byte[] audioData;
	  AudioFormat format;
	  
	  PlayThread(byte[] audioData){
		  this.audioData=audioData;
	  }
	  
	  	public void run() {
		  	format=getAudioFormat();
	        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
	        SourceDataLine line = null;
	        try {
	            line = (SourceDataLine) AudioSystem.getLine(info);
	            line.open(format,bufferSize);
	        } catch (LineUnavailableException ex) {
	        }
	        line.start();
	        line.write(audioData,0,audioData.length);
	        line.drain();
	    }
	 }
	    
	class CaptureThread extends Thread implements Serializable{
	   	byte[] audioData=new byte[bufferSize];
	   	String ipaddr;
	   	
	   	CaptureThread(String ipaddr){
	   		this.ipaddr=ipaddr;
	   	}
	   	
	   	public void run() {
	   		try {
	   			AudioFormat format=getAudioFormat();
	   			while (yymode==1){
	                TargetDataLine.Info info = new DataLine.Info(TargetDataLine.class, format, bufferSize);
	                TargetDataLine line=null;
	                try {
	                    line=(TargetDataLine)AudioSystem.getLine(info);
	                    line.open(format);
	                    line.start();
	                    
		                int intBytes=0;
		                while(intBytes!=-1) {
		                    intBytes=line.read(audioData,0,bufferSize);
		                    if (intBytes>=0) {
		                    	if (yymode==1) {
									SoftData data=new SoftData() ;
									data.sound=audioData;
									play(audioData);
									data.op=3;
									data.destip=ipaddr;
									data.srcip="0";
									fa.softsenddata(data) ;
								}
		                    }
		                }
		                line.close();
	                } catch (LineUnavailableException e1) {
	                    e1.printStackTrace();
	                }
	   			}
	   		} catch (Exception e) {
	   			e.printStackTrace();
	   		}
	   	}
	 }
}
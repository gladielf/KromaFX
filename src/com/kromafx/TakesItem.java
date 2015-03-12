package com.kromafx;

public class TakesItem {

	private int scene;
	private int take;
	private int track;
	
	public TakesItem(int scene,int take ,int track){
		this.scene = scene;
		this.take = take;
		this.track = track;
	}
	
	public int getScene(){
		return scene;
	}
	
	public void setScene(int scene){
		this.scene = scene;
	}
	
	public int getTake(){
		return take;
	}
	
	public void setTake(int take){
		this.take = take;
	}
	
	public int getTrack(){
		return track;
	}
	
	public void setTrack(int track){
		this.track = track;
	}
	
}

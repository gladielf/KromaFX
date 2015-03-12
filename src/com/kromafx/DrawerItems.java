package com.kromafx;

public class DrawerItems {

	private String itemName;
	private int itemImage;
	
	public DrawerItems(String itemname, int itemimage){
		this.itemName = itemname;
		this.itemImage = itemimage;
	}
	
	public String getItemName(){
		return itemName;
	}
	
	public void setItemName(String itemName){
		this.itemName = itemName;
	}
	
	public int getItemImage(){
		return itemImage;
	}
	
	public void setItemImage(int itemImage){
		this.itemImage = itemImage;
	}
}

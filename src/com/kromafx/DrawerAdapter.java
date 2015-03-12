package com.kromafx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<DrawerItems> {
	
	private Context context;
	private ArrayList<DrawerItems> drawerList;

	public DrawerAdapter(Context context, ArrayList<DrawerItems> drawerList) {
		super(context, R.layout.drawer_list_item, drawerList);
		this.context = context;
		this.drawerList = drawerList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.drawer_list_item, null);
		
		ImageView image = (ImageView) view.findViewById(R.id.drawer_list_image);
		TextView text = (TextView) view.findViewById(R.id.drawer_list_text);
		
		image.setImageResource(drawerList.get(position).getItemImage());
		
		text.setText(drawerList.get(position).getItemName());
		
		return view;
	}
	
}

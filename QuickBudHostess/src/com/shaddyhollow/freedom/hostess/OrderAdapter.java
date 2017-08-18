package com.shaddyhollow.freedom.hostess;

import java.util.ArrayList;

import com.shaddyhollow.quicktable.models.CartItem;
import com.shaddyhollow.quickbud.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter{
	
	private CartItem[] cartItems ;
	private LayoutInflater layoutInflater;
	
	public OrderAdapter(Context context, CartItem[] cartItems){
		this.cartItems = cartItems;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cartItems.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cartItems[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view = layoutInflater.inflate(R.layout.order_list_single, parent, false);
		
		TextView itemName = (TextView) view.findViewById(R.id.item_name);
		TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
		
		CartItem cartItem = cartItems[position];
		
		StringBuilder menuOptions = new StringBuilder();
		for (int i=0; i<cartItem.cart_item_options.length; i++){
			if(cartItem.cart_item_options[i].action.equalsIgnoreCase("A"))
				menuOptions.append("\n" + "  -" + cartItem.cart_item_options[i].menu_option.name);
		}
		
		itemName.setText(cartItem.name + menuOptions.toString());
		
		double totalItemPrice = cartItem.price;
		
		for(int i=0; i<cartItem.cart_item_options.length; i++){
			totalItemPrice += cartItem.cart_item_options[i].menu_option.price ;
		}
		
		itemPrice.setText("$"+String.valueOf(totalItemPrice));
		
		return view;
	}

}

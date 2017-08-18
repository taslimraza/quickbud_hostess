package com.shaddyhollow.freedom.hostess;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.models.Server;

public class ServerAdapter extends BaseDataAdapter<Server> {

	public ServerAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Server server = getItem(position);
		
		View view = layoutInflater.inflate(R.layout.simple_text_selected, null);
		TextView name = (TextView)view.findViewById(R.id.text);
		name.setText(server.getName());
		
		return view;
	}

	public void update(Server updatedServer) {
		Server server = getItemByID(updatedServer.id);
		if(server==null) {
			server = new Server();
			server.id = updatedServer.id;
			add(server);
		}
		
		server.name = updatedServer.name;
		server.max_party = updatedServer.max_party;
		server.min_party = updatedServer.min_party;
		server.setTables_served(updatedServer.getTables_served());
		server.colorstate = updatedServer.colorstate;

		ServerFactory.getInstance().createOrUpdate(server);
		return;
	}
}

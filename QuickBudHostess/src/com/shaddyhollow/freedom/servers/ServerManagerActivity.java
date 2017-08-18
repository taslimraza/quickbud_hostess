package com.shaddyhollow.freedom.servers;

import java.util.UUID;

import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.freedom.dinendashhostess.requests.AddServerRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.DeleteServerRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetServersRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.UpdateServerRequest;
import com.shaddyhollow.freedom.hostess.ServerAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.generic.listeditor.ItemListManagerActivity;
import com.shaddyhollow.quicktable.models.Identifiable;
import com.shaddyhollow.quicktable.models.ListServers;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseListener;

public class ServerManagerActivity extends ItemListManagerActivity<Server>  {

	public BaseDataAdapter<Server> initAdapter() {
		ServerAdapter adapter = new ServerAdapter(this);
		adapter.addAll(ServerFactory.getInstance().bulkRead(null));
		
		return adapter;
	}

	public String getItemType() {
		return "Server";
	}

	public void performItemDelete(Identifiable item) {
		Server server = (Server)item;
		
		DeleteServerRequest request = new DeleteServerRequest(Config.location.getTenantId(), server);
		try {
			request.execute(contentManager, new BaseListener<Void>() {
				@Override                           
				public void onRequestSuccess(Void arg0) {
					if(adapter.getCurrentSelectionPosition()!=-1) {
						adapter.remove(adapter.getCurrentSelectionPosition());
					}
					adapter.notifyDataSetChanged();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performItemUpdate(Identifiable item) {
		Server server = (Server)item;
		
		UpdateServerRequest request = new UpdateServerRequest(Config.getTenantID(), server);
		try {
			request.execute(contentManager, new BaseListener<Void>() {

				@Override
				public void onRequestSuccess(Void arg0) {
					Toast.makeText(ServerManagerActivity.this, "Server Re-Named!",Toast.LENGTH_SHORT).show();
					adapter.notifyDataSetChanged();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performItemCreate(String itemName) {
		Server server = new Server();
		server.id = UUID.randomUUID();
		server.name = itemName;
		adapter.add(server);

		AddServerRequest request = new AddServerRequest(Config.getTenantID(), Config.getLocationID() , server);
		try {
			request.execute(contentManager, new BaseListener<Server>() {

				@Override
				public void onRequestSuccess(Server server) {
					adapter.setSelectionByID(server.getId());
					adapter.notifyDataSetChanged();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

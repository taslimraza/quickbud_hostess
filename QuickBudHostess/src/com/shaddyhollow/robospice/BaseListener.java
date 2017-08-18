package com.shaddyhollow.robospice;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public abstract class BaseListener<T> implements RequestListener<T> {

	@Override
	public void onRequestFailure(SpiceException ex) {
		if(ex instanceof RequestCancelledException) {
			System.out.println("request cancelled");
			return;
		}
		onFailure(ex);
	}

	public void onFailure(SpiceException ex) {
	}
	
	@Override
	public abstract void onRequestSuccess(T obj);

}

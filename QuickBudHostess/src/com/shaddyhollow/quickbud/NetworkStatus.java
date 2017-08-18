package com.shaddyhollow.quickbud;


public class NetworkStatus {
	private int color;
	private String status;
	private long lastSuccessMS;
	private long lastErrorMS;

	private static final int COLOR_NETWORK_UP = 0xFF00FF00;
	private static final int COLOR_NETWORK_DOWN = 0xFFFF0000;
	private static final int COLOR_NETWORK_UNKNOWN = 0x0FFFFFF00;

	private enum STATE { UNKNOWN, UP, DOWN }; 
	
	private STATE previousState;
	private OnStatusChangedListener statusChangedListener = null;
	
	public NetworkStatus() {
		lastSuccessMS = System.currentTimeMillis() - Config.timeDiff;
		lastErrorMS = lastSuccessMS;
		previousState = STATE.UNKNOWN;
		setColor(COLOR_NETWORK_UNKNOWN);
	}
	
	public int getColor() {
		return color;
	}
	private void setColor(int color) {
		this.color = color;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public long getLastSuccessMS() {
		return lastSuccessMS;
	}
	
	public void setSuccess() {
		setColor(COLOR_NETWORK_UP);
		Config.setOnline(true);

		if(statusChangedListener!=null) {
			if(!previousState.equals(STATE.UP)) {
				statusChangedListener.toSuccess(lastSuccessMS - lastErrorMS);
			}
		}
		lastSuccessMS = System.currentTimeMillis() - Config.timeDiff;
		previousState = STATE.UP;
	}
	
	public long getLastErrorMS() {
		return lastErrorMS;
	}
	
	public void setError(Exception e) {
		setColor(COLOR_NETWORK_DOWN);
		Config.setOnline(false);

		if(statusChangedListener!=null) {
			if(!previousState.equals(STATE.DOWN)) {
				statusChangedListener.toError(lastErrorMS - lastSuccessMS);
			}
		}
		lastErrorMS = System.currentTimeMillis() - Config.timeDiff;
		previousState = STATE.DOWN;
	}
	
	public OnStatusChangedListener getStatusChangedListener() {
		return statusChangedListener;
	}
	public void setStatusChangedListener(OnStatusChangedListener statusChangedListener) {
		this.statusChangedListener = statusChangedListener;
	}

	public interface OnStatusChangedListener {
		public void toError(long uptime);
		public void toSuccess(long downtime);
	}
	
}

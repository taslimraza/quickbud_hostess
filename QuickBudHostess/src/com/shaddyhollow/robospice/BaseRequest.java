package com.shaddyhollow.robospice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.shaddyhollow.quickbud.Config;

public abstract class BaseRequest<T> extends SpringAndroidSpiceRequest<T> {
	T retVal = null;

	public BaseRequest(Class<T> resultType) {
		super(resultType);
		// disable retry
		this.setRetryPolicy(null);
		this.setPriority(PRIORITY_NORMAL);
	}

	public void execute(SpiceManager contentManager, RequestListener<T> listener) {
		if(isOfflineAvailable()) {
			retVal = loadOfflineData();
		}
		contentManager.execute(this, null, DurationInMillis.ALWAYS_EXPIRED, listener);
	}
	
	@Override
	public T loadDataFromNetwork() throws Exception {
		
		if(isCancelled()) {
			return retVal;
		}
		
		if(isOnlineAvailable()) {
			try {
				setApplicationHeaders();
				retVal = loadOnlineData();
			} catch (ResourceAccessException e) {
				// queue up online data request
				throw new SpiceException(e);
			} catch (Exception e) {
				if(!(e instanceof IllegalStateException)) {
					e.printStackTrace();
				}
			}

		}
		
		return retVal;
	}
	
	public void setApplicationHeaders() throws Exception {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new ApplicationHeaderInterceptor());
		getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory()); 
		getRestTemplate().setInterceptors(interceptors);
	}

	public boolean isOnlineAvailable() {
		return true;
	}
	
	public T loadOnlineData() throws Exception {
		return (T)null;
	}
	
	public boolean isOfflineAvailable() {
		return true;
	}
	
	public T loadOfflineData() {
		return (T)null;
	}
	
	public class ApplicationHeaderInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] data, ClientHttpRequestExecution execution) throws IOException {
//	        request.getHeaders().add("X-Application-Name", "QTHostess");
//	        request.getHeaders().add("X-Application-Version", Config.versionName);
//	        request.getHeaders().add("X-Application-Build",  Config.serverName);
//	        request.getHeaders().add("X-Application-Bundle-Identifier", Config.packageName);
			System.out.println("User Id: " + Config.getUserId());
			request.getHeaders().add("userid", Config.getUserId());
//	        request.getHeaders().add("Cookie", Config.getCsrfToken() + ";" + Config.getSessionKey());
	        return execution.execute(request, data);
		}
	}

}

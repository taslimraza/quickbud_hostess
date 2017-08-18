package com.shaddyhollow.robospice;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import roboguice.util.temp.Ln;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;

public class OfflineableGsonSpringAndroidSpiceService extends GsonSpringAndroidSpiceService {
	private static final int WEBSERVICES_TIMEOUT = 10000;

    @Override
    public CacheManager createCacheManager( Application application ) {
    	Ln.getConfig().setLoggingLevel(Log.ERROR);
        return new CacheManager();

//      cachemanager that doesn't do any caching
//    	return new CacheManager() {
//    		@Override
//    		public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
//
//    			return data;
//    		}
//    	};
    }

    /**
     * http://stackoverflow.com/questions/16707357/setting-connection-timeout-in-robospice-request-android
     */
    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();

        ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
        if (factory instanceof HttpComponentsClientHttpRequestFactory) {
            HttpComponentsClientHttpRequestFactory advancedFactory = (HttpComponentsClientHttpRequestFactory) factory;
            advancedFactory.setConnectTimeout(WEBSERVICES_TIMEOUT);
            advancedFactory.setReadTimeout(WEBSERVICES_TIMEOUT);
            restTemplate.setRequestFactory( advancedFactory );
        } else if (factory instanceof SimpleClientHttpRequestFactory) {
            SimpleClientHttpRequestFactory advancedFactory = (SimpleClientHttpRequestFactory) factory;
            advancedFactory.setConnectTimeout(WEBSERVICES_TIMEOUT);
            advancedFactory.setReadTimeout(WEBSERVICES_TIMEOUT);
            restTemplate.setRequestFactory( advancedFactory );
        }
        
        return restTemplate;
    }

    @Override
    protected NetworkStateChecker getNetworkStateChecker() {
    	// always assume that the network is available since we have offline requests that happen also
        return new NetworkStateChecker() {
            @Override
            public boolean isNetworkAvailable(Context context) {
            	return true;
            }

            @Override
            public void checkPermissions(Context context) {
            }
        };
    }
}

package com.example.mpcdemo.service;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.mpcdemo.domain.MPCAccount;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class CacheService {
	private static CacheService singleton = null;
	
	private RemoteCache<Integer, MPCAccount> accountsCache;
	private MPCAccount[] defaultAccounts;

	File file;

	public static  CacheService getInstance() {
		if ( null == singleton ) {
			singleton = new CacheService();
		}
		return singleton;
	}
	
	private CacheService() {
		setupCache();
		initializeCache();
	}
	
	
	
	private void setupCache() {
		String jdgServerHost = System.getenv("DATAGRID_APP_HOTROD_SERVICE_HOST");
		String jdgServerPort = System.getenv("DATAGRID_APP_HOTROD_SERVICE_PORT");
	
		// Catch and handle null values
		jdgServerHost = (jdgServerHost == null) ? "datagrid-app-hotrod" : jdgServerHost;
		jdgServerPort = (jdgServerPort == null) ? "11333" : jdgServerPort;
	
		//local JDG cache for testing
		//jdgServerHost = (jdgServerHost == null) ? "127.0.0.1" : jdgServerHost;
		//jdgServerPort = (jdgServerPort == null) ? "11222" : jdgServerPort;
	
		String serverEndpoint = jdgServerHost.concat(":").concat(jdgServerPort);
		Configuration configuration = new ConfigurationBuilder().addServers(serverEndpoint).build();
		RemoteCacheManager rcm = new RemoteCacheManager(configuration);
		accountsCache = rcm.getCache("default");
	}
	
	public MPCAccount[] getDefaultAccounts() {
		return this.defaultAccounts;
	}
	
	public MPCAccount[] getAccounts() {
		MPCAccount[] accounts;
		
		if (accountsCache.isEmpty()) { //load from accounts.json and update cache
			System.out.println("empty cache");
			initializeCache();
			accounts = this.defaultAccounts;
		}
		else { //load data from cache
			List<MPCAccount> accountsList = new ArrayList<MPCAccount>();
			accountsCache.entrySet().forEach(entry -> accountsList.add((MPCAccount)entry.getValue()));
			accountsCache.entrySet().forEach(entry -> System.out.printf("%s = %s\n", entry.getKey(), ((MPCAccount)entry.getValue()).getVenueName() + " " + ((MPCAccount)entry.getValue()).getRequestCount()));
			accounts = accountsList.toArray(new MPCAccount[accountsList.size()]);
		}
		return accounts;
		
	}
	
	public void initializeCache() {
		accountsCache.clear();
		defaultAccounts = loadDefaultAccountData();
		saveAccounts(defaultAccounts);
	}
	
	public MPCAccount[] loadDefaultAccountData() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);


			InputStream is = new ClassPathResource("accounts.json").getInputStream();
			
			MPCAccount[] accounts =  objectMapper.readValue(is, MPCAccount[].class);
			for (MPCAccount account: accounts) {
				System.out.println("account: " + account.getVenueName() + " :: " + account.getAccountId());
			}
			
			return accounts;
		}
		catch (Exception e) {
			e.printStackTrace();
			//TODO: load hard coded data 
			//RockTourHardCodeAndDBIO.readShowList
			return null;
			
		}
	}
	
	
	public void saveAccounts(MPCAccount[] accounts) {
		//adding the accounts to default cache
		if (accountsCache == null)
			setupCache();
		
		for(MPCAccount account: accounts) {
			accountsCache.put(account.getAccountId(), account);
		}
	}
	
	public void addUserInput(String accountId) {

		MPCAccount account = accountsCache.get(Integer.parseInt(accountId));
		if (account != null) {
			account.setRequestCount(account.getRequestCount()+1); //will update the revenueOpportunity
			accountsCache.put(Integer.parseInt(accountId), account);
		}
		else
		{
			//TODO
		}
	}
	
	/*public MPCAccount[] getSelectedAccounts() {
		accountsCache.entrySet().forEach(entry -> System.out.printf("%s = %s\n", entry.getKey(), ((MPCAccount)entry.getValue()).getVenueName() + " " + ((MPCAccount)entry.getValue()).getRequestCount()));
		ArrayList<MPCAccount> selectedAccountsList = new ArrayList<MPCAccount>();
		MPCAccount account;
		
	    Set<Integer> keySet = accountsCache.keySet();
	    Iterator<Integer> i = keySet.iterator();
	    System.out.println("============= selected accounts from cache");
	    while (i.hasNext()) {
	        Object key = i.next();
	        System.out.println("> key=" + key);
	        account = (MPCAccount)accountsCache.get(key);
	        selectedAccountsList.add(account);
	        System.out.println("> value=" + account.getVenueName());
	        System.out.println(" ");
	    }
	    MPCAccount[] selectedAccounts = selectedAccountsList.toArray(new MPCAccount[selectedAccountsList.size()]);
	    return selectedAccounts;
	}*/
	
	public void printCache() {
		System.out.println("printing cache entries");
		//TODO: handle entries that are not MPCAccount type 
		accountsCache.entrySet().forEach(entry -> System.out.printf("%s = %s\n", entry.getKey(), ((MPCAccount)entry.getValue()).getVenueName() + " " + ((MPCAccount)entry.getValue()).getRequestCount()));
		//MPCAccount account = accountsCache.get(101);
		//System.out.println(account.getVenueName() + account.getCity());
	}

	
}

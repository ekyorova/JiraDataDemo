package com.retriever.jira;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;

public final class ClientConnection {
	private static Client client = null;

	private ClientConnection() {
	}

	public synchronized static Client getInstance() {
		if (client == null) {
			synchronized (ClientConnection.class) {
				if (client == null) {
					client = ClientBuilder.newClient(new ClientConfig());
				}
			}
		}
		return client;
	}

}

package com.company;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class SSLServer extends Thread {

	private String destinationIP;
	private int destinationPort;
	private int listenPort;
	// Protocol 0 is for TCP, 1 is for UDP
	private int proto;
	private static String keyLocation;

	public SSLServer(String targetip, int targetport, int protocol, int listenport, String key) {

		destinationIP = targetip;
		destinationPort = targetport;
		proto = protocol;
		listenPort = listenport;
		keyLocation = key;
	}

	public void run() {

		ServerSocketFactory ssf = SSLServer.getServerSocketFactory("TLS");
		ServerSocket ss = null;
		try {
			ss = ssf.createServerSocket(listenPort);

			while(true) {
				Socket s = ss.accept();
				new ServerHelper(s, destinationIP, destinationPort, proto).run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static ServerSocketFactory getServerSocketFactory(String type) {
		if (type.equals("TLS")) {
		    SSLServerSocketFactory ssf = null;
		    try {
			SSLContext ctx;
		        KeyManagerFactory kmf;
		        KeyStore ks;
		        char[] passphrase = "importkey".toCharArray();

		        ctx = SSLContext.getInstance("TLS");
		        kmf = KeyManagerFactory.getInstance("SunX509");
		        ks = KeyStore.getInstance("JKS");

		        ks.load(new FileInputStream(keyLocation), passphrase);
		        kmf.init(ks, passphrase);
		        ctx.init(kmf.getKeyManagers(), null, null);

		        ssf = ctx.getServerSocketFactory();
		        return ssf;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} else {
		    return ServerSocketFactory.getDefault();
		}
		return null;
        }
}

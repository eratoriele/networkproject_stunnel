package com.company;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class SSLServer extends Thread {

	private int listenPort;

	public SSLServer(int port) {
		listenPort = port;
	}

	public void run() {

		ServerSocketFactory ssf = SSLServer.getServerSocketFactory("TLS");
		ServerSocket ss = null;
		try {
			ss = ssf.createServerSocket(listenPort);

			while(true) {
				Socket s = ss.accept();
				new ServerHelper(s).run();
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

		        ks.load(new FileInputStream("keystore.ImportKey"), passphrase);
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

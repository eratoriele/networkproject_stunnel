package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerHelper extends Thread {

	private String destinationIP;
	private int destinationPort;
	private Socket s;
	private int proto;
	
	public ServerHelper(Socket sock, String targetip, int targetport, int protocol) {
		s = sock;
		destinationIP = targetip;
		destinationPort = targetport;
		proto = protocol;
	}
	
	@Override
	public void run() {

		try {
			byte[] b = null;
			
			DataInputStream din = new DataInputStream(s.getInputStream());
			
			int len = din.readInt();
			b = new byte[len];
			din.readFully(b, 0, b.length);
			
			System.out.println("From " + s.getInetAddress() + ":" + s.getPort() + " => " + new String(b));

			switch(proto) {
				case 0:
					//TCP
					Socket ts = new Socket(destinationIP, destinationPort);
					DataOutputStream dOS = new DataOutputStream(ts.getOutputStream());
					dOS.write(b);
					dOS.flush();
					dOS.close();
					ts.close();
					break;
				case 1:
					//UDP
					DatagramPacket datapack = new DatagramPacket(b, b.length, InetAddress.getByName(destinationIP), destinationPort);
					DatagramSocket ds = new DatagramSocket();
					ds.send(datapack);
					ds.close();
					break;
				default:
					System.exit(-1);
			}

			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

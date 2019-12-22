package com.company;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

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
            if (SystemTray.isSupported())
			    TrayIconSettings.increaseactivity();

			byte[] b = null;
			DataInputStream din = new DataInputStream(s.getInputStream());

			// Loop and listen to the client until client closes the socket
			while (true) {
				b = null;
				int len = din.readInt();
				b = new byte[len];
				din.readFully(b, 0, b.length);

				System.out.println("From " + s.getInetAddress() + ":" + s.getPort() + " => " + new String(b));

				// send the data to the listener in localhost
				switch (proto) {
					case 0:
						//TCP
						Socket ts = new Socket(destinationIP, destinationPort);
						DataOutputStream dOS = new DataOutputStream(ts.getOutputStream());
						// send the length first
                        dOS.writeInt(len);
                        dOS.flush();
                        // then data itself
						dOS.write(b);
						dOS.flush();

						dOS.close();
						ts.close();
						break;
					case 1:
						//UDP
						byte[] datalen = null;
                        datalen = ByteBuffer.allocate(4).putInt(len).array();
                        DatagramPacket datapacklen = new DatagramPacket(datalen, datalen.length, InetAddress.getByName(destinationIP), destinationPort);
                        DatagramPacket datapack = new DatagramPacket(b, b.length, InetAddress.getByName(destinationIP), destinationPort);
						DatagramSocket ds = new DatagramSocket();

						ds.send(datapacklen);
						ds.send(datapack);
						ds.close();
						break;
					default:
						System.exit(-1);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

        if (SystemTray.isSupported())
            TrayIconSettings.decreaseactivity();
	}
	
}

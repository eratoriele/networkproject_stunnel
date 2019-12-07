package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerHelper extends Thread {

	Socket s;
	
	public ServerHelper(Socket sock) {
		s = sock;
	}
	
	@Override
	public void run() {
		
		DataOutputStream dOS;
		try {
			dOS = new DataOutputStream(s.getOutputStream());

			byte[] b = null;
			
			DataInputStream din = new DataInputStream(s.getInputStream());
			
			int len = din.readInt();
			b = new byte[len];
			din.readFully(b, 0, b.length);
			
			System.out.println("From " + s.getInetAddress() + ":" + s.getPort() + " sent: " + new String(b));

			dOS.writeInt(b.length);
			dOS.flush();
			dOS.write(b);
			dOS.flush();


		
			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

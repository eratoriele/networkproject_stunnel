package com.company;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

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

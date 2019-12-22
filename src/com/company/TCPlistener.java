package com.company;

import java.io.*;
import java.net.*;

class TCPServer {
 public static void main(String argv[]) throws Exception {
  String clientSentence;
  String capitalizedSentence;
  ServerSocket welcomeSocket = new ServerSocket(9999);

  while (true) {
	Socket connectionSocket = welcomeSocket.accept();
        DataInputStream dIN = new DataInputStream(connectionSocket.getInputStream());
	byte[] data = null;
	int len = dIN.readInt();
        data = new byte[len];
        dIN.readFully(data);
	connectionSocket.close();
	System.out.println("Recieved: " + new String(data));
	
  }


 }


}

package com.company;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

class UDPServer
{
   public static void main(String args[]) throws Exception
      {
         DatagramSocket serverSocket = new DatagramSocket(9999);
            byte[] receiveData = null;
            byte[] receiveDatalen = new byte[4];
            while(true)
               {
                  DatagramPacket receivePacketlen = new DatagramPacket(receiveDatalen, receiveDatalen.length);
                  serverSocket.receive(receivePacketlen);
		  receiveData = new byte[ByteBuffer.wrap(receivePacketlen.getData()).getInt()];
		  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		  serverSocket.receive(receivePacket);
                  String sentence = new String( receivePacket.getData());
                  System.out.println("RECEIVED: " + sentence);
               }
      }
}

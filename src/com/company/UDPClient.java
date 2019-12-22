package com.company;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

class UDPClient {
    public static void main(String args[]) throws Exception
    {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData;
        sendData = "t".getBytes();
        byte[] sendDatalen = ByteBuffer.allocate(4).putInt(sendData.length).array();
        // First is empty
        DatagramPacket sendPacketlen = new DatagramPacket(sendDatalen, sendDatalen.length, IPAddress, 3500);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 3500);
        clientSocket.send(sendPacketlen);
        clientSocket.send(sendPacket);
    }
}
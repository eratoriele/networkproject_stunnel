package com.company;

import java.io.*;
import java.net.*;

public class UDPclient {

    public static void main(String args[]) throws Exception {

        System.out.println("Client");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        //clientSocket.setBroadcast(true);
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        //InetAddress IPAddress = InetAddress.getByName("255.255.255.255");
        byte[] sendData;
        byte[] receiveData = new byte[100];


        String sentence = "hm";

        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
        clientSocket.send(sendPacket);
        //DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        //clientSocket.receive(receivePacket);
        //String modifiedSentence = new String(receivePacket.getData());
       // System.out.println("FROM SERVER:" + modifiedSentence);

    }
}
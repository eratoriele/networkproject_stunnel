package com.company;

import java.net.*;

public class UDPServer {


    public static void main(String[] args) {

        try {
            DatagramSocket serverSocket = new DatagramSocket(9999);

            byte[] receiveData = new byte[100];

            while (true) {

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                System.out.println("RECEIVED: " + new String(receivePacket.getData()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
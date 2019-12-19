package com.company;

import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        String sentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 3500);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        sentence = "aaaa";
        outToServer.writeInt(sentence.length());
        outToServer.write(sentence.getBytes());
        clientSocket.close();
    }
}
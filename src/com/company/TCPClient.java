package com.company;

import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        String sentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 3500);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        sentence = "a";
        //while (true) {
        for (int i = 0; i < 25; i++) {
            sentence = sentence.concat("a");
            outToServer.writeInt(sentence.getBytes().length);
            outToServer.write(sentence.getBytes());
            Thread.sleep(250);
        }
        //}
        //clientSocket.close();
    }
}
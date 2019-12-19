package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        // The config file
        JSONObject jo = (JSONObject) new JSONParser().parse(new FileReader("config.json"));

        new TrayIconSettings(jo).run();

//        new SSLServer().run();

        String client = jo.get("Client").toString();
        String destinationIP = jo.get("DestinationIP").toString();
        int destinationPort = Integer.parseInt(jo.get("DestinationPort").toString());
        int listenPort = Integer.parseInt(jo.get("ListenPort").toString());
        // 0 for TCP, 1 for UDP
        int proto = jo.get("Proto").toString().equals("TCP") ? 0 : 1;
        String key = jo.get("Key").toString();

        // If running a client
        if (client.equals("Yes"))
            new SSLSocketClient(destinationIP, destinationPort, proto, listenPort, key).run();
        else
            new SSLServer(destinationIP, destinationPort, proto, listenPort, key).run();

    }
}

package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        // The config file
        JSONObject jo = (JSONObject) new JSONParser().parse(new FileReader("config.json"));
        // Create the system tray icon
        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            Image image = null;
            try {
                image = ImageIO.read(new File("off_image.png"));
            } catch (IOException e) {
                System.err.println(e);
            }
            // create a action listener to listen for default action executed on the tray icon
            ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e)  {
                    // if the about button is used
                    if (e.getActionCommand().equals("Configure")) {
                        ConfigMenu cm = new ConfigMenu(jo);
                        cm.setVisible(true);
                    }
                    else if (e.getActionCommand().equals("About"))
                        JOptionPane.showMessageDialog(null, "Bora Gülerman\n20160702015");
                    else if (e.getActionCommand().equals("Exit"))
                        System.exit(0);
                }
            };
            // create a popup menu
            PopupMenu popup = new PopupMenu();
            // create menu item for the default action
            MenuItem configMenuItem = new MenuItem("Configure");
            configMenuItem.addActionListener(listener);
            MenuItem aboutMenuItem = new MenuItem("About");
            aboutMenuItem.addActionListener(listener);
            MenuItem exitMenuItem = new MenuItem("Exit");
            exitMenuItem.addActionListener(listener);
            popup.add(configMenuItem);
            popup.add(aboutMenuItem);
            popup.add(exitMenuItem);
            /// ... add other items
            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "Tray Demo", popup);
            // set the TrayIcon properties
            //trayIcon.addActionListener(listener);
            // ...
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
            // ...
        }


//        new SSLServer().run();

//       new SSLSocketClient().run();


    }
}

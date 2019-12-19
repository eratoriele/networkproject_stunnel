package com.company;

import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class TrayIconSettings extends Thread {

    private JSONObject jo;
    private static int activity = 0;
    private static Image image_off = null;
    private static Image image_on = null;
    private static TrayIcon trayIcon;

    public TrayIconSettings(JSONObject jo) {
        this.jo = jo;
    }

    // If a new connection is opened, use this function to change the image
    public static void increaseactivity() {
        activity++;
        if (activity > 0) {
            trayIcon.setImage(image_on);
        }
    }

    // If a connection is closed, use this to change the image back to off_image
    public static void decreaseactivity() {
        activity--;
        if (activity < 1) {
            trayIcon.setImage(image_off);
        }
    }

    @Override
    public void run() {
        // Create the system tray icon
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            try {
                image_off = ImageIO.read(new File("off_image.png"));
                image_on = ImageIO.read(new File("on_image.png"));
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

            // At the start image is off
            trayIcon = new TrayIcon(image_off, "Tray Demo", popup);
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        }
    }
}

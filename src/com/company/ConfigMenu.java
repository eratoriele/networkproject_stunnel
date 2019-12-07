package com.company;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigMenu extends JFrame {
    private JPanel panel1;
    private JButton saveButton;
    private JList list1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JList list2;
    private JTextField textField4;

    public ConfigMenu(JSONObject jo) {

        super("Config Menu");
        add(panel1);
        setSize(500,300);

        // Load the default values
        if (jo.get("Client").equals("Yes"))
            list1.setSelectedIndex(0);
        else
            list1.setSelectedIndex(1);
        textField1.setText(jo.get("ListenPort").toString());
        textField2.setText(jo.get("DestinationIP").toString());
        textField3.setText(jo.get("DestinationPort").toString());
        if (jo.get("Proto").equals("TCP"))
            list2.setSelectedIndex(0);
        else
            list2.setSelectedIndex(1);
        textField4.setText(jo.get("Key").toString());

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    jo.replace("Client", jo.get("Client"), list1.getSelectedValue());
                    jo.replace("ListenPort", jo.get("ListenPort"), Integer.parseInt(textField1.getText()));
                    jo.replace("DestinationIP", jo.get("DestinationIP"), textField2.getText());
                    jo.replace("DestinationPort", jo.get("DestinationPort"), Integer.parseInt(textField3.getText()));
                    jo.replace("Proto", jo.get("Proto"), list2.getSelectedValue());
                    jo.replace("Key", jo.get("Key"), textField4.getText());


                    FileWriter fw = new FileWriter("config.json");

                    fw.write(jo.toJSONString());
                    fw.flush();

                }
                catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Something went wrong, config file did not update.");
                }
                finally {
                    dispose();
                }
            }
        });
    }
}

package com.company;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.xml.crypto.Data;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.util.Properties;

/*
 * This example demostrates how to use a SSLSocket as client to
 * send a HTTP request and get response from an HTTPS server.
 * It assumes that the client is not behind a firewall
 */

public class SSLSocketClient extends Thread {

    private static String destinationIP;
    private static int destinationPort;
    // Protocol 0 is for TCP, 1 is for UDP
    private int proto;
    private int listenPort;
    private static String keyLocation;

    public SSLSocketClient(String targetip, int targetport, int Protocol, int listenport, String key) {

        destinationIP = targetip;
        destinationPort = targetport;
        proto = Protocol;
        listenPort = listenport;
        keyLocation = key;
    }

    @Override
    public void run() {
        // Data received, and to be sent
        switch (proto) {
            case 0:
                // TCP
                ServerSocket tcpsocket = null;
                try {
                    tcpsocket = new ServerSocket(listenPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while(true) {
                    Socket sck = null;
                    try {
                        sck = tcpsocket.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    new tcpserverthread(sck, destinationIP, destinationPort, keyLocation).run();
                }
                //break;
            case 1:
                // UDP
                DatagramSocket udpsocket = null;
                try {
                    udpsocket = new DatagramSocket(listenPort);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

               /* while (true) {

                    byte[] empty = new byte[1];
                    DatagramPacket emptyPacket = new DatagramPacket(empty, 1);
                    try {
                        udpsocket.receive(emptyPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    new udpserverthread(udpsocket, destinationIP, destinationPort, keyLocation).run();
                //}

                break;
            default:
                System.exit(-1);
        }

    }

	public static SSLSocketFactory getSSLSocketFactory(String type) {
		if (type.equals("TLS")) {
		    SocketFactory ssf = null;
		    try {
			SSLContext ctx;
		        KeyManagerFactory kmf;
		        KeyStore ks;
		        char[] passphrase = "importkey".toCharArray();

		        ctx = SSLContext.getInstance("TLS");
		        kmf = KeyManagerFactory.getInstance("SunX509");
		        ks = KeyStore.getInstance("JKS");

		        ks.load(new FileInputStream(keyLocation), passphrase);
		        kmf.init(ks, passphrase);

			ctx.init(kmf.getKeyManagers(), null, null);

		        ssf = ctx.getSocketFactory();
		        return (SSLSocketFactory) ssf;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} else {
		    return (SSLSocketFactory) SSLSocketFactory.getDefault();
		}
		return null;
        }
}

class tcpserverthread extends Thread {

    private Socket sck;
    private byte[] data;
    private String destinationIP;
    private int destinationPort;
    private String keyLocation;

    public tcpserverthread (Socket sck, String destinationIP, int destinationPort, String keyLocation) {
        this.sck = sck;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.keyLocation = keyLocation;
    }

    @Override
    public void run() {
        SSLSocket socket = null;
        DataOutputStream dos = null;

        Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.trustStore", keyLocation);
        try {
            DataInputStream dIN = new DataInputStream(sck.getInputStream());
            SSLSocketFactory factory = SSLSocketClient.getSSLSocketFactory("TLS");
            socket = (SSLSocket)factory.createSocket(destinationIP, destinationPort);

            /*
             * send http request
             *
             * Before any application data is sent or received, the
             * SSL socket will do SSL handshaking first to set up
             * the security attributes.
             *
             * SSL handshaking can be initiated by either flushing data
             * down the pipe, or by starting the handshaking by hand.
             *
             * Handshaking is started manually in this example because
             * PrintWriter catches all IOExceptions (including
             * SSLExceptions), sets an internal error flag, and then
             * returns without rethrowing the exception.
             *
             * Unfortunately, this means any error messages are lost,
             * which caused lots of confusion for others using this
             * code.  The only way to tell there was an error is to call
             * PrintWriter.checkError().
             */
            socket.startHandshake();

            dos = new DataOutputStream(socket.getOutputStream());

            while (!sck.isClosed()) {
                // Learn how big the data is to avoid zero padding
                int len = dIN.readInt();
                data = new byte[len];
                dIN.readFully(data);

                // Signify to the trayicon that the connection is opened, so it can change the color
                if (SystemTray.isSupported())
                    TrayIconSettings.increaseactivity();

                // Send the received data until application is dropped, or an exception occurs
                dos.writeInt(data.length);
                dos.flush();
                dos.write(data);
                dos.flush();

                // Signify to the trayicon that the connection is closed, so it can change the color
                if (SystemTray.isSupported())
                    TrayIconSettings.decreaseactivity();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dos.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class udpserverthread extends Thread {

    private DatagramSocket udpsocket;
    private byte[] data;
    private String destinationIP;
    private int destinationPort;
    private String keyLocation;

    public udpserverthread (DatagramSocket udpsocket, String destinationIP, int destinationPort, String keyLocation) {
        this.udpsocket = udpsocket;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.keyLocation = keyLocation;
    }

    @Override
    public void run() {
        SSLSocket socket = null;
        DataOutputStream dos = null;

        Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.trustStore", keyLocation);
        try {
            SSLSocketFactory factory = SSLSocketClient.getSSLSocketFactory("TLS");
            socket = (SSLSocket)factory.createSocket(destinationIP, destinationPort);

            /*
             * send http request
             *
             * Before any application data is sent or received, the
             * SSL socket will do SSL handshaking first to set up
             * the security attributes.
             *
             * SSL handshaking can be initiated by either flushing data
             * down the pipe, or by starting the handshaking by hand.
             *
             * Handshaking is started manually in this example because
             * PrintWriter catches all IOExceptions (including
             * SSLExceptions), sets an internal error flag, and then
             * returns without rethrowing the exception.
             *
             * Unfortunately, this means any error messages are lost,
             * which caused lots of confusion for others using this
             * code.  The only way to tell there was an error is to call
             * PrintWriter.checkError().
             */
            socket.startHandshake();

            dos = new DataOutputStream(socket.getOutputStream());

            byte[] datalen = new byte[4];

            while (true) {
                DatagramPacket dataPacketlen = new DatagramPacket(datalen, 4);
                udpsocket.receive(dataPacketlen);
                data = new byte[ByteBuffer.wrap(dataPacketlen.getData()).getInt()];
                DatagramPacket dataPacket = new DatagramPacket(data, data.length);
                udpsocket.receive(dataPacket);

                // Signify to the trayicon that the connection is opened, so it can change the color
                if (SystemTray.isSupported())
                    TrayIconSettings.increaseactivity();

                // Send the recieved data until application is dropped, or an exception occurs
                dos.writeInt(data.length);
                dos.flush();
                dos.write(data);
                dos.flush();
                Thread.sleep(100);

                // Signify to the trayicon that the connection is closed, so it can change the color
                if (SystemTray.isSupported())
                    TrayIconSettings.decreaseactivity();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dos.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
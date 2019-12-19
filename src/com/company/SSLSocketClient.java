package com.company;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
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
        byte[] data = new byte[0];
        switch (proto) {
            case 0:
                // TCP
                ServerSocket tcpsocket = tcpServer();

                while(true) {
                    Socket sck = null;
                    try {
                        sck = tcpsocket.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    new tcpserverthread(sck, data).run();
                }
                //break;
            case 1:
                // UDP
                DatagramSocket udpsocket = udpServer();

                while (true) {

                    byte[] lenpack = new byte[4];

                    // Learn how big the data is to avoid zero padding
                    DatagramPacket lenOfPacket = new DatagramPacket(lenpack, lenpack.length);
                    try {
                        udpsocket.receive(lenOfPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int len = ByteBuffer.wrap(lenOfPacket.getData()).getInt();

                    new udpserverthread(udpsocket, data, len).run();

                }
                //break;
            default:
                System.exit(-1);
        }

    }

    private ServerSocket tcpServer() {
        try {
            return new ServerSocket(listenPort);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private DatagramSocket udpServer() {
        try {

            return new DatagramSocket(listenPort);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendData(byte[] data) {
        Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.trustStore", "keystore.ImportKey");
        try {
            SSLSocketFactory factory = getSSLSocketFactory("TLS");
            SSLSocket socket = (SSLSocket)factory.createSocket(destinationIP, destinationPort);

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

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(data.length);
            dos.flush();
            dos.write(data);
            dos.flush();

            dos.close();
            socket.close();

            // Signify to the trayicon that the connection is closed, so it can change the color
            TrayIconSettings.decreaseactivity();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private static SSLSocketFactory getSSLSocketFactory(String type) {
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

    public tcpserverthread (Socket sck, byte[] data) {
        this.sck = sck;
        this.data = data;
    }

    @Override
    public void run() {
        DataInputStream dIN = null;
        try {
            // Signify to the trayicon that the connection is opened, so it can change the color
            TrayIconSettings.increaseactivity();

            dIN = new DataInputStream(sck.getInputStream());

            // Learn how big the data is to avoid zero padding
            int len = dIN.readInt();
            data = new byte[len];
            dIN.readFully(data);

            SSLSocketClient.sendData(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class udpserverthread extends Thread {

    private DatagramSocket udpsocket;
    private byte[] data;
    private int lenOfPacket;

    public udpserverthread (DatagramSocket udpsocket, byte[] data, int lenOfPacket) {
        this.udpsocket = udpsocket;
        this.data = data;
        this.lenOfPacket = lenOfPacket;
    }

    @Override
    public void run() {
        // Signify to the trayicon that the connection is opened, so it can change the color
        TrayIconSettings.increaseactivity();

        data = new byte[lenOfPacket];
        DatagramPacket partialDataPacket = new DatagramPacket(data, data.length);
        try {
            udpsocket.receive(partialDataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SSLSocketClient.sendData(partialDataPacket.getData());
    }
}
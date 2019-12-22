package com.company;

import java.io.IOException;

public class keyCreator {

    public static void createKeys() {

        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","openssl genrsa -out key.pem 2048"});

            int exitVal = p.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            } else {
                //abnormal...
            }
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","openssl req -new -x509 -key key.pem -out cert.pem -days 365 -subj /C=GB/ST=London/L=London/O=aaa/OU=aaa/CN=aaa"});

            exitVal = p.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            } else {
                //abnormal...
            }
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","openssl pkcs8 -topk8 -nocrypt -in key.pm -inform PEM -out key.der -outform DER"});

            exitVal = p.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            } else {
                //abnormal...
            }
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","openssl x509 -in cert.pem -inform PEM -out cert.der -outform DER"});

            exitVal = p.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            } else {
                //abnormal...
            }
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","java ImportKey key.der cert.der"});

            exitVal = p.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            } else {
                //abnormal...
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

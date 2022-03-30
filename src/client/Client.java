package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9178);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            //DataInputStream in = new DataInputStream(socket.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            String message;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            Object object = ois.readObject();
                            //System.out.println(object.getClass());
                            System.out.println((String) object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            while (true) {
                message = scanner.nextLine();
                out.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

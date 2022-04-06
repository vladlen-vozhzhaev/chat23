package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    static ArrayList<String> usersName = new ArrayList<>();
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
                            if(object.getClass().equals(usersName.getClass())){
                                usersName = (ArrayList<String>) object;
                                System.out.println(usersName);
                            }else{
                                System.out.println((String) object);
                            }
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

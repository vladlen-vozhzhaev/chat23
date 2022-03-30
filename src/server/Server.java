package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<User> users = new ArrayList<>();
    public static void main(String[] args) {
        try {
            System.out.println("Сервер запущен");
            ServerSocket serverSocket = new ServerSocket(9178);
            while (true){
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
                User currentUser = new User(socket);
                users.add(currentUser);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentUser.getOos().writeObject("SERVER: Введите имя");
                            String userName = currentUser.getIn().readUTF();
                            currentUser.setUserName(userName);
                            currentUser.getOos().writeObject(userName+ " добро пожаловать в чат!");
                            broadCastMessage("Пользователь "+userName+" подключился");
                            String response;
                            while (true){
                                response = currentUser.getIn().readUTF();
                                if (response.equals("/onlineUsers")){
                                    currentUser.getOos().writeObject(users);
                                }else{
                                    String message = currentUser.getUserName()+": "+response;
                                    broadCastMessage(message);
                                    System.out.println(response);
                                }

                            }
                        } catch (IOException e) {
                            users.remove(currentUser);
                            String message = currentUser.getUserName()+" отключился";
                            System.out.println(message);
                            broadCastMessage(message);
                        }
                    }
                });
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void broadCastMessage(String message){
        for (User user : users) {
            try {
                user.getOos().writeObject(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

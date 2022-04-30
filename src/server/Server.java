package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class Server {
    static ArrayList<User> users = new ArrayList<>();
    static String db_url = "jdbc:mysql://vladle43.beget.tech/vladle43_android";
    static String db_login = "vladle43_android";
    static String db_pass = "%Li30LRo";
    public static void main(String[] args) {
        try {
            System.out.println("Сервер запущен");
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            ServerSocket serverSocket = new ServerSocket(9178);
            while (true){
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
                User currentUser = new User(socket); // Создаём объект текущего подключившегося пользователя
                users.add(currentUser); // Добавляем его в коллекцию
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String header = currentUser.getIn().readUTF();
                            String target = header.split("//")[0];
                            System.out.println(target);
                            if(target.equals("registration")){
                                String[] headers = header.split("//");
                                String name = headers[1];
                                String phone = headers[2];
                                String pass = headers[3];
                                String uuid = headers[4];
                                Connection connection = DriverManager.getConnection(db_url, db_login, db_pass);
                                Statement statement = connection.createStatement();
                                ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE `phone`='"+phone+"'");
                                if (resultSet.next()) {
                                    currentUser.getOos().writeObject("user_exist");
                                }else{
                                    connection.close();
                                    connection = DriverManager.getConnection(db_url, db_login, db_pass);
                                    statement = connection.createStatement();
                                    String sql = "INSERT INTO `users`(`name`, `phone`, `pass`, `uuid`) VALUES ('"+name+"', '"+phone+"','"+pass+"', '"+uuid+"')";
                                    statement.executeUpdate(sql);
                                    connection.close();
                                    currentUser.getOos().writeObject("success");
                                }
                                users.remove(currentUser);
                                return;
                            }else if(target.equals("auth")){
                                String[] headers = header.split("//");
                                String phone = headers[1];
                                String pass = headers[2];
                                Connection connection = DriverManager.getConnection(db_url, db_login, db_pass);
                                Statement statement = connection.createStatement();
                                ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE `phone`='"+phone+"' AND `pass`='"+pass+"'");
                                if(resultSet.next()){
                                    currentUser.getOos().writeObject("success");
                                }else{
                                    currentUser.getOos().writeObject("error");
                                }
                            }

                            ArrayList<String> usersName = new ArrayList<>();
                            for (User user: users) {
                                if(user.getUserName() == null) continue;
                                usersName.add(user.getUserName());
                            }
                            currentUser.getOos().writeObject(usersName);

                            currentUser.getOos().writeObject("SERVER: Введите имя");
                            String userName = currentUser.getIn().readUTF();
                            currentUser.setUserName(userName);
                            currentUser.getOos().writeObject(userName+ " добро пожаловать в чат!");
                            broadCastMessage("Пользователь "+userName+" подключился");
                            String response;
                            while (true){
                                response = currentUser.getIn().readUTF();
                                /* /tell Igor Hello */
                                String[] responseArr = response.split(" ");
                                if (response.equals("/onlineUsers")){
                                    usersName = new ArrayList<>();
                                    for (User user: users) {
                                        usersName.add(user.getUserName());
                                    }
                                    currentUser.getOos().writeObject(usersName);
                                }else if(responseArr[0].equals("/tell")){
                                    String toUser = responseArr[1];
                                    StringBuilder text = new StringBuilder();
                                    for (int i = 2; i < responseArr.length; i++) {
                                        text.append(responseArr[i]).append(" ");
                                    }
                                    sendPrivateMessage(text.toString(), toUser);
                                }
                                else{
                                    String message = currentUser.getUserName()+": "+response;
                                    broadCastMessage(message, currentUser.getUuid());
                                    System.out.println(response);
                                }

                            }
                        } catch (Exception e) {
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
    public static void broadCastMessage(String message, UUID currentUserUUID){
        for (User user : users) {
            try {
                if(user.getUuid().toString().equals(currentUserUUID.toString())) continue;
                user.getOos().writeObject(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
    public static void sendPrivateMessage(String message, String toUser){
        for(User user:users){
            try {
                if(user.getUserName().equals(toUser)){
                    user.getOos().writeObject(message);
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}

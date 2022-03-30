package server;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class User implements Serializable{
    private String userName;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectOutputStream oos;
    private UUID uuid;

    public User(Socket socket){
        this.socket = socket;
        this.uuid = UUID.randomUUID();
        try{
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public String getUserName() {
        return userName;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public UUID getUuid() {
        return uuid;
    }
}

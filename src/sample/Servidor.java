package sample;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class Servidor  extends Observable implements Runnable{

    ServerSocket serverSocket;

    @Override
    public void run(){
        while (!serverSocket.isClosed()){
            try {
                Socket socket = serverSocket.accept();
                this.setChanged();
                this.notifyObservers(socket);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Servidor(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
}

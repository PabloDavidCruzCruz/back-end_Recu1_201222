package sample;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.Observable;

public class HiloCliente extends Observable implements Runnable {

    private Socket socket;
    private DataInputStream dataInputStream;

    public HiloCliente(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            String [] datosRecibidos;
            do {
                //System.out.println("Queloso");
                datosRecibidos =  dataInputStream.readUTF().split("\\.");
                this.setChanged();
                this.notifyObservers(datosRecibidos);
                System.out.println(datosRecibidos.length);
            }while (!socket.isClosed());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

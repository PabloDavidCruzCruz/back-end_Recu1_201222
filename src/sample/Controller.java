package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Controller implements Observer {

    ServerSocket serverSocket = null;
    private final int puerto = 3001;
    Socket socket = null;
    DataOutputStream datosSalida;

    @FXML
    private Pane paneInicio;

    @FXML
    private TextField textUser;

    @FXML
    private Button btnIniciar;

    @FXML
    private TextField textName;

    @FXML
    private TextField textLastName;

    @FXML
    private TextField textCountry;

    @FXML
    private TextField textFruits;

    @FXML
    private TextField textAnimal;

    @FXML
    private TextField textThing;

    @FXML
    private Button btnTerminado;

    @FXML
    private ListView<String> listView;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnOut;

    @FXML
    private Label labelLetra;

    @FXML
    private Label labelEspera;

    @FXML
    private Pane idPaneGanadores;

    @FXML
    private Label labelPerde;

    @FXML
    private Label labelGana;

    String nombreUsuario = " ";
    String letra = " ";
    int puntos = 0;

    String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    TextField[] arregloText = {textName, textLastName, textFruits, textThing, textAnimal, textCountry};
    String [] respuestaJugador;
    String respuesta;


    @FXML
    public void datosInsertados() {
        respuesta = textName.getText() + "."+ textLastName.getText() +"."+ textCountry.getText() +"." +textAnimal.getText() +"."+ textFruits.getText() +"."+ textThing.getText();
        enviarDatos();
        //compararDatos();
        for (int i = 0; i < arregloText.length; i++) {
            if (!arregloText[i].getText().isEmpty()) {
                if (letra.equals(arregloText[i].getText().substring(0, 1)) || letra.toLowerCase().equals(arregloText[i].getText().substring(0, 1))) {
                    System.out.println(arregloText[i].getText() + " es igual");
                } else {
                    System.out.println(arregloText[i].getText() + " no es igual");
                }
            } else {
                System.out.println("textFile esta vacio");
            }
        }
    }

    public void enviarDatos(){
        try {
            datosSalida.writeUTF(respuesta);
            datosSalida.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void compararDatos() {
        int jugador1 = 0;
        int jugador2 = 0;
        //System.out.println("Tamaño arreglo: "+ respuestaJugador.length);

        for (int i = 0; i < arregloText.length; i++) {
            if (!respuestaJugador [i].equals(" ")){
                System.out.println(respuestaJugador[i]+"Respuesta");
            }
            if (!arregloText[i].getText().isEmpty() && (!respuestaJugador[i].isEmpty() && !respuestaJugador[i].equals(" "))){
                if (arregloText[i].getText().equals(respuestaJugador[i])) {
                    jugador1=jugador1+50;
                    jugador2=jugador2+50;
                }else{
                    jugador1=jugador1+100;
                    jugador2=jugador2+100;
                }

            }else if (arregloText[i].getText().isEmpty() && !respuestaJugador[i].isEmpty() || !respuestaJugador[i].equals(" ")){
                jugador2 = jugador2 + 100;
            } else if(!arregloText[i].getText().isEmpty() && respuestaJugador[i].isEmpty() || !arregloText[i].getText().equals(" ")) {
                jugador1 = jugador1 + 100;
            }
        }
        System.out.println("Puntos jugador1: " + jugador1);
        System.out.println("Puntos jugador2: " + jugador2);
        if (jugador2 > jugador1){
            labelGana.setText("El jugador gano");
            labelPerde.setText("El servidor perdio");
        }else if (jugador2 < jugador1){
            labelGana.setText("El servidor gano");
            labelPerde.setText("El jugador perdio");
        }else{
            labelGana.setText("Ha sido un empate");
        }
        idPaneGanadores.setVisible(true);
        listaMostrada();

    }

    public void listaMostrada() {
        String idk = "RESPUESTAS DEL JUGADOR NUMERO 1: \n";
        String listBasta = "Nombre: " + respuestaJugador[0] + "\nApellido: " + respuestaJugador[1] + "\nCiudad o Pais: " + respuestaJugador[2] + "\nAnimal: " + respuestaJugador[3] + "\nFlor o Fruto: " + respuestaJugador[4] + "\nCosa: " + respuestaJugador[5];
        listView.getItems().add(idk);
        listView.getItems().add(listBasta);
    }

    @FXML
    public void btnSalir() {
        System.exit(0);
    }

    @FXML
    private void initialize() {
        arregloText[0] = textName;
        arregloText[1] = textLastName;
        arregloText[2] = textCountry;
        arregloText[3] = textAnimal;
        arregloText[4] = textFruits;
        arregloText[5] = textThing;



        try {
            serverSocket = new ServerSocket(puerto,100);
            Servidor servidor = new Servidor(serverSocket);
            servidor.addObserver(this);
            new Thread(servidor).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg){
        if (o instanceof Servidor){
            Socket socket2 = (Socket) arg;
            socket = socket2;
            try {
                datosSalida = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            HiloCliente hiloCliente = new HiloCliente(socket2);
            hiloCliente.addObserver(this);
            new Thread(hiloCliente).start();
            paneInicio.setVisible(false);
        }
        if (o instanceof HiloCliente){
            String [] arreglo = (String[]) arg;
            respuestaJugador = arreglo;
            if (!respuestaJugador[0].equals("1")){
                respuesta = textName.getText() +" "+ "."+ textLastName.getText() +" "+"."+ textCountry.getText() +" "+"." +textAnimal.getText()+" "+"."+ textFruits.getText()+" "+"."+ textThing.getText()+" ";
                enviarDatos();
                compararDatos();
            }else{
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btnJugar(respuestaJugador[1]);
                    }
                });
            }
        }
    }

    public void btnJugar(String let){
        //letra = "A";
        textName.setEditable(true);
        textLastName.setEditable(true);
        textAnimal.setEditable(true);
        textCountry.setEditable(true);
        textFruits.setEditable(true);
        textThing.setEditable(true);
        btnTerminado.setDisable(false);
        labelLetra.setText(let);
        labelEspera.setVisible(false);
        nombreUsuario = nombreUsuario.substring(0,1);
    }
}

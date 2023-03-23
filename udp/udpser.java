//import java.io.ByteArrayInputStream
//import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class udpser {
    public static void main(String[] args) {
        
        int puerto = 0;
        int acul = 0;
        boolean bucle = true;
        
        if(args.length!=1){
            System.out.println("Introduce los parametros con el formato : udpser port_number");
        }
        else{

            puerto = Integer.parseInt(args[0]);

            DatagramSocket socket = null;

            try{
                socket = new DatagramSocket(puerto);
            } catch(SocketException e){
                System.out.println("No se ha podido crear el socket, introduce un puerto libre");
                System.exit((-1));
            }

            while(bucle){
                try{
                    byte buffer[] = new byte [256];

                    DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
                    socket.receive(paquete);

                    int puerto_cliente = paquete.getPort();
                    InetAddress ip_cliente = paquete.getAddress();

                    //ByteArrayInputStream BufferEntrada = new ByteArrayInputStream(buffer);
                    //DataInputStream entrada = new DataInputStream(BufferEntrada);
                   
                    String stringNumeros = new String(paquete.getData()); //Leemos el string recibido por el cliente
                    String splitNumeros[] = stringNumeros.split(" "); //Separamos los números y la guardamos en una array de caracteres
                    
                    int arrayINT[] = new int [splitNumeros.length];

                    for(int i = 0; i<splitNumeros.length; i++){ //Guardamos los numeros en una array de enteros y los sumamos actualizando el valor del acumulador
                        arrayINT[i]=Integer.parseInt(splitNumeros[i].trim());
                        acul = acul + arrayINT[i];
                        System.out.println("Valor del acumulador: "+acul);

                    }
                    
                    //Enviamos respuesta
                    ByteArrayOutputStream bufferSalida = new ByteArrayOutputStream();
                    DataOutputStream salida = new DataOutputStream(bufferSalida);

                    salida.writeInt(acul);
                    salida.close();

                    paquete = new DatagramPacket(bufferSalida.toByteArray(),4,ip_cliente,puerto_cliente);
                    socket.send(paquete);

                } catch(IOException e){
                    System.out.println("Error");
                    System.exit(-1);
                }
            }
        }
    }
}
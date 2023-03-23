import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;


public class udpcli  {
    public static void main(String[] args) {

        Scanner lectura=new Scanner(System.in);
        
        InetAddress direccion_server = null;
        
        int puerto = 0;
        int resultado = 0;


        //Pedimos los numeros por teclado y los enviamos al servidor
        
        if(args.length!=2){
            System.out.println("Introduce los parametros con el formato : udpcli ip_address port_number");
        }
        else{

            try{
                direccion_server = InetAddress.getByName(args[0]);
            } catch(UnknownHostException e){
                System.out.println("Introduce una ip correcta");
                System.exit(-1);
            }
            puerto = Integer.parseInt(args[1]);
        
            //Datos por teclado
            
            System.out.println("Introduce una fila de numeros separados por un espacios : 1 54 1 21... Se leeran los numeros hasta fin de linea o encontrar un 0.");
            String filaNumeros = lectura.nextLine();
            filaNumeros = filaNumeros.trim(); // Limpiamos los espacios después del último dígito
            if(filaNumeros.indexOf('0')==0)
                {
                    System.exit(-1);
                }
            else if(filaNumeros.indexOf(" 0")!=-1){
                filaNumeros = filaNumeros.substring(0, filaNumeros.indexOf(" 0"));   
            }

            try{
                /*ENVIO*/
                
                DatagramSocket socket = new DatagramSocket();
                
                ByteArrayOutputStream bufferSalida= new ByteArrayOutputStream(); //Creamos un buffer para escribir y enviar
                
                DataOutputStream salida = new DataOutputStream(bufferSalida); 

                byte[] sendData = new byte[filaNumeros.length()]; 

                salida.writeChars(filaNumeros);
                salida.close(); 
                sendData = filaNumeros.getBytes();

                DatagramPacket paquete = new DatagramPacket(sendData,sendData.length,direccion_server,puerto); //Creamos el paquete para enviar al servidor
                
                socket.send(paquete); //Enviamos el paquete

                /*Respuesta del servidor*/

                byte buffer[] = new byte [4]; //Creamos una varibale para almacenar el mensaje respuesta del servidor

                paquete = new DatagramPacket(buffer,buffer.length); //Creamos un paquete donde guardaremos el datagrama respuesta del servidor

                //Limitamos el tiempo de espera de respuesta por parte del servidor
                socket.setSoTimeout(10000);
                try{
                    socket.receive(paquete); //Recibimos el paquete
                }
                catch(SocketTimeoutException e){
                    System.out.println("No ha habido respuesta por parte del servidor tras 10 segundos de espera.");
                    System.exit(-1);
                }                   
                ByteArrayInputStream bufferEntrada = new ByteArrayInputStream(buffer); 
                DataInputStream entrada = new DataInputStream(bufferEntrada);

                resultado = entrada.readInt();  //Leemos el entero recibido por el servidor

                socket.close(); 

            } catch(IOException e){
                System.out.println("Error al conectarse con servidor");
                System.exit(-1);
            
            }
                       
            System.out.println("Valor del acumulador = "+resultado); //Mostramos el valor final del acumulador
        
            lectura.close();
        }
    }
}
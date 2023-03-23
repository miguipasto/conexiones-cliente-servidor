import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class tcp2cli {

    public static void main(String[] args) {
        
        Scanner lectura = new Scanner(System.in);

        InetAddress ipserver = null;
        int puerto = 0;
        int salir = 0;
        int resultado = 0;

        if(args.length!=2){
            System.out.println("Introduce los parametros con el formato : tcp2cli ip_address port_numer");
        }
        else{

            try{
                ipserver = InetAddress.getByName(args[0]);
                puerto = Integer.parseInt(args[1]);
                          
                Socket socketCliente = new Socket();
                SocketAddress serverAdress = new InetSocketAddress(ipserver,puerto);

                try{
                    socketCliente.connect(serverAdress,15000);  
                }
                catch(SocketTimeoutException e){
                    System.out.println("No se ha podido establecer una conexion con el servidor tras 15 segundos");
                    System.exit(-1);
                }

                //Datos por teclado
                while(salir!=1){
                    System.out.println("Introduce una fila de numeros separados por un espacios : 1 54 1 21... Se leeran los numeros hasta fin de linea o encontrar un 0.");
                    String filaNumeros = lectura.nextLine();
                    filaNumeros = filaNumeros.trim(); // Limpiamos los espacios después del último dígito
                    if(filaNumeros.indexOf('0')==0){
                        salir = 1;
                        break;
                    } else if(filaNumeros.indexOf(" 0")!=-1){
                        filaNumeros = filaNumeros.substring(0, filaNumeros.indexOf(" 0"));   
                    }        
                    //Envio y recepción de datos
                    try{   
                        //Envio de datos al servidor
                        OutputStream bufferSalida = socketCliente.getOutputStream();
                        DataOutputStream salida = new DataOutputStream(bufferSalida); 

                        salida.writeUTF(filaNumeros);

                        //Respuesta del servidor
                        InputStream bufferEntrada = socketCliente.getInputStream();
                        DataInputStream entrada = new DataInputStream(bufferEntrada);

                        try{
                            resultado = entrada.readInt();  //Leemos el entero recibido por el servidor
                        }
                        catch(Exception e){  
                            System.out.println("Problemas en la respuesta del servidor");
                            //Generaria una excepcion en el caso en el que lo recibido no sea un numero entero
                        }

                    }catch(IOException e){
                        System.out.println("Error al enviar datos al servidor");
                        System.exit(-1);
                    }

                    System.out.println("Valor del acumulador = "+resultado); //Mostramos el valor final del acumulador
                    
                }
                //Cerramos la lectura por teclado y el socket del cliente 
                lectura.close();
                socketCliente.close();
            }
            catch(IOException e){
                System.out.println("Error en la conexion con el servidor");
                System.exit(-1);
            }
            
            
        }
    }
}
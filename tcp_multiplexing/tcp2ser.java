import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class tcp2ser {

    public tcp2ser (int puerto){ //Configuración del servidor
        boolean bucle = true;
        Socket socketCliente = null;
        ServerSocket socketServer = null;

        try{
            socketServer = new ServerSocket(puerto);        
            System.out.println("Servidor ON");
        }catch(IOException e){
            System.out.println("Error al crear el socket del servidor");
            //Saltaría en el caso de usar un puerto ocupado
        }
        while(bucle){

            try{
                socketCliente = socketServer.accept();
                ClientThread nuevoCliente = new ClientThread(socketCliente);
                nuevoCliente.start();
                
            }
            catch(IOException e){
                System.out.println("Error al acpetar un nuevo cliente");
            }
        }
        try{
            socketServer.close();
        } catch (IOException e) { 
            System.out.println("Error al cerrar el socket del servidor");
        }
    }
    public static void main(String[] args) {
        int puerto = 0;
        if(args.length!=1){
            System.out.println("Introduce los parametros con el formato : tcp2ser port_number");
        }
        else{
            
            puerto = Integer.parseInt(args[0]);
            new tcp2ser(puerto); // Inicializamos el servidor
        }
    }

    class ClientThread extends Thread { 
        int acul = 0;
        Socket socketCliente;     
        ClientThread(Socket nuevoCliente) { 
            socketCliente = nuevoCliente;
            System.out.println("Nuevo cliente");
        }
         
        public void run(){
            try{
                while(socketCliente.isConnected()){
                    
                    //Recibimos la fila con los numeros
                    InputStream bufferEntrada = socketCliente.getInputStream();
                    DataInputStream entrada = new DataInputStream(bufferEntrada);

                    String stringNumeros = new String(entrada.readUTF());
                    String splitNumeros[] = stringNumeros.split(" "); //Separamos los números y la guardamos en una array de caracteres
                        
                    int arrayINT[] = new int [splitNumeros.length];

                    for(int i = 0; i<splitNumeros.length; i++){ //Guardamos los numeros en una array de enteros y los sumamos actualizando el valor del acumulador
                        arrayINT[i]=Integer.parseInt(splitNumeros[i].trim());
                        acul = acul + arrayINT[i];
                        System.out.println("Valor del acumulador: "+acul);
                    }

                    //Enviamos la respuesta
                    OutputStream bufferSalida = socketCliente.getOutputStream();
                    DataOutputStream salida = new DataOutputStream(bufferSalida); 

                    salida.writeInt(acul);
                }
            }
            catch(Exception e){
                System.out.println("Un cliente se ha desconectado");
            }
            finally{
                try{
                    socketCliente.close();
                }
                catch(IOException e){
                    System.out.println("Error cerrando el socket del cliente");
                }
            }
        }
    }
}
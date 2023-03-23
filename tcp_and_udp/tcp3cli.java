import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class tcp3cli {
	public static void main(String[] args) throws IOException {

		Scanner reader = new Scanner(System.in);
		InetAddress ipserver = null;
			
		int entero = 0;
		int puerto = 0;
		
		if(args.length!=2 && args.length!=3){
            System.out.println("Introduce los parametros con el formato : tcp3cli ip_address port_numer [-u]");
        }
		else{
            try{
                ipserver = InetAddress.getByName(args[0]);
                puerto = Integer.parseInt(args[1]);

				ByteBuffer buffer = ByteBuffer.allocate(4);
				
				if(args.length==3){

					System.out.println("Envio a traves de UDP");
					
					DatagramChannel canalUDP = DatagramChannel.open();	
					
					while(true){		

						//Enviamos el numero al servidor
						entero = getEntero();

						buffer.clear();
						buffer.putInt(entero);
						buffer.flip();

						canalUDP.send(buffer, new InetSocketAddress(ipserver,puerto));
						
						//Recibimos la respuesta del servidor
						buffer.clear();
						canalUDP.receive(buffer);
						buffer.flip();

						int acumuladorUDP = buffer.getInt();
						System.out.println("Valor del acumulador UDP: "+acumuladorUDP);
						
					}

				}
				else{//TCP

					System.out.println("Envio a traves de TCP");

					SocketChannel canalTCP=SocketChannel.open();
					canalTCP.connect(new InetSocketAddress(ipserver, puerto));
					
					while(true){
								
						//Enviamos el numero al servidor
						entero = getEntero();
						
						buffer.clear();
						buffer.putInt(entero);
						buffer.flip();

						canalTCP.write(buffer);	

						//Respuesta del servidor
						buffer.clear();
						canalTCP.read(buffer);
						buffer.flip();

						int acumulador = buffer.getInt();
						System.out.println("Valor del acumulador = "+acumulador);
		
					}
				}
			} catch(IOException e){
				//System.out.println(e);
			}
		}
	}

	public static int getEntero(){
		Scanner lectura = new Scanner(System.in);
		//Leer numeros por teclado
		try{
			System.out.println("Introduce numeros enteros. El 0 signfica finalizar el programa: ");
			int entero = lectura.nextInt();
			if(entero==0){
				System.out.println("Desconexion del cliente");
				lectura.close();
				System.exit(0);
			}
			return entero;
		}
		catch(Exception e){
			lectura.close();
			System.exit(0);
		}
		return 0;
	}
}

			
import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class tcp3ser {
 
	public static void main(String[] args) throws IOException {

		int puerto =0;
		if(args.length!=1){
            System.out.println("Introduce los parametros con el formato : tcp3ser port_number");
			System.exit(-1);
		}
        else{
            puerto = Integer.parseInt(args[0]);
		}
	
		Selector selector=Selector.open();
		Map<SocketChannel, Integer> clientesTCP = new HashMap<>();

		//TCP
		ServerSocketChannel socketServerCanal=ServerSocketChannel.open();
		ServerSocket serverSocket=socketServerCanal.socket();
		socketServerCanal.configureBlocking(false);
		socketServerCanal.register(selector, SelectionKey.OP_ACCEPT);

		//UDP
		DatagramChannel channelUdp = DatagramChannel.open();
		channelUdp.configureBlocking(false);
		SelectionKey keyUDP = channelUdp.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE);
		int acumuladorUDP = 0;

		try{ //Bindeamos el puerto por el que recibiremos los numeros
			serverSocket.bind(new InetSocketAddress(puerto)); 
			channelUdp.socket().bind(new InetSocketAddress(puerto));
		} catch(Exception e){
			System.out.println("Eror al bindear los puertos.");
			System.exit(-1);
		}
		 
		System.out.println("Server ON");
		
		while(true){
			selector.select();
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> i = selectedKeys.iterator();
			
			while(i.hasNext()){
				SelectionKey nextKey = i.next();

				if (!nextKey.isValid()) {
                    continue;
                }
				if(nextKey.isAcceptable()){

					ServerSocketChannel server=(ServerSocketChannel) nextKey.channel();
					SocketChannel socketCanal=server.accept();
					socketCanal.configureBlocking(false);

					Socket socket = socketCanal.socket();
					SocketAddress remoteAddr = socket.getRemoteSocketAddress();
					System.out.println("Nuevo Cliente TCP");

					socketCanal.register(selector, SelectionKey.OP_READ);
					
					clientesTCP.put(socketCanal, 0);
					
				} else if(nextKey.isReadable()){

					ByteBuffer buffer = ByteBuffer.allocate(4);

					SocketChannel canalTCP = null;
					DatagramChannel canalUDP = null;

					int udp = 1;
					try{
						//UDP
						canalUDP = (DatagramChannel) keyUDP.channel();

						buffer.clear();
						SocketAddress direccionClienteUDP = canalUDP.receive(buffer); //Recibimos el buffer y guardamos su direccion
					
						//Guardamos el numero entero recibido por el cliente UDP
						buffer.flip();
						int enteroUDP = buffer.getInt();
						acumuladorUDP = acumuladorUDP+enteroUDP;
						System.out.println("Valor del acumulador UDP: "+acumuladorUDP);

						//Enviamos el valor del acumulador al cliente UDP
						buffer.clear();
						buffer.putInt(acumuladorUDP);
						buffer.flip();
						channelUdp.send(buffer, direccionClienteUDP);

					}catch(Exception e){
						//System.out.println(e);
						udp = 0;
					}
						
					if(udp==0){ //TCP
						try{	
							canalTCP = (SocketChannel) nextKey.channel();	

							//Recibimos el buffer TCP
							buffer.clear();	
							int numRead = canalTCP.read(buffer); 

							//Guardamos el numero entero recibido por el cliente TCP y los guardamos en el hashmap
							buffer.flip();
							int enteroTCP = buffer.getInt();
							int acumuladorTCP = clientesTCP.get(canalTCP)+ enteroTCP;
							clientesTCP.put(canalTCP,acumuladorTCP);
							System.out.println("Valor del acumulador cliente TCP: "+acumuladorTCP);

							//Enviamos el valor del acumulador TCP al cliente
							buffer.clear();
							buffer.putInt(acumuladorTCP);
							buffer.flip();
							canalTCP.write(buffer);

						} catch(Exception e){
							//Desconexion del cliente
							System.out.println("Un cliente TCP se ha desconcetado");
							
							clientesTCP.remove(canalTCP); //Lo eliminamos del mapa de clientes TCP
							canalTCP.close(); //Cerramos el canal 
							nextKey.cancel(); //Cancelamos la key asociada			
						}			
					}	
				} 
				i.remove();
			}	
		}
	}
}

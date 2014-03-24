import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		byte[] nullcharArray = "\0".getBytes();
		System.out.println("nullcharArray.length: "+nullcharArray.length);
		System.out.println((char)nullcharArray[0]);
		//System.out.println((char)nullcharArray[1]);
		
		/*int a = 2500;
		
		ByteBuffer b = ByteBuffer.allocate(2);
		b.putInt(a);
		byte[] result = b.array();
		
		System.out.println("length: " + result.length);
		
		System.out.println(result[0] + " " +result[1] );
		
		ByteBuffer c = ByteBuffer.wrap(result);
		a = c.getInt();
		
		System.out.println(a);*/
		
		/*byte[] ipAddress = new byte[4];
		ipAddress[0] = (byte)192;
		ipAddress[1] = (byte)168;
		ipAddress[2] = (byte)0;
		ipAddress[3] = (byte)14;
		
		String ipAddressString = new String(ipAddress);
		System.out.println(ipAddress + "\t" + ipAddressString);*/
		
		
		/*try {
			InetAddress DnsAddress = InetAddress.getByName("google-public-dns-a.google.com");
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.connect(DnsAddress, 53);	
			InetAddress hostAddress = clientSocket.getLocalAddress();
			
			byte[] result = hostAddress.getAddress();
			System.out.println(result.length);
			
			System.out.println(result[0] + " " +result[1] + " " + result[2] + " " +result[3]);
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
	}

}

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpellaServer extends Thread
{
	static int tcpPort;
	static int httpPort;
	ServerSocket serverSocket;
	
	static InetAddress hostAddress;
	static String hostName;
	String serverIP;

	public SimpellaServer(int tcpPort, int httpPort) throws IOException
	{
		SimpellaServer.tcpPort = tcpPort;
		SimpellaServer.httpPort = httpPort;
		serverSocket = new ServerSocket(tcpPort);
		
		InetAddress DnsAddress = InetAddress.getByName("google-public-dns-a.google.com");
		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.connect(DnsAddress, 53);	
		hostAddress = clientSocket.getLocalAddress();
		hostName = hostAddress.getHostName();
		String[] hostAddressArr = hostAddress.toString().split("/");
		serverIP = hostAddressArr[1];
		hostName = hostAddressArr[0];
		clientSocket.close();
		
		
		
		start();
	}//constructor ends
	
	
	
	public void run()
	{
		while(true)
		{
			try
			{
				Socket welcomeSocket = serverSocket.accept();
				new SimpellaServerClient(welcomeSocket);
			}
			catch(IOException e)
			{
				//SimpellaVer1.print(e.getMessage());
			}
			catch(Exception e)
			{
				//SimpellaVer1.print(e.getMessage());
			}
		}
	}// run ends
	
	public void info()
	{
		String str = "Local IP: " + serverIP + "\n";
		str = str + "Host Name: " + hostName + "\n";
		str = str + "Simpella Net Port: " + tcpPort + "\n";
		str = str + "Downloading Port: " + httpPort + "\n";
		str = str + "Simpella version 0.6 (c) 2012-2013 Chirag Todarka, Amit Kulkarni" + "\n";
		System.out.print(str);
	}

}//class ends

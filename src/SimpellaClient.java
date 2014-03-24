import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class SimpellaClient extends Thread
{
	static ArrayList<SimpellaClient> simpellaClientServerList = new ArrayList<SimpellaClient>();
	
	Socket simpellaClientSocket;
	String remoteSocketAddressString;
	DataOutputStream outToServer;
	BufferedReader inFromServer ;
	
	//info details
	public int msgReceived;
	public int msgSent;
	public int bytesReceived;
	public int bytesSent;
	public int msgHeader = 23;
	public int queryCount;
	public int responseCount;


	public SimpellaClient(String hostString, int port) 
	{
		try
		{
			System.out.println("\nConnection request sent...waiting for response");
			simpellaClientSocket = new Socket();
			
			InetSocketAddress inetSocketAddress = new InetSocketAddress(hostString, port);
			
			simpellaClientSocket.connect(inetSocketAddress, 3000);
			remoteSocketAddressString = simpellaClientSocket.getRemoteSocketAddress().toString();
			//System.out.println("simpellaClientSocket.getInetAddress(): "+simpellaClientSocket.getInetAddress());
			//System.out.println("simpellaClientSocket.getPort(): "+simpellaClientSocket.getPort());
			//outToServer = new DataOutputStream(simpellaClientSocket.getOutputStream());
			//outToServer.writeBytes("SIMPELLA CONNECT/0.6\r\n");
			
			byte[] simpellaConnect = new byte[4096];
			String str = "SIMPELLA CONNECT/0.6\r\n";
			byte[] tempSimpellaConnect = str.getBytes();
			
			for(int i =0; i<tempSimpellaConnect.length; i++)
				simpellaConnect[i] = tempSimpellaConnect[i];
			
			simpellaClientSocket.getOutputStream().write(simpellaConnect);
			
			
			//inFromServer = new BufferedReader(new InputStreamReader(simpellaClientSocket.getInputStream()));
			//String responseFromServer = inFromServer.readLine();
			//byte[] byteResponseFromServer = new byte[4096];
			String responseFromServer= "";
			InputStream byteInputStream = simpellaClientSocket.getInputStream();
			
			
			byte[] byteResponseFromServer = new byte[4096];
			byteInputStream.read(byteResponseFromServer);
			for(int i=0; i<byteResponseFromServer.length; i++)
			{
				char ch = (char)byteResponseFromServer[i];				
				//responseFromServer = responseFromServer + (char)byteResponseFromServer[i];
				responseFromServer = responseFromServer + ch;
				//Break the loop at new line
				if (ch=='\n')
					break;
			}
			
			/*int i =0;
			char ch;
			while((ch =(char)byteResponseFromServer[i])!='\n')
			{
				responseFromServer = responseFromServer + ch;
			}*/
			
			//System.out.println(responseFromServer);
			System.out.print("Response from Node: "+responseFromServer);
			if (responseFromServer.startsWith("SIMPELLA/0.6 200 "))// && responseFromServer.endsWith("\r\n"))
			{
				//System.out.println("in if block");
				//System.out.println("Server Response: "+responseFromServer);
				//outToServer.writeBytes("SIMPELLA/0.6 200 Thank you for accepting me\r\n");
				
				str = "SIMPELLA/0.6 200 Thank you for accepting me\r\n";
				byte[] thankyou = new byte[4096];
				byte[] tempThankyou = str.getBytes();
				
				for(int i=0; i<tempThankyou.length; i++)
					thankyou[i] = tempThankyou[i];
				
				simpellaClientSocket.getOutputStream().write(thankyou);
				//Added by Amit
				msgReceived = 0;
				msgSent = 0;
				bytesReceived =0;
				bytesSent = 0;
				queryCount = responseCount = 0;
				simpellaClientServerList.add(this);
				System.out.println("Connection to Node established successfully!!!\n");
				start();
			}
			else if (responseFromServer.startsWith("SIMPELLA/0.6 503 "))// && responseFromServer.endsWith("\r\n"))
			{
				//System.out.println("Server Response: "+responseFromServer);
			}
			else
			{
				System.out.println("Unexpected response from server");
			}
		}
		catch (SocketTimeoutException e)
		{
			System.out.println("Unable to connect to server...connection time out");
		}
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		
	}// constructor ends

	
	public void run()
	{
		while(true)
		{
			try
			{
				byte[] message = new byte[4096];
				//System.out.println("waiting...");
				int length = simpellaClientSocket.getInputStream().read(message);
				//for(int i=0; (i<length ); i++)
					//System.out.println("pingMessage["+i+"]=" +message[i]);
				//Modified by Amit
				// Update msgReceived var for this client - ArrayList requires external synchronization
				incrementMsgReceived();
				//Get message payload
				//increment byte received
				incrementBytesReceived(SimpellaVer1.getMessagePayloadLenght(message));
				
				if(message[17]>15)
				{
					//ignore the messages having TTL>15
				}
				//for(int i=0; i<length; i++)
					//System.out.println("pingMessage["+i+"]=" +message[i]);
				else
				{
					if((message[17]+message[18])>7)
					{
						if(message[17]<message[18])
						{
							message[18] = (byte)(7-message[17]);
						}
						else 
						{
							message[17] = (byte)(7-message[18]);
						}
					}
					
					if(message[16]==0 )//ping message
					{
						
						String remoteSocketAddress = simpellaClientSocket.getRemoteSocketAddress().toString();
						//System.out.println("ping received in Client, send by: "+ remoteSocketAddress);
						SimpellaVer1.ping(message, length, remoteSocketAddress);
	/*					SimpellaVer1.print("got a ping msg\n");
						
						ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
						ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
						
						//SimpellaVer1.print("Incoming Connections\n");
						for (int i=0; i< simpellaServerClientList.size(); i++)
						{
							SimpellaServerClient temp = simpellaServerClientList.get(i);
							
							if (this.equals(temp)){}
							else
								temp.outToClient.writeBytes(responseFromServer);
						}
						
						//SimpellaVer1.print("\nOutgoing Connections\n");
						for (int i=0; i< simpellaClientServerList.size(); i++)
						{
							SimpellaClient temp = simpellaClientServerList.get(i);
							if (this.equals(temp)){}
							else
								temp.outToServer.writeBytes(responseFromServer);
						}*/
					}
					else if (message[16]==1)
					{
						String remoteSocketAddress = simpellaClientSocket.getRemoteSocketAddress().toString();
						//System.out.println("passPong Received in client, send by: "+ remoteSocketAddress);
						SimpellaVer1.passPong(message);
					}
					else if (message[16]== -128)
					{
						
						sleep((int)(Math.random()*1000));
						String remoteSocketAddress = simpellaClientSocket.getRemoteSocketAddress().toString();
						//System.out.println("Query Received!!! from: "+ remoteSocketAddress);
						//SimpellaVer1.queryHit(message, simpellaClientSocket, remoteSocketAddress);
						// Modified by Amit
						//SimpellaVer1.queryHit(message, simpellaClientSocket, remoteSocketAddress);
						queryCount+=1;
						int payloadValue = SimpellaVer1.queryHit(message, simpellaClientSocket, remoteSocketAddress);
						if(payloadValue > 0)
						{
							//Query Hit is sent
							incrementMsgSent();
							incrementBytesSent(payloadValue);
							responseCount+=1;
						}
						//SimpellaVer1.passToAllButThis(message, remoteSocketAddress);
						//byte[] queryHitMessage = SimpellaVer1.queryHit(message);
						//this.simpellaClientSocket.getOutputStream().write(queryHitMessage);
						//System.out.println("Hello: "+queryHitMessage[16]);
						
						
						/*if(Monitor.isAlive)
						{
							int counter = 24;
							char ch;
							String messageIdString = "" ;
							while((ch=(char)message[++counter])!='\0')
							{
								messageIdString = messageIdString + ch;
							}
							
							//for(int i=0; i<16; i++)
								//messageIdString = messageIdString + message[i];
							
							Monitor.monitorString = messageIdString;
						}*/
					}
					else if (message[16]== -127)
					{
						//System.out.println("Query Hit Received!!!");
						SimpellaVer1.queryHitCounter++;
						String remoteSocketAddress = simpellaClientSocket.getRemoteSocketAddress().toString();
						//System.out.println("Query HIT Received!!! from: "+ remoteSocketAddress);
						SimpellaVer1.passQueryHit(message);
					}
					else if (message[16] == 2)
					{
						String remoteString = simpellaClientSocket.getRemoteSocketAddress().toString();
						simpellaClientSocket.close();
						for(int i=0; i<SimpellaClient.simpellaClientServerList.size(); i++)
						{
							SimpellaClient simpellaClient =SimpellaClient.simpellaClientServerList.get(i);
							if(simpellaClient.simpellaClientSocket.getRemoteSocketAddress().toString().equals(remoteString))
								SimpellaClient.simpellaClientServerList.remove(i);
						}
						//System.out.println("In client message[16] == 2");
						System.out.println("\nMessage from Simpella Network: "+remoteString.substring(1) + " left the network!!!");
						System.out.print("Simpella>>");
						MinTwoConnection.method();
						break;
						
					}
					else
					{
						System.out.println("unknown message");
					}
					
				}
			
				
			}
			catch(IOException | InterruptedException e)
			{
				//System.out.println(e.getMessage());
				System.exit(0);
			}
		}//while ends
		
	}
	
	//Added by Amit
	public synchronized void incrementMsgReceived()
	{
		msgReceived++;
	}
	public synchronized void incrementMsgSent()
	{
		msgSent++;
	}
	
	public synchronized void incrementBytesReceived(int payload)
	{
		bytesReceived = msgHeader+payload;
	}
	
	public synchronized void incrementBytesSent(int payload)
	{
		bytesSent = msgHeader+payload;
	}
	
	
	
	
}// class ends
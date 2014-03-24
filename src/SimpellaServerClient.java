import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SimpellaServerClient extends Thread
{
	Socket clientSocket;
	String remoteSocketAddressString;
	DataOutputStream outToClient;
	BufferedReader inFromClient;
	
	//info details
	//Modified by Amit
	public int msgReceived;
	public int msgSent;
	public int bytesReceived;
	public int bytesSent;
	public int msgHeader = 23;
	public int queryCount;
	public int responseCount;
	static ArrayList<SimpellaServerClient> simpellaServerClientList = new ArrayList<SimpellaServerClient>();
	
	public SimpellaServerClient(Socket clientSocket) throws Exception
	{
		this.clientSocket = clientSocket;
		this.remoteSocketAddressString = this.clientSocket.getRemoteSocketAddress().toString();
		
		outToClient = new DataOutputStream(clientSocket.getOutputStream());
		//We can add new client
		if(simpellaServerClientList.size()<3)
		{
			//inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			//System.out.println("waiting");
			//int letter = 0;
			//Read responce from client
			String responseFromClient = "";
			InputStream byteInputStream = clientSocket.getInputStream();
			
			
			byte[] byteResponseFromClient = new byte[4096];
			byteInputStream.read(byteResponseFromClient);
			
			for(int i =0; i<byteResponseFromClient.length; i++)
			{
				char ch = (char)byteResponseFromClient[i];
				//responseFromClient = responseFromClient + (char)byteResponseFromClient[i];
				responseFromClient = responseFromClient + ch;
				//break read buffer loop when encounter new line char
				if(ch == '\n')
					break;
			}
			System.out.print("\n\nMessage from Node: "+responseFromClient);
			/*int i =0;
			char ch;
			while((ch =(char)byteResponseFromClient[i])!='\n')
			{
				responseFromClient = responseFromClient + ch;
			}*/
		
			
			//String responseFromClient = inFromClient.readLine();
			if(responseFromClient.startsWith("SIMPELLA CONNECT/0.6"))
			{
				//System.out.println("in if block");
				//Added by Amit
				msgReceived = 0;
				msgSent = 0;
				bytesReceived =0;
				bytesSent = 0;
				queryCount = responseCount = 0;
				simpellaServerClientList.add(this);
				start();
				
			}
			else
			{
				//System.out.println(responseFromClient);
				String str = "SIMPELLA/0.6 504 Invalid connection request. Sorry!\r\n";
				byte[] maximumReached = new byte[4096];
				byte[] tempMaximumReached = str.getBytes();
				
				for(int i=0; i<tempMaximumReached.length; i++)
					maximumReached[i] = tempMaximumReached[i];
				
				clientSocket.getOutputStream().write(maximumReached);
				//outToClient.writeBytes("SIMPELLA/0.6 504 Invalid connection request. Sorry!\r\n");
			}
				
			
		}
		else
		{
			String str = "SIMPELLA/0.6 503 Maximum number of connections reached. Sorry!\r\n";
			byte[] maximumReached = new byte[4096];
			byte[] tempMaximumReached = str.getBytes();
			
			for(int i=0; i<tempMaximumReached.length; i++)
				maximumReached[i] = tempMaximumReached[i];
			
			clientSocket.getOutputStream().write(maximumReached);
		}
		
	}
	
	public void run()
	{
		try
		{
			String str = "SIMPELLA/0.6 200 OK\r\n";
			byte[] ok = new byte[4096];
			byte[] tempOk = str.getBytes();
			
			for(int i=0; i<tempOk.length; i++)
				ok[i] = tempOk[i];
			
			clientSocket.getOutputStream().write(ok);
			//System.out.println(str + " written");
			//outToClient.writeBytes("SIMPELLA/0.6 200 OK\r\n");
			//String responseFromClient = inFromClient.readLine();
			
			String responseFromClient = "";
			InputStream byteInputStream = clientSocket.getInputStream();
			
			//Read client response
			byte[] byteResponseFromClient = new byte[4096];
			byteInputStream.read(byteResponseFromClient);
			
			for(int i =0; i<byteResponseFromClient.length; i++)
			{
				char ch = (char)byteResponseFromClient[i];
				responseFromClient = responseFromClient + ch;
				//Break condn
				if (ch == '\n')
					break;
			}
			
			/*int charIndex =0;
			char ch;
			while((ch =(char)byteResponseFromClient[charIndex])!='\n')
			{
				responseFromClient = responseFromClient + ch;
			}*/
			
			
			System.out.print("Message from Node: "+responseFromClient);
			System.out.println("Connection established successfully!!!\n");
			//SimpellaVer1.print(responseFromClient+"\n");
			//UpdateMethodClass.update();
			System.out.print("Simpella>>");
			while(true)
			{
				//System.out.println("waiting...");
				//responseFromClient = inFromClient.readLine();
				byte[] message = new byte[4096];
				int length = clientSocket.getInputStream().read(message);
				
				//Modified by Amit				
				incrementMsgReceived();				
				//Get message payload				
				//increment bytes received
				incrementBytesReceived(SimpellaVer1.getMessagePayloadLenght(message));
				if(message[17]>15)
				{
					//ignore the messafges having TTL>15
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
					
					
					if(message[16]== 0 )//ping message
					{
						String remoteSocketAddress = clientSocket.getRemoteSocketAddress().toString();
						//System.out.println("ping recevied in Server, send by: "+remoteSocketAddress);
						SimpellaVer1.ping(message, length, remoteSocketAddress);
	/*					ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
						ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
						
						//SimpellaVer1.print("Incoming Connections\n");
						for (int i=0; i< simpellaServerClientList.size(); i++)
						{
							SimpellaServerClient temp = simpellaServerClientList.get(i);
							
							if (this.equals(temp)){}
							else
								temp.outToClient.writeBytes(responseFromClient);
						}
						
						//SimpellaVer1.print("\nOutgoing Connections\n");
						for (int i=0; i< simpellaClientServerList.size(); i++)
						{
							SimpellaClient temp = simpellaClientServerList.get(i);
							if (this.equals(temp)){}
							else
								temp.outToServer.writeBytes(responseFromClient);
						}*/
					 }// if ends
					else if (message[16]==1)
					{
						String remoteSocketAddress = clientSocket.getRemoteSocketAddress().toString();
						//System.out.println("passPong Received in server, send by: "+ remoteSocketAddress);
						SimpellaVer1.passPong(message);
						
					}// Pong ends
					else if (message[16] == -128)
					{
						
						sleep((int)(Math.random()*1000));
						String remoteSocketAddress = clientSocket.getRemoteSocketAddress().toString();
						//System.out.println("Query Received!!! from: "+ remoteSocketAddress);
						//SimpellaVer1.queryHit(message, clientSocket, remoteSocketAddress);
						//SimpellaVer1.passToAllButThis(message, remoteSocketAddress);
						//byte[] queryHitMeaasge = SimpellaVer1.queryHit(message);
						//this.clientSocket.getOutputStream().write(queryHitMeaasge);
						//System.out.println("Hello: "+queryHitMeaasge[16]);
						//Modified by Amit - Increment the msgSent count if QHT is sent
						//SimpellaVer1.queryHit(message, clientSocket, remoteSocketAddress);
						queryCount+=1; //include duplicate queries
						int payloadValue = SimpellaVer1.queryHit(message, clientSocket, remoteSocketAddress);					
						if(payloadValue > 0)
						{
							//Query Hit is sent
							incrementMsgSent();
							incrementBytesSent(payloadValue);
							responseCount+=1; //QHT sent
						}
						
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
						
					}// query ends
					else if (message[16] == -127)
					{
						String remoteSocketAddress = clientSocket.getRemoteSocketAddress().toString();
						//System.out.println("Query HIT Received!!! from: "+ remoteSocketAddress);
						SimpellaVer1.queryHitCounter++;
						SimpellaVer1.passQueryHit(message);
					}
					else if(message[16] == 2)
					{
						//clientSocket.close();
						String remoteString = this.clientSocket.getRemoteSocketAddress().toString();
						for(int i=0; i<SimpellaServerClient.simpellaServerClientList.size(); i++)
						{
							SimpellaServerClient simpellaServerClient = SimpellaServerClient.simpellaServerClientList.get(i);
							if(simpellaServerClient.clientSocket.getRemoteSocketAddress().toString().equals(remoteString))
								SimpellaServerClient.simpellaServerClientList.remove(i);
							
						}
						//System.out.println("in server message[16] == 2");
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
		}
		catch(IOException | InterruptedException |ArrayIndexOutOfBoundsException e)
		{
			
		}
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
	
}

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;



public class SimpellaVer1 
{
	public static ServerSocket serverSocket;
	static File sharedDirectoryPath;// = new File(".");
	static int numberOfFiles = 0;
	static double dataSharedinKiloBytes = 0;
	static double dataSharedinBytes = 0;
	
	static ArrayList<byte[]> messageIdList = new ArrayList<byte[]>();
	static ArrayList<String> remoteSocketAddressList = new ArrayList<String>();
	
	
	static ArrayList<String> queryMessageIdList = new ArrayList<String>();
	static ArrayList<String> queryRemoteSocketAddressList = new ArrayList<String>();
	
	public  static ArrayList<byte[]> updateMessageIdList = new ArrayList<byte[]>();
	
	static HashMap<String, Update> infoFromPongsList = new HashMap<String, Update>();
	
	static ArrayList<byte[]> querySendByMe = new ArrayList<byte[]>();
	static HashMap<String, FileInfoFromQH> FileInfoFromQHHashMap = new HashMap<String, FileInfoFromQH>();
	static ArrayList<String> FileInfoFromCurrentQHArrayList = new ArrayList<String>();
	
	static ArrayList<FileInfoFromQH> tempFileList = new ArrayList<FileInfoFromQH>();
	
	static int queryHitCounter = 0;
	
	static int passPongCounter = 0;
	
	static byte[] uNI;
	
	static ArrayList<File> fileList = new ArrayList<File>();
	
	//Methods starts
	
	/*
	 * Info command with Options - Modified by Amit
	 * */
	public static void info(String option)
	{
		
		try
		{
			if(option.matches("[cdhnqs]"))
			{
				//System.out.println("Option "+option+" is valid");
				//check individual options
				if(option.equals("h"))
				{
					System.out.println("HOSTS STATS: ");
					System.out.println("---------------------");
					int numHost = infoFromPongsList.size();
					//System.out.println("Hosts: "+numHost);
					int numberOfFilesShared = 0;
					int numberOfKilobytesShared = 0;
					//Iterate over infoFromPongList
					@SuppressWarnings("rawtypes")
					Iterator it = infoFromPongsList.entrySet().iterator();
					while(it.hasNext())
					{
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) it.next();
						//System.out.println(pairs.getKey() + " = " +(Update) pairs.getValue());
						Update values = (Update)pairs.getValue();
						numberOfFilesShared = numberOfFilesShared + values.numberOfFilesShared;
						numberOfKilobytesShared = numberOfKilobytesShared + values.numberOfKilobytesShared;						
					}
					
					System.out.println("Hosts: "+numHost+"\tFiles: "+numberOfFilesShared+"\tSize: "+numberOfKilobytesShared+" k");
				}// option h ends
				else if(option.equals("c"))
				{
					//System.out.println("Option c called");
					System.out.println("CONNECTION STAT: received:sent");
					System.out.println("-------------------------------------");
					ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
					ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
					
					//SimpellaVer1.print("\nIncoming Connections\n");
					int cnt = 0;
					for (int i=0; i< simpellaServerClientList.size(); i++)
					{
						SimpellaServerClient temp = simpellaServerClientList.get(i);
						cnt = i+1;
						double kiloBytesReceived = (double)temp.bytesReceived/1024;
						double kiloBytesSent = (double)temp.bytesSent/1024;
						System.out.println(""+cnt+") "+temp.remoteSocketAddressString+"\t Packs: "+temp.msgReceived+" : "+temp.msgSent+"\tBytes: "+kiloBytesReceived+":"+kiloBytesSent+"K");
					}
					
					//outgoing connections
					for (int i=0; i< simpellaClientServerList.size(); i++)
					{
						SimpellaClient temp = simpellaClientServerList.get(i);
						cnt = i+1;
						double kiloBytesReceived = (double)temp.bytesReceived/1024;
						double kiloBytesSent = (double)temp.bytesSent/1024;						
						System.out.println(""+cnt+") "+temp.remoteSocketAddressString+"\t Packs: "+temp.msgReceived+" : "+temp.msgSent+"\tBytes: "+kiloBytesReceived+":"+kiloBytesSent+"K");
					}
					
				}//end option c
				else if(option.equals("n"))
				{
					System.out.println("NET STATS: received: Sent");
					System.out.println("-------------------------------");
					int totalMsgReceived = 0;
					int totalMsgSent = 0;
					double totalBytesReceived = 0;
					double totalBytesSent = 0;
					ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
					ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
					//Incoming
					for (int i=0; i< simpellaServerClientList.size(); i++)
					{
						SimpellaServerClient temp = simpellaServerClientList.get(i);
						totalMsgReceived += temp.msgReceived;
						totalMsgSent += temp.msgSent;
						totalBytesReceived +=temp.bytesReceived;
						totalBytesSent += temp.bytesSent;
					}
					//outgoing connections
					for (int i=0; i< simpellaClientServerList.size(); i++)
					{
						SimpellaClient temp = simpellaClientServerList.get(i);
						totalMsgReceived += temp.msgReceived;
						totalMsgSent += temp.msgSent;
						totalBytesReceived +=temp.bytesReceived;
						totalBytesSent += temp.bytesSent;
					}
					
					//convert bytes to KB
					totalBytesReceived = totalBytesReceived/1024;
					totalBytesSent = totalBytesSent/1024;
					
					//Unique message guid
					int uniqueMessageGUID = messageIdList.size();
					
					//display
					System.out.println("Message Received: "+totalMsgReceived+"\tMessage Sent: "+totalMsgSent);
					System.out.println("Unique GUIDs in memory: "+uniqueMessageGUID);
					System.out.println("Bytes Received: "+totalBytesReceived+" K\tBytes Sent: "+totalBytesSent+" K");
					
				}//end of option n
				else if(option.matches("q"))
				{
					System.out.println("QUERY STAT: ");
					System.out.println("---------------------");
					ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
					ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
					int totalQueryCount =0; //number of queries received by servent on all connections
					int totalResponseCount =0;//number of responses sent by all connections
					//Incoming
					for (int i=0; i< simpellaServerClientList.size(); i++)
					{
						SimpellaServerClient temp = simpellaServerClientList.get(i);
						totalQueryCount += temp.queryCount;
						totalResponseCount += temp.responseCount;
					}
					
					//Outgoing
					for (int i=0; i< simpellaClientServerList.size(); i++)
					{
						SimpellaClient temp = simpellaClientServerList.get(i);
						totalQueryCount += temp.queryCount;
						totalResponseCount += temp.responseCount;
					}
					//Display
					System.out.println("Queries: "+totalQueryCount+"\tResponses Sent: "+totalResponseCount);
				}//option q ends
				else if(option.matches("s"))
				{
					System.out.println("SHARE STATS: ");
					System.out.println("---------------------");
					SimpellaVer1.scan();
					int numFilesShared = SimpellaVer1.fileList.size();
					double sizeFilesShared = 0;
					for (int i=0;i<numFilesShared;i++)
					{
						sizeFilesShared +=(double) SimpellaVer1.fileList.get(i).length();
					}
					//size in Kb
					sizeFilesShared = sizeFilesShared/1024;
					System.out.println("Number of Files Shared: "+numFilesShared+"\tSize Shared: "+sizeFilesShared+" K");
				}//optins s ends
				else if(option.matches("d"))
				{
					System.out.println("DOWNLOAD STATS: ");
					System.out.println("---------------------");
					for(int i=0; i<SimpellaWebClient.simpellaWebClientsList.size(); i++)
					{
						SimpellaWebClient temp = SimpellaWebClient.simpellaWebClientsList.get(i);
						String webServerIP = temp.webServer.toString();
						int serverPort = temp.serverPort;
						double totalFileSize = temp.totalFileBytes/1024;
						double partialFileSize = temp.readFileBytes/1024;
						double percentComplete = (partialFileSize/totalFileSize) *100;
						System.out.println((i+1)+webServerIP+":"+serverPort+"\tPercent Complete: "+percentComplete+"%\tActual Complete: "+partialFileSize+" K/"+totalFileSize+" K");
						
					}
				}
				
				
			}
			else
			{
				
				throw new IllegalArgumentsException();
			}
		}
		catch(IllegalArgumentsException e)
		{
			String str = "Inavlid option\n";
			str = str+"valid options are: \n";
			str = str+"c - displays simpella network connections\n";
			str = str+"d - file transfer in progress\n";
			str = str+"h - number of hosts, number of files they are sharing, and total size of those shared files\n";
			str = str+"q - queries received and replies sent\n";
			str = str+"s - number and total size of shared files on this host\n";
			System.out.println(str);
		}
				
	}//Info command default
	
	
synchronized	public static void passToAll(byte[] message)
	{
		try 
		{
			ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
			ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
			
			//SimpellaVer1.print("\nIncoming Connections\n");
			for (int i=0; i< simpellaServerClientList.size(); i++)
			{
				SimpellaServerClient temp = simpellaServerClientList.get(i);
				//if(!temp.clientSocket.isClosed())
				{
					temp.clientSocket.getOutputStream().write(message);
					
					//Modified by Amit
					temp.incrementMsgSent();
					temp.incrementBytesSent(getMessagePayloadLenght(message));
				}
			}
			
			//SimpellaVer1.print("\nOutgoing Connections\n");
			for (int i=0; i< simpellaClientServerList.size(); i++)
			{
				SimpellaClient temp = simpellaClientServerList.get(i);
				//if(!temp.simpellaClientSocket.isClosed())
				{
					temp.simpellaClientSocket.getOutputStream().write(message);
					//Modified by Amit
					temp.incrementMsgSent();
					temp.incrementBytesSent(getMessagePayloadLenght(message));
				}
				
			}
		} 
		catch (SocketException e) 
		{
			
		}
		catch (IOException e) 
		{
			//e.printStackTrace();
		}
		
	}
	
synchronized	public static void passToAllButThis (byte[] message, String remoteSocketAddress)
	{
		try
		{
			String messageIdString = "";
			
			for(int i=0; i<16; i++)
			{
				messageIdString = messageIdString + message[i];
			}
			
			ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
			ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
			
			//SimpellaVer1.print("\nIncoming Connections\n");
			for (int i=0; i< simpellaServerClientList.size(); i++)
			{
				SimpellaServerClient temp = simpellaServerClientList.get(i);
				
				String remoteSocketAddressString = temp.clientSocket.getRemoteSocketAddress().toString();
				
				if(remoteSocketAddressString.equals(remoteSocketAddress))
				{
					/*byte[] queryHitMessage = SimpellaVer1.queryHit(message);
					System.out.println(message[16] + " in passToAllButThis");
					temp.clientSocket.getOutputStream().write(queryHitMessage);*/
				}
				else
				{
					//Modified by Amit
					temp.clientSocket.getOutputStream().write(message);
					temp.incrementMsgSent();
					temp.incrementBytesSent(getMessagePayloadLenght(message));
				}
			}
			
			//SimpellaVer1.print("\nOutgoing Connections\n");
			for (int i=0; i< simpellaClientServerList.size(); i++)
			{
				SimpellaClient temp = simpellaClientServerList.get(i);
				
				String remoteSocketAddressString = temp.simpellaClientSocket.getRemoteSocketAddress().toString();
				
				if(remoteSocketAddressString.equals(remoteSocketAddress))
				{
					/*byte[] queryHitMessage = SimpellaVer1.queryHit(message);
					System.out.println(message[16] + " in passToAllButThis");
					temp.simpellaClientSocket.getOutputStream().write(queryHitMessage);*/
				}
				else
				{
					//Modified by Amit
					temp.simpellaClientSocket.getOutputStream().write(message);
					temp.incrementMsgSent();
					temp.incrementBytesSent(getMessagePayloadLenght(message));
				}
			}
			
			if(SimpellaVer1.queryMessageIdList.size()>=160)
			{
				SimpellaVer1.queryMessageIdList.remove(0);
				SimpellaVer1.queryRemoteSocketAddressList.remove(0);
			}
			SimpellaVer1.queryMessageIdList.add(messageIdString);
			SimpellaVer1.queryRemoteSocketAddressList.add(remoteSocketAddress);
		}
		catch(IOException e)
		{
			//System.out.println(e.getMessage());
		}
	}// passToALLButThis ends
	
synchronized	public static void ping(byte[] message, int length, String remoteSocketAddress)
	{
		try 
		{
			//SimpellaVer1.print("got a ping msg\n");
			byte[] messageId = new byte[16];
			
			for (int i=0; i<16; i++)
				messageId[i] = message[i];
			
			String messageIdString ="";
			for (int i=0; i<16; i++)
				messageIdString = messageIdString + messageId[i];
			
			//System.out.println("In ping(), MessageId: " + messageIdString);
			
			
			//checking passing ping through me....
			boolean isPresent = false;
			for(int i=0; i<SimpellaVer1.messageIdList.size() ;i++)
			{
				byte[] temp = SimpellaVer1.messageIdList.get(i);
				
				//System.out.println("inside for: " + i);
				
				String tempMessageIdString ="";
				for (int j=0; j<=15; j++)
					tempMessageIdString = tempMessageIdString + temp[j];
				
				//System.out.println("tempMessageIdString: "+tempMessageIdString);
				//System.out.println("messageIdString: "+messageIdString);
				
				if(tempMessageIdString.equals(messageIdString))
				{
					//System.out.println("Duplicate ping!!!");
					isPresent = true;
					break;
				}
				
			}
			
			//checking ping created by me
			for (int i =0; i<SimpellaVer1.updateMessageIdList.size(); i++)
				if(Arrays.equals(messageId, SimpellaVer1.updateMessageIdList.get(i)))
					isPresent = true;
			
			if(!isPresent)
			{
				//message[17] = (byte)(message[17] - 1);
				//message[18] = (byte)(message[18] + 1);
				//System.out.println("TTL: " + message[17]);
				//System.out.println("HOP: " + message[18]);
				
				ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
				ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
				
				//for (int i=0; i<length; i++)
					//message[i] = message[i];
				
				//SimpellaVer1.print("Incoming Connections\n");
				for (int i=0; i< simpellaServerClientList.size(); i++)
				{
					SimpellaServerClient temp = simpellaServerClientList.get(i);
					String tempRemoteSocketAddress = temp.clientSocket.getRemoteSocketAddress().toString();
					if (tempRemoteSocketAddress.equals(remoteSocketAddress))
					{
						//System.out.println("hello " + remoteSocketAddress);
						//System.out.println("Calling pong method");
						byte[] pongMessage = SimpellaVer1.pong(messageId);
						temp.clientSocket.getOutputStream().write(pongMessage);
					}
					else
					{
						if(message[17]>1)
						{
							message[17] = (byte)(message[17] - 1);
							message[18] = (byte)(message[18] + 1);
							temp.clientSocket.getOutputStream().write(message);
						}
					}
					
					
					//Modified by Amit - message sent increment counter 
					temp.incrementMsgSent();
					//Get msg payload lenght
					int payloadLenght = getMessagePayloadLenght(message);
					temp.incrementBytesSent(payloadLenght);
				}
				
				//SimpellaVer1.print("\nOutgoing Connections\n");
				for (int i=0; i< simpellaClientServerList.size(); i++)
				{
					SimpellaClient temp = simpellaClientServerList.get(i);
					String tempRemoteSocketAddress = temp.simpellaClientSocket.getRemoteSocketAddress().toString();
					if (tempRemoteSocketAddress.equals(remoteSocketAddress))
					{
						//System.out.println("Calling pong method");
						byte[] pongMessage = SimpellaVer1.pong(messageId);
						temp.simpellaClientSocket.getOutputStream().write(pongMessage);
						
						//System.out.println("hello " + remoteSocketAddress);
					}
					else
					{
						if(message[17]>1)
						{
							message[17] = (byte)(message[17] - 1);
							message[18] = (byte)(message[18] + 1);
							temp.simpellaClientSocket.getOutputStream().write(message);
						}
						
					}	
				
					//Modified by Amit - message sent increment counter 
					temp.incrementMsgSent();
					int payloadLenght = getMessagePayloadLenght(message);
					temp.incrementBytesSent(payloadLenght);
				}
				if(SimpellaVer1.messageIdList.size()>=160)
				{
					SimpellaVer1.messageIdList.remove(0);
					SimpellaVer1.remoteSocketAddressList.remove(0);
				}
				
				SimpellaVer1.messageIdList.add(messageId);						
								
				SimpellaVer1.remoteSocketAddressList.add(remoteSocketAddress);
				//System.out.println("Adding :" + messageIdString);
				

			}// if(!isPresent)
			else
			{
				//System.out.println("Message Id already present");
			}
		} 
		catch (IOException e) 
		{
			//e.printStackTrace();
		}
	}//pings end
	
synchronized	public static byte[] pong(byte[] messageId)
	{
		byte[] message = new byte[4096];
		for (int i=0; i<messageId.length; i++)
			message[i] = messageId[i];
		
		String messageIdString= "";
		for (int i=0; i<messageId.length; i++)
			messageIdString = messageIdString + message[i];
			
		//System.out.println("Creating Pong, MessageId: " + messageIdString);
		
		message[16] = (byte)1;//Message Type Pong
		message[17] = (byte)7;//TTL = 7
		message[18] = (byte)0;//Hops = 0 initially
		
		int payloadLength = 14;
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(payloadLength);
		byte[] payloadLengthArray = bb.array();
		message[19] = payloadLengthArray[0];
		message[20] = payloadLengthArray[1];
		message[21] = payloadLengthArray[2];
		message[22] = payloadLengthArray[3];
		
		//Payload
		short port = (short)SimpellaServer.tcpPort;
		ByteBuffer bbPort = ByteBuffer.allocate(2);
		bbPort.putShort(port);
		byte[] portArray = bbPort.array();
		message[23] = portArray[0];
		message[24] = portArray[1];
		
		byte[] ipAddress = SimpellaServer.hostAddress.getAddress();
		//System.out.println("ipAddressString:\t"+ipAddressString);
		//String[] ipAddress = ipAddressString.split("\\.");
		//System.out.println("ipAddress.length:\t"+ipAddress.length);
		message[25] = ipAddress[0];
		message[26] = ipAddress[1];
		message[27] = ipAddress[2];
		message[28] = ipAddress[3];
		//byte[] ipAddress2 = SimpellaServer.hostAddress.getHostAddress().getBytes();
		//System.out.println("ipAddress2.length:\t" + ipAddress2.length);
		/*int a = message[25] & 0xff;
		int b = message[26] & 0xff;
		int c = message[27] & 0xff;
		int d = message[28] & 0xff;
		System.out.println("IPAddress entered in pong: "+a+"."+b+"."+c+"."+d);*/
		
		SimpellaVer1.scan();
		
		ByteBuffer bbNumberOfSharedFiles = ByteBuffer.allocate(4);
		bbNumberOfSharedFiles.putInt(SimpellaVer1.numberOfFiles);
		byte[] numberOfSharedFilesArray = bbNumberOfSharedFiles.array();
		message[29] = numberOfSharedFilesArray[0];
		message[30] = numberOfSharedFilesArray[1];
		message[31] = numberOfSharedFilesArray[2];
		message[32] = numberOfSharedFilesArray[3];
		
		ByteBuffer bbDataSharedinKilobytes = ByteBuffer.allocate(4);
		bbDataSharedinKilobytes.putInt((int)SimpellaVer1.dataSharedinKiloBytes);
		byte[] dataSharedinKilobytesArray = bbDataSharedinKilobytes.array();
		message[33] = dataSharedinKilobytesArray[0];
		message[34] = dataSharedinKilobytesArray[1];
		message[35] = dataSharedinKilobytesArray[2];
		message[36] = dataSharedinKilobytesArray[3];
		
		return message;
	}
	
	public static void scan()
	{
		SimpellaVer1.fileList.clear();
		File[] listOfFiles = sharedDirectoryPath.listFiles(); 
		 int count =0;
		 long size = 0;
		 
		 for (int i = 0; i < listOfFiles.length; i++) 
		 {
			 if (listOfFiles[i].isFile()) 
		     {
				 count++;
				 size = size + listOfFiles[i].length();
				 SimpellaVer1.fileList.add(listOfFiles[i]);
		     }
			 
			 
		 }
		 
		 numberOfFiles = count;
		 dataSharedinKiloBytes = (double)(((double)size)/1024.0);
		 dataSharedinBytes = size;
		  
	}
	
	//Modified by Amit 
	//public static void queryHit(byte[] message, Socket socket, String remoteSocketAddress)
synchronized	public static int queryHit(byte[] message, Socket socket, String remoteSocketAddress)
	{
			//System.out.println("Creating Query Hit...");
			
			//Modified by Amit
			int payloadLenght = 0;// method returns this value changed if file match found
		
			//To check for duplicate queryHit
			boolean isPresent = false;
			String messageIdString = "";
			
			for(int i=0; i<16; i++)
			{
				messageIdString = messageIdString + message[i];
			}
			
			//System.out.println("SimpellaVer1.queryMessageIdList.size(): "+SimpellaVer1.queryMessageIdList.size());
			for(int i=0; i<SimpellaVer1.queryMessageIdList.size(); i++)
			{
				String tempMessageId = SimpellaVer1.queryMessageIdList.get(i);
				
				if(tempMessageId.equals(messageIdString))
				{
					isPresent = true;
					break;
				}
			}
			//check if the query message is generated by u
			for(int i=0; i<SimpellaVer1.querySendByMe.size(); i++)
			{
				byte[] tempMessageId = SimpellaVer1.querySendByMe.get(i);
				
				String tempMessageIdString = "";
				for(int j=0; j<16; j++)
					tempMessageIdString = tempMessageIdString + tempMessageId[j];
				
				if(tempMessageIdString.equals(messageIdString))
				{
					isPresent = true;
					break;
				}
			}
			
			
			byte[] payloadArray = Arrays.copyOfRange(message, 19, 23);
			ByteBuffer bbPayloadArray = ByteBuffer.wrap(payloadArray);
			int payload = bbPayloadArray.getInt();
			//System.out.println("payload: "+payload);
			//File Searching
			if(!isPresent && payload<4096)
			{
				
				
				//System.out.println("new query");
				
			
				String fileString = "";
				for(int i=0; i<payload-2; i++)
					fileString = fileString + (char)message[25+i];
				
				//System.out.println("file to be searched: "+ fileString);
				
				String[] fileStringArray = fileString.split(" ");
				//for(int i=0; i<fileStringArray.length; i++)
					//System.out.println("fileStringArray: "+fileStringArray[i]);
				
				//File[] listOfFiles = sharedDirectoryPath.listFiles();
				ArrayList<File> fileArrayList= SimpellaVer1.fileList;
				
				ArrayList<File> matchedFileList = new ArrayList<File>();
				ArrayList<Integer> fileIndexArrayList = new ArrayList<Integer>();
					
				SimpellaVer1.scan();
				
				for(int i=0; i<fileArrayList.size(); i++)
				{
					String fileInSharedDir = fileArrayList.get(i).getName();
					String fileInSharedDirToLower = fileInSharedDir.toLowerCase();
					for(int j=0; j<fileStringArray.length; j++)
					{
						String fileStringArraytoLower = fileStringArray[j].toLowerCase();
						if(fileInSharedDirToLower.contains(fileStringArraytoLower))
						{
							matchedFileList.add(fileArrayList.get(i));
							fileIndexArrayList.add(i+1);
						}
							
					}
					
				}
				
				if(fileStringArray[0].equals("allsharedfiles"))
				{
					for(int i=0; i<fileArrayList.size(); i++)
					{
						matchedFileList.add(fileArrayList.get(i));
						fileIndexArrayList.add(i+1);
					}
				}
				
				/*for (int i=0; i < fileArrayList.size(); i++) 
				{
					//if (fileArrayList.get(i).isFile()) 
				    {
						String tempFileName = fileArrayList.get(i).getName();
						//String[] tempFileNamewithoutextension = tempFileName.split(".");
						//tempFileName = tempFileNamewithoutextension[0];
						
						int indexOfPereiod = tempFileName.lastIndexOf('.');
						if(indexOfPereiod != -1)
							tempFileName = tempFileName.substring(0, indexOfPereiod);
						
						String[] tempFileNameArray = tempFileName.split(" ");
						for(int k=0; k<tempFileNameArray.length; k++)
						{
							//System.out.println("tempFileName: "+tempFileName);
							for(int j=0; j<fileStringArray.length; j++)
							{
								if(tempFileNameArray[k].equalsIgnoreCase(fileStringArray[j]))
								{
									matchedFileList.add(fileArrayList.get(i));
									fileIndexArrayList.add(i+1);
								}
							}
						}
						
				    }
				}*/
				
				//System.out.println("matchedFileList.size(): "+matchedFileList.size());
				//for(int i=0; i<matchedFileList.size(); i++)
					//System.out.println("Matched files: "+matchedFileList.get(i).getName());
				
				
				if(matchedFileList.size()>0)
				{
					//System.out.println("files matched: "+matchedFileList.size());
					byte[] queryHitmessage = new byte[4096];
					
					for(int i=0; i<16; i++)
						queryHitmessage[i] = message[i];
					
					queryHitmessage[16] = (byte)0x81;//Message Type QueryHit
					//System.out.println(message[16]);
					queryHitmessage[17] = (byte)(message[18]+2);
					queryHitmessage[18] = (byte)0;//Hops = 0 initially
					
					//payload message
					queryHitmessage[23] = (byte)matchedFileList.size();
					
					short port = (short)SimpellaServer.httpPort;
					ByteBuffer bbPortArray = ByteBuffer.allocate(2);
					bbPortArray.putShort(port);
					byte[] portArray = bbPortArray.array();
					queryHitmessage[24] = portArray[0];
					queryHitmessage[25] = portArray[1];
					
					byte[] ipAddress = SimpellaServer.hostAddress.getAddress();
					//System.out.println("ipAddressString:\t"+ipAddressString);
					//String[] ipAddress = ipAddressString.split("\\.");
					//System.out.println("ipAddress.length:\t"+ipAddress.length);
					queryHitmessage[26] = ipAddress[0];
					queryHitmessage[27] = ipAddress[1];
					queryHitmessage[28] = ipAddress[2];
					queryHitmessage[29] = ipAddress[3];
					
					int speed = 10000;
					ByteBuffer bbSpeed = ByteBuffer.allocate(4);
					bbSpeed.putInt(speed);
					byte[] speedArray = bbSpeed.array();
					queryHitmessage[30] = speedArray[0];
					queryHitmessage[31] = speedArray[1];
					queryHitmessage[32] = speedArray[2];
					queryHitmessage[33] = speedArray[3];
					
					int counter = 33;
					for(int i=0; i<matchedFileList.size(); i++)
					{
						int index = fileIndexArrayList.get(i);
						ByteBuffer bbIndex = ByteBuffer.allocate(4);
						bbIndex.putInt(index);
						byte[] indexArray = bbIndex.array();
						
						queryHitmessage[++counter] = indexArray[0];
						queryHitmessage[++counter] = indexArray[1];
						queryHitmessage[++counter] = indexArray[2];
						queryHitmessage[++counter] = indexArray[3];
						
						File file = matchedFileList.get(i);
						
						int size = (int)file.length();
						ByteBuffer bbSize = ByteBuffer.allocate(4);
						bbSize.putInt(size);
						byte[] sizeArray = bbSize.array();
						byte[] c = new byte[4];
						c[0] = queryHitmessage[++counter] = sizeArray[0];
						c[1] = queryHitmessage[++counter] = sizeArray[1];
						c[2] = queryHitmessage[++counter] = sizeArray[2];
						c[3] = queryHitmessage[++counter] = sizeArray[3];
						//System.out.println("size :"+ size);
						
						
						//ByteBuffer a = ByteBuffer.wrap(c);
						//int b = a.getInt();
						//System.out.println("b: "+b);
						
						String fileName = file.getName();
						byte[] fileNameArray = fileName.getBytes();
						for(int j=0; j<fileNameArray.length; j++)
						{
							queryHitmessage[++counter] = fileNameArray[j];
						}
						queryHitmessage[++counter] = (byte)'\0';
						
					}
															
					for(int i =0; i<16; i++)
						queryHitmessage[++counter] = uNI[i];
					
					//System.out.println("counter: "+ counter);
					int payloadValue = counter - 22;
					
					ByteBuffer bbPayLoadValue = ByteBuffer.allocate(4);
					bbPayLoadValue.putInt(payloadValue);
					byte[] payloadValueArray = bbPayLoadValue.array();
					queryHitmessage[19] = payloadValueArray[0];
					queryHitmessage[20] = payloadValueArray[1];
					queryHitmessage[21] = payloadValueArray[2];
					queryHitmessage[22] = payloadValueArray[3];
					
					ByteBuffer d = ByteBuffer.wrap(payloadValueArray);
					payloadValue = d.getInt();
					/*System.out.println("payloadValue: "+ payloadValue);
					System.out.println("queryHitmessage[payloadValue+22]: "+queryHitmessage[payloadValue+22]);
					System.out.println("queryHitmessage[payloadValue+23]: "+queryHitmessage[payloadValue+23]);
					System.out.println("queryHitmessage[++payloadValue+23]: "+queryHitmessage[++payloadValue+23]);*/
					
					//prining out the data
					
					try 
					{
						socket.getOutputStream().write(queryHitmessage);
						//Modified by Amit - Increment count
						payloadLenght = payloadValue;						
						
					}
					catch (IOException e) 
					{
						//e.printStackTrace();
					}
				}
				
				//Monitor.monitorString = "";
				
				if(Monitor.isAlive)
				{
					int counter = 24;
					char ch;
					String searchedfileName = "" ;
					while((ch=(char)message[++counter])!='\0')
					{
						searchedfileName = searchedfileName + ch;
					}
					
					//for(int i=0; i<16; i++)
						//messageIdString = messageIdString + message[i];
					
					Monitor.monitorString = searchedfileName;
					Monitor.monitorMessageIdString = messageIdString;
				}
				//Monitor.monitorString = "";
				
				if(message[17]>1)
				{
					message[17] = (byte)(message[17]-1);
					message[18] = (byte)(message[18]+1);
					SimpellaVer1.passToAllButThis(message, remoteSocketAddress);
				}
			}
			else
			{
				//System.out.println("Duplicate query msg!!!");
			}
			
			//Modified by Amit
			return payloadLenght;
			
	}//queryHit ends
	
	public static byte[] generateUNI()
	{
		byte[] uNI = new byte[16];
		Random random = new Random();
		random.nextBytes(uNI);
		return uNI;
	}



	
synchronized	public static void passQueryHit(byte[] message)	
	{
		try 
		{
			boolean isPresent = false;
			byte[] messageId = Arrays.copyOfRange(message, 0, 16);
			
			for(int i=0; i<SimpellaVer1.querySendByMe.size(); i++)
			{
				byte[] tempMessageId = SimpellaVer1.querySendByMe.get(i);
				if(Arrays.equals(tempMessageId, messageId))
					isPresent = true;
			}
			
			
			
			//System.out.println("in passQueryHit");
			if(!isPresent && message[17]>1)
			{
				message[17] = (byte)(message[17]-1);
				message[18] = (byte)(message[18]+1);
				
				String messageIdString = "";
				for(int i=0; i<16; i++)
					messageIdString = messageIdString + message[i];
				
				String remoteSocketAddress = "";
				
				for (int i=0; i<SimpellaVer1.queryMessageIdList.size(); i++)
				{
					if(SimpellaVer1.queryMessageIdList.get(i).equals(messageIdString))
					{
						remoteSocketAddress = SimpellaVer1.queryRemoteSocketAddressList.get(i);
						break;
					}
					
				}
				
				for (int i=0; i<SimpellaServerClient.simpellaServerClientList.size(); i++)
				{
					String temRemoteSocketAddress =  SimpellaServerClient.simpellaServerClientList.get(i).clientSocket.getRemoteSocketAddress().toString();
					if(temRemoteSocketAddress.equals(remoteSocketAddress))
					{
						//Modified by Amit
						SimpellaServerClient.simpellaServerClientList.get(i).clientSocket.getOutputStream().write(message);
						SimpellaServerClient.simpellaServerClientList.get(i).incrementMsgSent();
					}
				}
				
				for(int i=0; i<SimpellaClient.simpellaClientServerList.size(); i++)
				{
					String temRemoteSocketAddress =  SimpellaClient.simpellaClientServerList.get(i).simpellaClientSocket.getRemoteSocketAddress().toString();
					
					if(temRemoteSocketAddress.equals(remoteSocketAddress))
					{
						//Modified by Amit
						SimpellaClient.simpellaClientServerList.get(i).simpellaClientSocket.getOutputStream().write(message);
						SimpellaClient.simpellaClientServerList.get(i).incrementMsgSent();
					}
				}
			}
			else
			{
				byte[] queryHitmessage = message;
				
				byte[] payloadArray = new byte[4];
				payloadArray[0] = queryHitmessage[19];
				payloadArray[1] = queryHitmessage[20];
				payloadArray[2] = queryHitmessage[21];
				payloadArray[3] = queryHitmessage[22];
				
				/*ByteBuffer bbPayload = ByteBuffer.wrap(payloadArray);
				int payload =  bbPayload.getInt();
				
				byte[] reverseMessageId = new byte[16];
				String reverseMessageIdString = "";
				for(int i=15; i<0; i--)
				{
					reverseMessageId[i] = queryHitmessage[23+payload-1 - (15-i)];
					 
				}
				
				reverseMessageId[15] = queryHitmessage[payload-1];
				reverseMessageId[14] = queryHitmessage[payload-2];
				reverseMessageId[13] = queryHitmessage[payload-3];
				reverseMessageId[12] = queryHitmessage[payload-4];
				reverseMessageId[11] = queryHitmessage[payload-5];
				reverseMessageId[10] = queryHitmessage[payload-6];
				reverseMessageId[9] = queryHitmessage[payload-7];
				reverseMessageId[8] = queryHitmessage[payload-8];
				reverseMessageId[7] = queryHitmessage[payload-9];
				
				for(int i=0; i<reverseMessageId.length; i++)
					reverseMessageIdString = reverseMessageIdString + reverseMessageId[i];
				
				System.out.println("reverseMessageId.length: "+ reverseMessageId.length);
				System.out.println("reverseMessageIdString: "+ reverseMessageIdString);*/
				
				int counter = 22;
				
					byte numberofFile = queryHitmessage[++counter];
					
					byte[] httpPortArray = new byte[2];
					httpPortArray[0] = queryHitmessage[++counter]; 
					httpPortArray[1] = queryHitmessage[++counter];
					ByteBuffer bbhttpPort = ByteBuffer.wrap(httpPortArray);
					short httpPort = bbhttpPort.getShort();
					
					byte[] ipAddressArray = new byte[4];
					ipAddressArray[0] = queryHitmessage[++counter];
					ipAddressArray[1] = queryHitmessage[++counter];
					ipAddressArray[2] = queryHitmessage[++counter];
					ipAddressArray[3] = queryHitmessage[++counter];
					
					byte[] speedArray = new byte[4];
					speedArray[0] = queryHitmessage[++counter];
					speedArray[1] = queryHitmessage[++counter];
					speedArray[2] = queryHitmessage[++counter];
					speedArray[3] = queryHitmessage[++counter];
					ByteBuffer bbSpeed = ByteBuffer.wrap(speedArray);
					
					@SuppressWarnings("unused")
					int speed = bbSpeed.getInt();
					
					for(int i=0; i<numberofFile; i++)
					{
						byte[] fileIndexArray = new byte[4];
						fileIndexArray[0] = queryHitmessage[++counter];
						fileIndexArray[1] = queryHitmessage[++counter];
						fileIndexArray[2] = queryHitmessage[++counter];
						fileIndexArray[3] = queryHitmessage[++counter];
						ByteBuffer bbfileIndex = ByteBuffer.wrap(fileIndexArray);
						int fileIndex = bbfileIndex.getInt();
						//System.out.println("fileIndex: "+fileIndex);
						
						byte[] fileSizeArray = new byte[4];
						fileSizeArray[0] = queryHitmessage[++counter];
						fileSizeArray[1] = queryHitmessage[++counter];
						fileSizeArray[2] = queryHitmessage[++counter];
						fileSizeArray[3] = queryHitmessage[++counter];
						ByteBuffer bbfileSize = ByteBuffer.wrap(fileSizeArray);
						int fileSize = bbfileSize.getInt();
						//System.out.println("fileSize: "+fileSize);
						
						char ch;
						String fileName ="";
						while((ch=(char)queryHitmessage[++counter])!='\0')
							fileName = fileName + ch;
						
						//System.out.println("fileName: "+fileName);
						
						FileInfoFromQH fileInfoFromQH = new FileInfoFromQH(ipAddressArray, httpPort, fileIndex, fileSize, fileName);
						String uniqueFileId = "" + fileInfoFromQH.ipAddressString + httpPort + fileName;
						
						SimpellaVer1.FileInfoFromQHHashMap.put(uniqueFileId, fileInfoFromQH);
						if(SimpellaVer1.tempFileList.size()>300)
							SimpellaVer1.tempFileList.remove(0);
						SimpellaVer1.tempFileList.add(fileInfoFromQH);
						
					}
					
					String remoteUNId = "";
					for(int j=0; j<16; j++)
						remoteUNId = remoteUNId + queryHitmessage[++counter];
					
					//System.out.println("remoteUNId: "+remoteUNId);
					
					
				
				
				
			}
			
			
		} 
		catch (IOException e) 
		{
			
			//e.printStackTrace();
		}
		
		
			
	}
	
	public static void showMyUNI()
	{
		String uNI = "";
		for(int i=0; i<16; i++)
			uNI = uNI + SimpellaVer1.uNI[i];
		
		System.out.println("Unique Node ID: "+ uNI);
	}
	
synchronized	public static void passPong(byte[] message)
	{
		try 
		{
			byte[] messageId = new byte[16];
			String messageIdString = "";
			String remoteSocketAddress;
			
			boolean pingSendByMe = false;
			
			for(int i=0; i<16; i++)
			{
				messageIdString = messageIdString + message[i];
				messageId[i] = message[i];
			}
			
			for (int i =0; i<SimpellaVer1.updateMessageIdList.size(); i++)
				if(Arrays.equals(messageId, SimpellaVer1.updateMessageIdList.get(i)))
					pingSendByMe = true;
				
			
			//System.out.println("passPong received, MessageId: " + messageIdString);
			
			if(pingSendByMe)
			{
				//System.out.println("Adding data received by pong...");
				
				byte[] portArray = new byte[2];
				portArray[0] = message[23];
				portArray[1] = message[24];
				ByteBuffer bbPortArray = ByteBuffer.wrap(portArray);
				short port = bbPortArray.getShort();
				
				byte[] ipAddress = new byte[4];
				ipAddress[0] = message[25];
				ipAddress[1] = message[26];
				ipAddress[2] = message[27];
				ipAddress[3] = message[28];
				
				
				byte[] numberOfFilesSharedArray = new byte[4];
				numberOfFilesSharedArray[0] = message[29]; 
				numberOfFilesSharedArray[1] = message[30];
				numberOfFilesSharedArray[2] = message[31];
				numberOfFilesSharedArray[3] = message[32];
				ByteBuffer bbNumberOfFilesSharedArray = ByteBuffer.wrap(numberOfFilesSharedArray);
				int numberOfFilesShared = bbNumberOfFilesSharedArray.getInt();
				
				byte[] numberOfKilobytesSharedArray = new byte[4];
				numberOfKilobytesSharedArray[0] = message[33];
				numberOfKilobytesSharedArray[1] = message[34];
				numberOfKilobytesSharedArray[2] = message[35];
				numberOfKilobytesSharedArray[3] = message[36];
				ByteBuffer bbNumberOfKilobytesShared = ByteBuffer.wrap(numberOfKilobytesSharedArray);
				int numberOfKilobytesShared = bbNumberOfKilobytesShared.getInt();
				
				Update update = new Update(port, ipAddress, numberOfFilesShared, numberOfKilobytesShared);
				
				//if(SimpellaVer1.infoFromPongsList.containsKey(update.ipAddressString+":"+port))
				{
					//SimpellaVer1.infoFromPongsList.remove(update.ipAddressString+":"+port);
				}
				SimpellaVer1.infoFromPongsList.put(update.ipAddressString+":"+port, update);
				
				//Update test = SimpellaVer1.infoFromPongsList.get(ipAddress);
				//System.out.println("test.numberOfFilesShared: "+test.numberOfFilesShared);
				SimpellaVer1.passPongCounter++;
				
				if(SimpellaVer1.passPongCounter>=2)
					MinTwoConnection.method();
					
					
			}
			else if(message[17]>1)
			{
				//System.out.println("to be forwarded");
				message[17] = (byte)(message[17] -1);
				message[18] = (byte)(message[17] +1);
				for(int i=0; i<SimpellaVer1.messageIdList.size(); i++)
				{
					byte[] tempMessageId = SimpellaVer1.messageIdList.get(i);
					String tempMessageIdString = "";
					//System.out.println("Adding Succesfull...");
					for(int j=0; j<16; j++)
						tempMessageIdString = tempMessageIdString + tempMessageId[j];
					
					//System.out.println("passPong received, tempMessageId: " + tempMessageIdString);
					
					if(messageIdString.equals(tempMessageIdString))
					{
						//System.out.println("no prob...");
						remoteSocketAddress = SimpellaVer1.remoteSocketAddressList.get(i);
						for (int k=0; k< SimpellaServerClient.simpellaServerClientList.size(); k++)
						{
							SimpellaServerClient temp = SimpellaServerClient.simpellaServerClientList.get(k);
							String tempRemoteSocketAddress = temp.clientSocket.getRemoteSocketAddress().toString();
							if (tempRemoteSocketAddress.equals(remoteSocketAddress))
							{
								//System.out.println("hello " + remoteSocketAddress);
								//byte[] pongMessage = SimpellaVer1.pong(messageId);
								temp.clientSocket.getOutputStream().write(message);
								
								//Modified by Amit
								temp.incrementMsgSent();
								temp.incrementBytesSent(getMessagePayloadLenght(message));
								break;
							}								
						}
						
						//SimpellaVer1.print("\nOutgoing Connections\n");
						for (int l=0; l< SimpellaClient.simpellaClientServerList.size(); l++)
						{
							SimpellaClient temp = SimpellaClient.simpellaClientServerList.get(l);
							String tempRemoteSocketAddress = temp.simpellaClientSocket.getRemoteSocketAddress().toString();
							if (tempRemoteSocketAddress.equals(remoteSocketAddress))
							{
								//byte[] pongMessage = SimpellaVer1.pong(messageId);
								temp.simpellaClientSocket.getOutputStream().write(message);
								//System.out.println("hello " + remoteSocketAddress);
								//Modified by Amit
								temp.incrementMsgSent();
								temp.incrementBytesSent(getMessagePayloadLenght(message));
								break;
							}
						}
						
						break;
					}
						
				}//for ends
			}
			
		} 
		catch (IOException e) 
		{
			//e.printStackTrace();
		}
		//System.out.print("Simpella>>");
	}
	
	

	public static void main(String[] args) //throws Exception
	{
		int tcpPort = 0;
		int httpPort = 0;
		
		if (args.length == 0)
		{
			tcpPort = 6346;
			httpPort = 5635;
		}
		else if (args.length == 2)
		{	
			try
			{
				int number1 = Integer.parseInt(args[0]);
				int number2 = Integer.parseInt(args[1]);
				if(SimpellaVer1.IsValidPort(number1) && SimpellaVer1.IsValidPort(number2))
				{
					tcpPort = Integer.parseInt(args[0]);
					httpPort = Integer.parseInt(args[1]);
				}
				else
				{
					throw new NotAValidPortException();
				}
			}//end try
			catch (NumberFormatException e)
			{
				System.out.println("System message: Port Numbers can only be numeric values.");
				System.exit(0);
			}
			catch(NotAValidPortException e)
			{
				System.out.println("System message: "+e.getMessage());
				System.exit(0);
			}
			catch (Exception e)
			{
				System.out.println("System Message: "+e.getMessage());
				System.exit(0);
			}
		}
		else
		{
			try 
			{
				throw new IllegalNumberOfArgumentsException(2);
			} 
			catch (IllegalNumberOfArgumentsException e) 
			{
				System.out.println("System message: Valid input is Simpella <port1> <port2>");
				//exit the program
				System.exit(0);
			}
			
		}
		
		try
		{
			SimpellaServer simpellaServer = new SimpellaServer(tcpPort, httpPort);
			simpellaServer.info();
			SimpellaVer1.serverSocket = simpellaServer.serverSocket;
			//Start web server
			//Modified by Amit
			SimpellaWebServer simpellaWebServer = new SimpellaWebServer(httpPort);
			BufferedReader inFromConsole = new BufferedReader(new InputStreamReader(System.in));
			String command;		
			SimpellaVer1.uNI = SimpellaVer1.generateUNI();
			
			File tempPath = new File(".");
			String tempPathString = tempPath.getAbsolutePath();
			tempPathString = tempPathString.substring(0, tempPathString.length()-1);
			SimpellaVer1.sharedDirectoryPath = new File(tempPathString);
			
			while(true)
			{
				try
				{
				
					System.out.print("Simpella>>");
					command = inFromConsole.readLine();
					
					
					if (command.startsWith("open"))
					{
						command = command.trim();
						if(command.length() ==4)
						{
							System.out.println("Host String missing, please provide host name/address : port");
							System.out.println("Usage: open <hostAddress:port>");
						}
						else
						{
							MethodClass.open(command);
							UpdateMethodClass.update();
						}
						
					}
					//else if (command.equals("update"))
					//Modified by Amit
					else if (command.startsWith("update"))
					{
						command.trim();
						String[] commandArray = command.split("\\s+");
						if (commandArray.length == 1 && commandArray[0].length()==6)
						{
							UpdateMethodClass.update();
						}
						else
						{
							System.out.println("Invalid update usage");
							System.out.println("Usage: Update command do not take any parameters..");
						}
					
					}
					else if (command.startsWith("find"))
					{
						
						if(command.equals("find    "))
						{


							SimpellaVer1.queryHitCounter = 0;
							new Query("allsharedfiles");
						}
							
						
						else
						{
							command = command.trim();
							if(command.length() ==4)
							{
								System.out.println("File Name missing, please provide file name to be searched");
								System.out.println("Usage: find <file name>");
							}
							else
							{
								command = command.substring(5);
								SimpellaVer1.queryHitCounter = 0;
								new Query(command);
							}
						}
					}
					
					else if (command.startsWith("info "))
					{
						//System.out.println("Info called");
						if (command.length()>4)
						{
							String[] commandString = new String[2];
							commandString = command.split("\\s+");						
							//System.out.println("Info Option is:"+commandString[1]);	
							SimpellaVer1.info(commandString[1]);
						}
						else
						{
							System.out.println("Info option is: default");
							simpellaServer.info();
						}
						
						
					}//else if info ends
					else if (command.startsWith("share"))
					{
						command = command.trim();
						if(command.length()==5)
						{
							System.out.println("Directory path needed for sharing");
							System.out.println("Usage: share <DirectoryPath>");
						}
						else
						{
							command = command.substring(6);
							
							if (command.trim().equals("-i"))
							{
								String str = sharedDirectoryPath.getAbsolutePath();
								str = str.substring(0, str.length());
								System.out.println("\tsharing " + str );
							}
							else
							{
								if (command.charAt(0) == '/')
								{
									File temp = new File(command);
									//sharedDirectoryPath = new File(command+"\\.");
									if(temp.isDirectory())
									{
										
										sharedDirectoryPath = temp;
										SimpellaVer1.scan();
									}
									else
									{
										System.out.println("Not a valid absolute path");
									}
								}
								else
								{
									String str = sharedDirectoryPath.getAbsolutePath();
									//System.out.println("in else");
									//System.out.println(str);
									//str = str.substring(0, str.length()-1);
									str = str +"/" +command;
									//System.out.println("str: "+str);
									File temp = new File(str);
									if(temp.isDirectory())
									{
										
										sharedDirectoryPath = temp;
										SimpellaVer1.scan();
									}
									else
									{
										System.out.println("Directory not found in currently shared folder");
									}
									
									/*File[] listOfFiles = sharedDirectoryPath.listFiles();
									int flag = 0;
									for (int i =0; i<listOfFiles.length; i++)
									{
										if(listOfFiles[i].isDirectory() && listOfFiles[i].getName().equals(command))
										{
											flag =1;
											break;
										}
									}//for loop ends
									
									if (flag==1)
									{
										str = str.substring(0, str.length()-2);
										command = str +"\\"+ command + "\\.";
										sharedDirectoryPath = new File(command);
										SimpellaVer1.scan();
									}
									else
									{
										System.out.println("Directory not found");
									}*/
									
								}
							}
						}
						
						
					}// end if share ends
					else if (command.startsWith("scan"))
					{
						command = command.trim();
						if(command.equals("scan"))
						{
							
							SimpellaVer1.scan();
							 
							System.out.println("\tscanning " + sharedDirectoryPath.getAbsolutePath() + " for files ...");
							System.out.println("\tScanned "+ numberOfFiles + " files and "+ dataSharedinBytes+" Bytes");
							 //SimpellaVer1.print("\tScanned "+ count + " files and "+ size+" bytes\n");
						}
						else
						{
							System.out.println("No arguments are allowed/needed with scan.");
							System.out.println("Usage: scan");
						}
						
					}
					else if (command.trim().equals("monitor"))
					{
						new Monitor();
					}					
					//Modified by Amit
					else if (command.startsWith("download"))
					{
						command = command.trim();
						if(command.length() ==8)
						{
							System.out.println("File number missing, please provide file number indicated by list command");
							System.out.println("Usage: download");
						}
						else
						{
							String[] commandString = new String[2];
							 commandString = command.split("\\s+");
							 int fileNum = Integer.parseInt(commandString[1])-1;
							 //System.out.println("File Number: "+fileNum);
							 //Get servent details for file download
							 if(fileNum < tempFileList.size() && fileNum>=0)
							 {
								 FileInfoFromQH fileDetails = tempFileList.get(fileNum);							 
								 InetAddress webServerIP = InetAddress.getByName(fileDetails.ipAddressString);
								 String fileName = fileDetails.fileName;
								 int webServerPort = fileDetails.port;
								 int fileIndex = fileDetails.localIndex;
								 //System.out.println("Call web client with arguments"+webServerIP+" : "+webServerPort+" : "+fileName);
								 new SimpellaWebClient(webServerIP, webServerPort, fileName, fileIndex);
							 }
							 else
							 {
								 System.out.println("File Index out of bound....");
							 }
							 
						}
					}//download end
					else if (command.equals("exit"))
					{
						ExitMethodClass.exit();
					}
					else if (command.equals(""))
					{
						//PressEnter.enterPress = true;
					}
					else if (command.equals("unid"))
					{
						SimpellaVer1.showMyUNI();
					}
					else if (command.equals("test"))
					{
						Iterator<String> iter = SimpellaVer1.infoFromPongsList.keySet().iterator();
						
						while(iter.hasNext())
						{
							String key = (String)iter.next();
							Update value = (Update)SimpellaVer1.infoFromPongsList.get(key);
							
							System.out.println("IP Address: "+value.ipAddressString+"\tPort: "+ value.port+"\tNumberOfFiles: "+value.numberOfFilesShared+"\tNumberOfKiloBytes: "+value.numberOfKilobytesShared);
						}
						
						
						for(int i=0; i<SimpellaVer1.messageIdList.size(); i++)
						{
							String messageIdString = "";
							for(int j =0; j<16; j++)
							{
								messageIdString = messageIdString + SimpellaVer1.messageIdList.get(i)[j];
							}
								
							System.out.println("messageIdString: "+messageIdString);
						}
					}
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				
					else if (command.trim().equals("show"))
					{
						System.out.println("Outgoing: ");
						for(int i=0; i<SimpellaClient.simpellaClientServerList.size(); i++)
						{
							System.out.println(SimpellaClient.simpellaClientServerList.get(i).simpellaClientSocket.getRemoteSocketAddress().toString());
						}
						System.out.println("\nIncomming");
						for(int i=0; i<SimpellaServerClient.simpellaServerClientList.size(); i++)
						{
							System.out.println(SimpellaServerClient.simpellaServerClientList.get(i).clientSocket.getRemoteSocketAddress().toString());
						}
					}
					else if(command.trim().equals("help"))
					{
						String helpString = "\n>>info [cdhnqs] - Display list of current connections. The letters are:\n";
						helpString = helpString + "\t-- c -Simpella network connections\n";
						helpString = helpString + "\t-- d -file transfer in progress (downloads only)\n";
						helpString = helpString + "\t-- h -number of hosts, number of files they are sharing,\n\t   and total size of those shared files\n";
						helpString = helpString + "\t   (routing tables), total Simpella bytes received and sent so far.\n";
						helpString = helpString + "\t-- q -queries received and replies sent\n";
						helpString = helpString + "\t-- q -queries received and replies sent\n";
						helpString = helpString + "\n>>share [dir | -i]\n";
						helpString = helpString + "\t-- dir - Specify the directory whose files are shared.\n";
						helpString = helpString + "\t-- -i - Shows the current shared directory.\n";
						helpString = helpString + "\n>>scan - Scan the shared directory for files' information.\n";
						helpString = helpString + "\n>>open <host:port> - Open a connection to host at port. \n\tThe field <host> could either be a host name or a host IP. \n";
						helpString = helpString + "\n>>update - Send PINGs to all neighbors.\n";
						helpString = helpString + "\n>>find <string> - Searches for files containing words in the string\n";
						helpString = helpString + "\n>>list - List files returned by find.\n";
						helpString = helpString + "\n>>clear [fileNumber] - Clear file from list.\n";
						helpString = helpString + "\t-- (no arguments) - Clear all file entries from list.\n";
						helpString = helpString + "\t-- fileNumber - Clear file whose number is fileNumber from the list.\n";
						helpString = helpString + "\n>>download <fileNumber> - Start downloading the file specified.\n";
						helpString = helpString + "\n>>monitor - Display the queries people are searching for.\n";
						
						System.out.println(helpString);
						System.out.println("Note: All the commands are case sensitive, e.g.: Open and open are not same");
					}// end help command
					else if(command.trim().equals("list"))
					{
						for(int i=0; i<SimpellaVer1.tempFileList.size(); i++)
						{
							FileInfoFromQH fileInfoFromQH = SimpellaVer1.tempFileList.get(i);
							
							String line1 = (i+1) +"\t" +fileInfoFromQH.ipAddressString+":"+fileInfoFromQH.port;
							line1 = line1 + "\tSize: " + fileInfoFromQH.fileSize;
							
							String line2 = "\tName: " + fileInfoFromQH.fileName +"\n";
							
							System.out.println(line1);
							System.out.println(line2);
							
						}
					}
					else if(command.trim().startsWith("clear"))
					{
						if(command.trim().equals("clear"))
						{
							SimpellaVer1.tempFileList.clear();
						}
						else
						{
							command = command.trim().substring(6);
							
							try
							{
								int index = Integer.parseInt(command);
								index--;
								SimpellaVer1.tempFileList.remove(index);
							}
							catch(NumberFormatException e)
							{
								System.out.println("Illegal file index number");
								System.out.println("Usage: clear <Valid File Index Number>");
							}
							catch (Exception e) 
							{
								System.out.println("Illegal file index number");
								System.out.println("Usage: clear <Valid File Index Number>");
							}
						}
					}
					else if (command.trim().equals("cls"))
					{
						for(int i=0; i<100; i++)
							System.out.println("\n");
					}
					else 
					{
						//SimpellaVer1.print("Not a valid command\n");
						System.out.println("Unknown command: "+ command);
						System.out.println("type 'help' to get details of valid commands and usage");
					}
					
				} // end of try block
				//catch exceptions
				/*catch (IllegalNumberOfArgumentsException e)
				{
					System.out.println("Illegal number of command arguments");
					System.out.println("system message"+e.getMessage());
				}*/
				catch(IllegalArgumentsException e)
				{
					//System.out.println("Illegal Command arguments");
					System.out.println("System message: "+e.getMessage());
				}
				catch (NotAValidPortException e)
				{
					// TODO: handle exception
					System.out.println("System message: "+e.getMessage());
				}
				catch (NumberFormatException e) 
				{
					// TODO: handle exception
					System.out.println("System message: "+e.getMessage());
				}
				catch (IOException e) 
				{
					// TODO: handle exception
					//System.out.println("System message: "+e.getMessage());
					
				}
				
				
				
			}// while ends
		}//end outer try
		
		catch(IOException e)
		{
			System.out.println("System message: "+e.getMessage());
		}
		catch (Exception e) 
		{
			System.out.println("System message: "+e.getMessage());
		}
		finally
		{
			ExitMethodClass.exit();
		}

	}//main method ends
	
	public static void print(String string)
	{
		System.out.print(string);
	}
	
	//Modified by Amit
	public static int getMessagePayloadLenght(byte[] message)
	{
		byte[] payloadArray = new byte[4];
		payloadArray[0] = message[19];
		payloadArray[1] = message[20];
		payloadArray[2] = message[21];
		payloadArray[3] = message[22];
		
		ByteBuffer bbPayloadArray = ByteBuffer.wrap(payloadArray) ;
		int msgPayload = bbPayloadArray.getInt();
		return msgPayload;
	}
	
	public static boolean IsValidPort(int portnumber)
	{
		if (portnumber<=1023 || portnumber>=65536)
				return false;
		
		return true;
	}

}// class ends


@SuppressWarnings("serial")
class IllegalNumberOfArgumentsException extends Exception
{

	public IllegalNumberOfArgumentsException(int number)
	{
		super (number + " arguments expected");
	}
	
}

@SuppressWarnings("serial")
class IllegalArgumentsException extends Exception
{
	public IllegalArgumentsException()
	{
		super ("Argument(s) are not valid");
	}
}

@SuppressWarnings("serial")
class NotAValidPortException extends Exception
{
	public NotAValidPortException()
	{
		super("Valid port is a number ranging between 1024 to 65535(Inclusive)\n");
	}
}
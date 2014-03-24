public class MethodClass 
{

	static void open(String command) throws IllegalArgumentsException, NotAValidPortException
	{
		if(SimpellaClient.simpellaClientServerList.size()<3)
		{
			command = command.substring(5, command.length());
			String[] commandString = command.split(":");
						
			if(commandString.length == 2 )
			{
				
				int tempPort; 
				
				try
				{
					tempPort = Integer.parseInt(commandString[1]);
				}
				catch (NumberFormatException e)
				{
					throw new NotAValidPortException();
				}
				
				boolean isValidPort = SimpellaVer1.IsValidPort(tempPort);
				if(!isValidPort)
					throw new NotAValidPortException();
				
				byte[] tempHostIp = SimpellaServer.hostAddress.getAddress();
				String tempHostIpString = "";
				int a = tempHostIp[0] & 0xff;
				int b = tempHostIp[1] & 0xff;
				int c = tempHostIp[2] & 0xff;
				int d = tempHostIp[3] & 0xff;
				
				tempHostIpString = a+"."+b+"."+c+"."+d;
				//System.out.println("commandString[0]: "+commandString[0]);
				//System.out.println("tempHostIpString: "+ tempHostIpString);
				//System.out.println("tempPort :"+tempPort);
				//System.out.println("SimpellaServer.tcpPort :"+SimpellaServer.tcpPort);
				
				boolean isDuplicateConnection = false;
				
				String connectionString = "/"+commandString[0]+":"+commandString[1];
				
				for(int i=0; i<SimpellaClient.simpellaClientServerList.size(); i++)
				{
					SimpellaClient simpellaClient = SimpellaClient.simpellaClientServerList.get(i);
					String remoteSocketAddressString = simpellaClient.remoteSocketAddressString;
					
					//String[] remoteSocketAddressStringArray = remoteSocketAddressString.split("/");
					
					if(connectionString.equals(remoteSocketAddressString))
						isDuplicateConnection = true;
					
					if(remoteSocketAddressString.contains(commandString[0]) &&  remoteSocketAddressString.contains(commandString[1]) )
						isDuplicateConnection = true;
				}
				
				/*String SocketAddressString = "/" + tempHostIpString + ":" + tempPort;
				
				for(int i=0; i<SimpellaClient.simpellaClientServerList.size(); i++)
				{
					SimpellaClient tempSimpellaClient = SimpellaClient.simpellaClientServerList.get(i);
					String tempRemoteSocketAddress = tempSimpellaClient.simpellaClientSocket.getInetAddress().toString();
					tempRemoteSocketAddress = tempRemoteSocketAddress + ":" + tempSimpellaClient.simpellaClientSocket.getPort();
					if(SocketAddressString.contains(tempRemoteSocketAddress) || tempRemoteSocketAddress.contains(SocketAddressString))
						isDuplicateConnection = true;
				}*/
				
				if ((tempHostIpString.equals(commandString[0]) || SimpellaServer.hostName.equalsIgnoreCase(commandString[0]))&& tempPort==SimpellaServer.tcpPort)
				{
					System.out.println("Self connection is not allowed.");
				}
				else if (isDuplicateConnection)
				{
					System.out.println("Duplicate connection not allowed.");
				}
				else
				{
					new SimpellaClient(commandString[0], Integer.parseInt(commandString[1]));
				}
			}
			else
			{
				System.out.println("Usage: open <hostAddress:port>");
				throw new IllegalArgumentsException();
			}
		}
		else if(SimpellaClient.simpellaClientServerList.size()==3)
		{
			System.out.println("No more out going connection are allowed. Maximum limit reached.");
		}
		
	
	}
}

import java.util.Iterator;

public class MinTwoConnection extends Thread 
{
	public MinTwoConnection()
	{
		
		//start();
	}
	
	
synchronized	public static void method()
	{
		/*try {
			sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		int size = SimpellaClient.simpellaClientServerList.size() + SimpellaServerClient.simpellaServerClientList.size();
		if(size < 2)
		{
			//for(int i=0; i<SimpellaVer1.infoFromPongsList.size(); i++)
			Iterator<String> iter = SimpellaVer1.infoFromPongsList.keySet().iterator();
			
			while(iter.hasNext())
			{
				String key = iter.next();
				boolean isPresent = false;
				Update tempUpdate = SimpellaVer1.infoFromPongsList.get(key);
				String updateRemoteSocketAddressString = "/" + tempUpdate.ipAddressString + ":" + tempUpdate.port;
				
				if(!tempUpdate.visited)
				{
					for(int j=0; j<SimpellaClient.simpellaClientServerList.size(); j++)
					{
						if(tempUpdate!=null && SimpellaClient.simpellaClientServerList.get(j).remoteSocketAddressString.equals(updateRemoteSocketAddressString))
						{
							tempUpdate.visited=true;
							isPresent = true;
						}
					}
						
					//for(int j =0; j<SimpellaServerClient.simpellaServerClientList.size(); j++)
						//if(tempUpdate!=null && SimpellaServerClient.simpellaServerClientList.get(j).remoteSocketAddressString.equals(updateRemoteSocketAddressString))
							//tempUpdate.visited=true;//isPresent = true;
					
					//if(!isPresent && tempUpdate.visited== false)
					if(!isPresent)
					{
						try 
						{
							tempUpdate.visited = true;
							System.out.println("To maintain atleast 2 connections at a time, trying to auto connected...");
							System.out.println("open "+updateRemoteSocketAddressString.substring(1));
							MethodClass.open("open "+updateRemoteSocketAddressString.substring(1));
							sleep(1000);
							System.out.print("Simpella>>");
						}
						catch (IllegalArgumentsException | NotAValidPortException | InterruptedException e) 
						{
							//e.printStackTrace();
						}
					}
				}
				
				
			}
		}//if size<2 ends
	}
	
	public void run()
	{
		while(true)
		{
			int size = SimpellaClient.simpellaClientServerList.size() + SimpellaServerClient.simpellaServerClientList.size();
			if(size < 2)
			{
				//for(int i=0; i<SimpellaVer1.infoFromPongsList.size(); i++)
				Iterator<String> iter = SimpellaVer1.infoFromPongsList.keySet().iterator();
				
				while(iter.hasNext())
				{
					String key = iter.next();
					boolean isPresent = false;
					Update tempUpdate = SimpellaVer1.infoFromPongsList.get(key);
					String updateRemoteSocketAddressString = "/" + tempUpdate.ipAddressString + ":" + tempUpdate.port;
					









					if(!tempUpdate.visited)
					{

						for(int j=0; j<SimpellaClient.simpellaClientServerList.size(); j++)
						{




							if(tempUpdate!=null && SimpellaClient.simpellaClientServerList.get(j).remoteSocketAddressString.equals(updateRemoteSocketAddressString))
							{
								tempUpdate.visited=true;
								isPresent = true;
							}
						}

							
						//for(int j =0; j<SimpellaServerClient.simpellaServerClientList.size(); j++)
							//if(tempUpdate!=null && SimpellaServerClient.simpellaServerClientList.get(j).remoteSocketAddressString.equals(updateRemoteSocketAddressString))
								//tempUpdate.visited=true;//isPresent = true;
						
						//if(!isPresent && tempUpdate.visited== false)
						if(!isPresent)
						{
							try 
							{
								tempUpdate.visited = true;
								System.out.println("To maintain atleast 2 connections at a time, trying to auto connected...");
								System.out.println("open "+updateRemoteSocketAddressString.substring(1));
								MethodClass.open("open "+updateRemoteSocketAddressString.substring(1));
								sleep(1000);
								System.out.print("Simpella>>");
							}
							catch (IllegalArgumentsException | NotAValidPortException | InterruptedException e) 
							{
								//e.printStackTrace();
							}
						}
					}
					
					
				}


			}//if size<2 ends
		}//while(true) ends
	}
}

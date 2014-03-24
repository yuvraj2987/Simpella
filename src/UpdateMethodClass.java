import java.util.Arrays;
import java.util.Random;

public class UpdateMethodClass 
{
	static public void update()
	{
		byte[] pingMessage = new byte[4096];
		byte[] messageId = new byte[16];
		
		Random random = new Random();
		random.nextBytes(messageId);
		
		for(int i=0; i<messageId.length; i++)
			pingMessage[i] = messageId[i];
		
		pingMessage[8]  = (byte)0xff;
		pingMessage[15] = (byte)0;
		
		pingMessage[16] = (byte)0;//Message Type Ping
		pingMessage[17] = (byte)7;//TTL = 7
		pingMessage[18] = (byte)0;//Hops = 0 initially
		pingMessage[19] = (byte)0;//PayLoad length
		pingMessage[20] = (byte)0;
		pingMessage[21] = (byte)0;
		pingMessage[22] = (byte)0;
		
		//for(int i=0; i<pingMessage.length; i++)
			//System.out.println("pingMessage["+i+"]=" +pingMessage[i]);
		
		/*String str;
		
		
		while(true)
		{
			str = Math.random()+"";
			str = str.substring(2);
			if(str.length()>=15)
				break;
		}
		
		
		for (int i=0; i<15; i++)
		{
			if(i==8)
				pingMessage[8]  = (byte)255;
			else
				pingMessage[i] = (byte)Byte.parseByte(str.charAt(i)+"");
		}
		
		str = "";
		for(int i =0; i<pingMessage.length; i++)
			str = str + pingMessage[i];
		
		str = str+"\n";*/
		
		
		/*ArrayList<SimpellaServerClient> simpellaServerClientList = SimpellaServerClient.simpellaServerClientList;
		ArrayList<SimpellaClient> simpellaClientServerList = SimpellaClient.simpellaClientServerList;
		
		//SimpellaVer1.print("Incoming Connections\n");
		for (int i=0; i< simpellaServerClientList.size(); i++)
		{
			SimpellaServerClient temp = simpellaServerClientList.get(i);
			temp.clientSocket.getOutputStream().write(pingMessage);
		}
		
		//SimpellaVer1.print("\nOutgoing Connections\n");
		for (int i=0; i< simpellaClientServerList.size(); i++)
		{
			SimpellaClient temp = simpellaClientServerList.get(i);
			temp.simpellaClientSocket.getOutputStream().write(pingMessage);
		}*/
		
		SimpellaVer1.passToAll(pingMessage);
		
		messageId = Arrays.copyOfRange(pingMessage, 0, 16);
		SimpellaVer1.updateMessageIdList.add(messageId);

		/*System.out.println("messageId length: "+ messageId.length);
		for(int i =0; i<16; i++)
		{
			
			System.out.println(messageId[i]+ "      " + pingMessage[i]);
		}*/
	}

}

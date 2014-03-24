import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class Query extends Thread
{
	public Query(String command)
	{
		byte[] queryMessage = new byte[4096];
		byte[] message = new byte[23];
		Random random = new Random();
		random.nextBytes(message);
		
		message[8]  = (byte)0xff;
		message[15] = (byte)0;
		message[16] = (byte)0x80;//Message Type Query
//		System.out.println(message[16]);
		message[17] = (byte)7;//TTL = 7
		
		if(command.equals("allsharedfiles"))
			message[17] = (byte)1;
		message[18] = (byte)0;//Hops = 0 initially
		/*message[19] = (byte)0;//PayLoad length
		message[20] = (byte)0;
		message[21] = (byte)0;
		message[22] = (byte)0;*/
		
		queryMessage[23] = (byte)0;
		queryMessage[24] = (byte)0;
		byte[] searchString = command.getBytes();
		for (int i=0; i<searchString.length; i++)
			queryMessage[25+i] = searchString[i];
		
		int payloadLength = searchString.length+2;
		ByteBuffer bbpayloadLength = ByteBuffer.allocate(4);
		bbpayloadLength.putInt(payloadLength);
		byte[] payloadLengthArray = bbpayloadLength.array();
		message[19] = payloadLengthArray[0];
		message[20] = payloadLengthArray[1];
		message[21] = payloadLengthArray[2];
		message[22] = payloadLengthArray[3];
		
		byte[] nullCharString = "\0".getBytes();
		queryMessage[25+searchString.length] = nullCharString[0];
		
		for (int i=0; i<message.length; i++)
			queryMessage[i] = message[i];
		
		byte[] messageId = new byte[16];
		messageId = Arrays.copyOfRange(queryMessage, 0, 16);
		
		//System.out.println("newy created query messageId.length: "+messageId.length);
		SimpellaVer1.querySendByMe.add(messageId);
		
		int previousfileListSize = SimpellaVer1.tempFileList.size();
		
		SimpellaVer1.passToAll(queryMessage);
		
		//start();
		
		System.out.println("Searching Simpella Network for '"+command+"'");
		new PressEnter();
		System.out.println("Press Enter to continue");
		
		while(!PressEnter.enterPress)
		{
//			
			//try
			{
				//System.out.println("in while loop");
				//if(PressEnter.enterPress)
					//break;
								
				System.out.print("\r" + SimpellaVer1.queryHitCounter + "  response(s) received");
				
			}
			//catch(IOException e)
			{
				//System.out.println(e.getMessage());
			}
		}
		PressEnter.enterPress = false;
		//System.out.println("\n\n");
		//pressEnter.getThreadGroup().destroy();
		System.out.println("\n--------------------------------");
		
		
		
		/*if(SimpellaVer1.FileInfoFromQHHashMap.size()>0)
		{
			System.out.println("The query was '"+ command +"'");
			System.out.println("localIndex"+"\t"+"ipAddressString"+"\t"+"port"+"\tfileName");
			Iterator<String> iter= SimpellaVer1.FileInfoFromQHHashMap.keySet().iterator();
			
			while(iter.hasNext())
			{
				String key = iter.next();
				FileInfoFromQH temp = SimpellaVer1.FileInfoFromQHHashMap.get(key);
				
				System.out.println(temp.localIndex+"\t"+temp.ipAddressString+"\t"+temp.port+"\t"+temp.fileName);
				
			}
		}*/
		
		//if(SimpellaVer1.tempFileList.size()>0)
		{
			//System.out.println("");
			for(int i=previousfileListSize; i<SimpellaVer1.tempFileList.size(); i++)
			{
				FileInfoFromQH fileInfoFromQH = SimpellaVer1.tempFileList.get(i);
				
				String line1 = (i+1) +"\t" +fileInfoFromQH.ipAddressString+":"+fileInfoFromQH.port;
				line1 = line1 + "\tSize: " + fileInfoFromQH.fileSize;
				
				String line2 = "\tName: " + fileInfoFromQH.fileName +"\n";
				
				System.out.println(line1);
				System.out.println(line2);
				
			}
		}
			
		
		
	}
	
	/*public void run()
	{
		System.out.println("Searching...");
		new PressEnter();
		System.out.println("Press Enter to exit");
		
		while(true)
		{
//			
			//try
			{
				//System.out.println("in while loop");
				if(PressEnter.enterPress)
					break;
								
				System.out.print(SimpellaVer1.queryHitCounter + "  QueryHit received\r");
				
			}
			//catch(IOException e)
			{
				//System.out.println(e.getMessage());
			}
		}
		PressEnter.enterPress = false;
		System.out.println("\n\n");
		//pressEnter.getThreadGroup().destroy();
	}*/
}
import java.io.IOException;

public class ExitMethodClass 
{
	public static void exit()
	{
		byte[] message = new byte[4096];
		message[16] = (byte)2;
		message[17] = (byte)1;
		message[18] = (byte)0;
		
		SimpellaVer1.passToAll(message);
		
		System.exit(0);
	}
}

public class Update 
{
	short port;
	byte[] ipAddress;
	int numberOfFilesShared;
	int numberOfKilobytesShared;
	String ipAddressString;
	boolean visited;
	
	public Update(short port, byte[] ipAddress, int numberOfFilesShared, int numberOfKilobytesShared)
	{
		this.port = port;
		this.ipAddress = ipAddress;
		this.numberOfFilesShared = numberOfFilesShared;
		this.numberOfKilobytesShared = numberOfKilobytesShared;
		
		int a = ipAddress[0] & 0xff;
		int b = ipAddress[1] & 0xff;
		int c = ipAddress[2] & 0xff;
		int d = ipAddress[3] & 0xff;
		
		ipAddressString = a+"."+b+"."+c+"."+d;
		
		visited = false;
	}
	
}

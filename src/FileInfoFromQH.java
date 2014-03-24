public class FileInfoFromQH 
{
	public String fileName;
	public Integer fileIndex;
	public short port;
	public byte[] ipAddress;
	public int fileSize;
	public String ipAddressString;
	public int localIndex;
	
	static int counter = 1;
	
	public FileInfoFromQH(byte[] ipAddress, short port, Integer fileIndex, int fileSize, String fileName)
	{
		this.ipAddress = ipAddress;
		this.port = port;
		this.fileIndex = fileIndex;
		this.fileSize = fileSize;
		this.fileName = fileName;
		
		localIndex = counter;
		counter++;
		
		int a = ipAddress[0] & 0xff;
		int b = ipAddress[1] & 0xff;
		int c = ipAddress[2] & 0xff;
		int d = ipAddress[3] & 0xff;
		
		ipAddressString = a+"."+b+"."+c+"."+d;
	}
	
}

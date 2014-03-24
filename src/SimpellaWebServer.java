

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class SimpellaWebServer extends Thread
{
	//Server class private members
	int httpPort;
	ServerSocket webListner;
	//Constructor
	public SimpellaWebServer(int port)
	{
		try
		{
			httpPort = port;
			webListner = new ServerSocket(httpPort);
		}
		catch (IOException e)
		{
			System.out.println("Error while creating Web Server Socket on port:"+httpPort);
			System.out.println(e.getMessage());
		}
		//Start server Thread
		start();
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				//Create a new connection Socket per request
				Socket connection = webListner.accept();
				//Associate input/output stream with the channel
				BufferedReader inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				ObjectOutputStream outStream = new ObjectOutputStream(connection.getOutputStream());
				
				String message;
				while((message = inStream.readLine())!= null)
				{
					//System.out.println("client message: "+message);
					//Divide each HTTP message into Tokens
					StringTokenizer messageToken = new StringTokenizer(message);
					if((messageToken.hasMoreTokens()) && (messageToken.nextToken().equals("GET")))
					{
						String fileHeader = messageToken.nextToken();
						System.out.println("File Headers: "+fileHeader);
						//Partially Split the header to get file path
						String[] splitFileHeader =  fileHeader.split("/", 4);
						/*
						for(int i=0; i<splitFileHeader.length;i++)
						{
							System.out.println("index: "+i+"value: "+splitFileHeader[i]);
						}*/
						//last element is
						String fileName = splitFileHeader[splitFileHeader.length-1];
						//System.out.println("File Path on Server: "+filePath);
						//split entire header to get file name
						/*
						splitFileHeader = fileHeader.split("/");
						String fileName = splitFileHeader[splitFileHeader.length-1];*/
						
						//System.out.println("File Name: "+fileName);
						//check if file exists
						//System.out.println("Simpella share directory"+SimpellaVer1.sharedDirectoryPath.getAbsolutePath());
						//System.out.println("File name "+fileName);
						String directoryPath = SimpellaVer1.sharedDirectoryPath.getAbsolutePath();
						directoryPath = directoryPath.substring(0, directoryPath.length()-1);	
						String filePath = directoryPath+fileName; 
						//System.out.println("Check if file "+ filePath +" exists");
						File file = new File(filePath);
						String responseMessage;
						if(file.exists())
						{
							//System.out.println("File exists");
							long numOfBytes =  file.length();
							responseMessage = "HTTP/1.1 200 Document Follows\r\n";
							responseMessage = responseMessage+"Server: Simpella0.6\r\n";
							responseMessage = responseMessage+"Content-type: application/binary\r\n";
							responseMessage = responseMessage+"Content-length:"+numOfBytes+"\r\n";
							responseMessage = responseMessage+"\r\n";
						}
						else
						{
							System.out.println("File does not exists");
							responseMessage = "HTTP/1.1 503 File Not Found\r\n";
							responseMessage = responseMessage+"\r\n";
							
						}
						//System.out.println("Send responce message to client: "+ responseMessage);
						//send HTTP response message						
						outStream.writeObject(responseMessage);
						sleep(10);
						//System.out.println("Sending file.");
						sendFile(filePath, outStream);
					}
				}// end of read while
				
				
				//System.out.println("Done close this server socket");
				//closing
				inStream.close();
				connection.close();
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
			} catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}//End infinite while
		
	}//End (Thread)run method
	
	private static void sendFile(String file, ObjectOutputStream out) throws IOException
	{
		FileInputStream fileIn = new FileInputStream(file);
		byte[] buf = new byte[1024];
		int bytesread = 0, bytesBuffered = 0;
		while((bytesread=fileIn.read(buf))>-1)
		{
			out.write(buf, 0, bytesread);
			bytesBuffered += bytesread;
			
			//flush after 1MB write
			if (bytesBuffered > 1024 * 1024)
			{
				bytesBuffered = 0;
		        out.flush();
			}
		}//end file read in bytes while
		
		//end flush
		fileIn.close();
		out.flush();
		
	}//End of sendFile method
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		System.out.println("Simpella Web Server");
		new SimpellaWebServer(6790);
	}

}

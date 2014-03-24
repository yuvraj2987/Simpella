

import java.io.*;
import java.net.*;
//import java.util.StringTokenizer;
import java.util.ArrayList;

public class SimpellaWebClient extends Thread
{
	Socket webClient;
	InetAddress webServer;
	int serverPort;
	String filePath;
	String downloadFolder;
	int fileIndex;
	long readFileBytes;
	long totalFileBytes;
	
	static ArrayList<SimpellaWebClient> simpellaWebClientsList = new ArrayList<SimpellaWebClient>();
	
	//constructor
	public SimpellaWebClient (InetAddress server, int port, String file, int index )
	{
		webServer = server;
		serverPort = port;
		
		filePath = file;
		downloadFolder = SimpellaVer1.sharedDirectoryPath.getAbsolutePath();
		downloadFolder = downloadFolder.substring(0,downloadFolder.length()-1);
		fileIndex = index;
		
		
		try
		{
			webClient = new Socket(webServer, serverPort);
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		//Start the Thread
		start();
	}
	
	public void run()
	{
		try
		{
			//Create input/output streams 
			//add this instance to the arraylist
			simpellaWebClientsList.add(this);
			ObjectInputStream inFromServer = new ObjectInputStream(webClient.getInputStream());
			PrintWriter outFromClient = new PrintWriter(webClient.getOutputStream(), true);
			
			//Dst file path
			String [] filePathArr = filePath.split("/");
			String fileName = filePathArr[filePathArr.length - 1];
			//System.out.println("File Name is: "+fileName);
			String fileDstPath = downloadFolder+fileName;
			//System.out.println("File dst path"+fileDstPath);
			//HTTP request messages
			String message = "GET /get/"+fileIndex+"/"+filePath+" HTTP/1.1\r\n";
			//outFromClient.println(message);
			message = message+"User-Agent: Simpella\r\n";
			//outFromClient.println(message);
			message = message+"Host: "+webClient.getLocalAddress()+":"+webClient.getLocalPort()+"\r\n";
			//outFromClient.println(message);
			message = message+"Connection: Keep-Alive\r\n";
			//outFromClient.println(message);
			message = message+"Range: bytes=0-\r\n";
			//outFromClient.println(message);
			message = message+"\r\n";
			outFromClient.println(message);
			//HTTP request message ends
			
			//System.out.println("Waiting from server response");
			//String responseMessage = (String) inFromServer.readObject();
			String responseMessage = (String) inFromServer.readObject();
			//System.out.println("Server response: "+responseMessage+"Message lenght: "+responseMessage.length());
			String[] responseMessageArray = responseMessage.split("\\n");
			for(int i=0; i<responseMessageArray.length;i++)
			{
				if(responseMessageArray[i].contains("Content-length"))
				{
					String[] contentLenghtArray = responseMessageArray[i].split(":");
					totalFileBytes = Long.parseLong(contentLenghtArray[1].trim());
					//System.out.println("Total file size "+totalFileBytes);
				}
			}
			//Exception is given file is not present in the server
			if(responseMessage.contains("503 File Not Found"))			
				throw new FileNotFoundException();
			
			//To Do: Read file size content from the response message
			
			

			//System.out.println("Wait for file");
			getFile(inFromServer, fileDstPath);
			//System.out.println("File downloaded exit");
			
			//closing
			outFromClient.close();
			webClient.close();
			
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found "+e.getMessage());
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			
			System.out.println(e.getMessage());
		}
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		finally
		{
			//Remove this instance 
			simpellaWebClientsList.remove(this);
		}
		
		
	}//End of thread run method
	
	private void getFile(ObjectInputStream in, String file) throws ClassNotFoundException, IOException, InterruptedException
	{
		//read from socket input stream		
		//System.out.println("Get file called");
		byte[] byteArray = new byte[1024];
		//Save incoming file - text, audio, jpg - media		
		FileOutputStream fileStream = new FileOutputStream(file);
		int bytesRead;
		while((bytesRead =in.read(byteArray))>-1)
		{
			//write bytearray to file
			this.readFileBytes+=bytesRead;
			//System.out.println("Read file bytes "+readFileBytes);
			fileStream.write(byteArray);
			//System.out.println("1 KB read sleep for 3s");
			sleep(100);
			
		}
		fileStream.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		
		System.out.println("Simepella Web Cliet Starts");
		try
		{
			InetAddress localServer = InetAddress.getLocalHost();
			int httpPort = 6790;
			String fileName = "Wildlife.wmv";
			//String fileName = "D:/study/Masters_Courses/573-cvip/Project/Project Image_Butterfly.jpg";
			System.out.println("Starting WebClinet");
			//new SimpellaWebClient(localServer, httpPort, fileName);
			System.out.println("Web Client Program ends");
		}
		catch (UnknownHostException ex)
		{
			System.out.println(ex.getMessage());
		}

	}

}

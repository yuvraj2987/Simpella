

import java.io.Console;

public class PressEnter extends Thread
{
	static boolean enterPress = false;
	public PressEnter()
	{
		start();
	}
	
	public void run()
	{
		Console con = System.console();
		con.readLine();
		enterPress = true;
		//System.out.println("in PressEnterRun()");
	}
}
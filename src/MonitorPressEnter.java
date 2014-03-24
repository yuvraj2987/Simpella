

import java.io.Console;

public class MonitorPressEnter extends Thread
{
	static boolean monitorEnterPress = false;
	public MonitorPressEnter()
	{
		start();
	}
	
	public void run()
	{
		Console con = System.console();
		con.readLine();
		MonitorPressEnter.monitorEnterPress = true;
		//System.out.println("in MonitorPressEnterRun()");
	}
}

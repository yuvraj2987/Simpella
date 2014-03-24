public class Monitor extends Thread
{
	public static String monitorString = "";
	public static boolean isAlive = false;
	public static String monitorMessageIdString="";
	
	public Monitor()
	{
		//start();
		isAlive = true;
		String previousString = "";
		String perviousMessageIdString = "";
		monitorString = "";
		int count =0;
		new MonitorPressEnter();
		String showString = "\nMONITORING SIMPELLA NETWORK:\nPress enter to continue\n----------------------------";
		System.out.println(showString);
		while(!MonitorPressEnter.monitorEnterPress)
		{
			//if(monitorString.equals(""))
			//
			if(previousString.equalsIgnoreCase(monitorString)  && perviousMessageIdString.equals(monitorMessageIdString))
			{
				System.out.print("\r  ");
			}
			else
			{
				count++;
				System.out.println("\rSearch: " +"'"+ monitorString+"'");
				previousString = monitorString;
				perviousMessageIdString = monitorMessageIdString;
			}
		}
		isAlive = false;
		MonitorPressEnter.monitorEnterPress = false;
		System.out.println("\renter Pressed...");
		previousString = "";
		perviousMessageIdString = "";
		monitorMessageIdString = "";
		monitorString = "";
	}
	
	/*public void run()
	{
		isAlive = true;
		String previousString = "";
		int count =0;
		while(!MonitorPressEnter.monitorEnterPress)
		{
			if(previousString.equalsIgnoreCase(monitorString))
			{
				
			}
			else
			{
				count++;
				System.out.println("Monitor: " + count +  "  " +monitorString);
				previousString = monitorString;
			}
		}
		isAlive = false;
	}*/
}
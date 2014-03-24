Simpella
------------------------------------------------------------
------------------------------------------------------------
Command : java SimpellaVer1 <port1> <port2>
------------------------------------------------------------
Valid user input: 
java SimpellaVer1 <port1> <port2>
java SimpellaVer1

Exceptions handeled:: Corrosponding Response from Servent
java SimpellaVer1 string1 string2 :: "System message: Port Numbers can only be numeric values."
java SimpellaVer1 1024 :: System message: Valid input is Simpella <port1> <port2>
java SimpellaVer1 1023 1024 :: System message: Valid port is a number ranging between 1024 to 65535(Inclusive)

------------------------------------------------------------
Basic Commands: (Minimum Requirement : JDK 1.6)

1. info [cdhnqs] - Display list of current connections. The letters are:
	-- c -Simpella network connections 
	-- d -file transfer in progress (downloads only) 
	-- h -number of hosts, number of files they are sharing, and total size of those shared files 
	-- n -Simpella statistics: packets received and sent, number of unique packet IDs in memory 
		  (routing tables), total Simpella bytes received and sent so far. 
	-- q -queries received and replies sent 
	-- s -number and total size of shared files on this host 

2.  share [dir | -i]
3.  scan
4.  open <host:port>
5.  update
6.  find <string>
7.  list
8.  clear [fileNumber|""]
9.  download <fileNumber>
10. monitor


------------------------------------------------------------
Other functianalities:
1. Each servent is trying its best to maintain at least 2 connections at all time. When a node opens a connection, it send 'ping' message
   and in response of that receives pong messages from the other nodes, and immidiately tries to connect to new node (base on the information received form
   pongs), if number of total connections is less than two. Also when any of the neighbour quits from the network it again tries to make new connection based
   on the information received by the pong(s).

2. Handled invalid input graciously, reports "unknown command" for totally 
   off inputs, reports the command's usage if the input parameters were not expected.
   Shows appropriate message if number of arguments given is not same as expected number of arguments, also give
   appropriate message if String is given as argument where numeric value is expected and vice versa.
   Few examples:
   "Simpella>>open
    Host String missing, please provide host name/address : port
    Usage: open <hostAddress:port>
    Simpella>>"
   
   "Simpella>>share
    Directory path needed for sharing
    Usage: share <DirectoryPath>
    Simpella>>"	
	
   "Simpella>>scan string
    No arguments are allowed/needed with scan.
    Usage: scan
	Simpella>>"
	
   "Simpella>>hello
    Unknown command: hello
    type 'help' to get details of valid commands and usage
	Simpella>>"
	
3. For file downloading: the sender write blocks of data of size 1K each.

4. Program respond in a nice way (tells user what's happened) when any of the neighbou quits the network or when the total 
   number of outgoing connections or incoming connections have reached its limit.

5. Download file to the shared directory too.

6. Extra Commands Implemented:
	exit : To exit, this will send exit message to all the direct connection, a meesage will come on screen 
	       telling which node has quited from network.
	unid : To display the unique Node ID.
	cls : To clear the screen.
	show : To show the list of incomming and outgoing connectioins.
	help : To get list of valid commands and usage.
	
7. "Query messages with TTL=1, hops=0 and Search Criteria=" " (four spaces) are used to index all files a 
   host is sharing. Servents SHOULD reply to such queries with all its shared files." To check this, enter command as:
   Simpella>>find<space><space><space><space>
   only 4 spaces, nor 3 nor 5.

8. Valid port id assumed between 1024 to 65535(Inclusive), if port number out of range this is used in open command or at the
   initial invokation of Simpella, it shows appropriate error.

------------------------------------------------------------
Note:
1. In a small network when you type find command you imidiately get QueryHit(s), if any.
   In this senario you will not able to see counter changing of "X response received", as it will happen very fast.
   For ONLY demonstration purpose of proper implementation of find command we have added random delay in each node before responding by QueryHit,
   so that evaluaters can see the counter is changing, oblviously this this not the requirement and will not be needed in actual implementation.

2. We can create the two or more insatce of the program on same machine with different tcp port number, each is independent of others.

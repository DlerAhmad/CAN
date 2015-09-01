How to run this program:

	1) Inside the proj1 directory compile all the files using the command: javac *.java
	
	2) Register bootstrapping server with RMI on glados machine. 
		a) inside the proj1 directory run the command: rmiregistry 9111
		b) run the file RegisterServerWithRMI: java RegisterServerWithRMI
	
	3) Now to run the program, login (or ssh) into any machine and do the following steps:
		a) run the registry on port 9111: rmiregistry 9111
			note: if you want to run the program on glados, skip this step

		b) run the file CanClient: java CanClient


Dler Ahmad
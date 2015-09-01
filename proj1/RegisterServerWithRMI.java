/*
 * Instantiates the remote object from class "ServerInterfaceImp" 
 * and register it with the running RMI registry
 * 
 *  @author Dler
 * 
 * Version:
 *        $Id$
 * Revisions:
 *        $Log$
 */

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegisterServerWithRMI{
	static int port=Constants.port;

	public static void main(String args[]) throws IOException {
		
		try {
			Registry registry;
			parsArgs(args);
			
			BootstrapingServerInterface remoteObject = new BootstrapingServerImp();
			
			if (port!=-1){
				registry = LocateRegistry.getRegistry(port);
			}else{
				registry = LocateRegistry.getRegistry();
			}
			
	
			registry.rebind("BootstrapingServer", remoteObject);
		
			System.out.println("BootstrapingServer" + remoteObject + " registered.");
			

		} catch (RemoteException ex) {
			System.out.println("Exception in Regisry.");
			System.out.println(ex.toString());
		}

	}
	
	private static void parsArgs(String args[]) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-p")) {
                    port = new Integer(args[++i]).intValue();
                } else if (args[i].equals("-help")) {
                    System.out.println("Command--> java RigisterWithRMIServer [-p port]");
                    System.exit(0);
                }
            }
        }
    }
	
}

/*
 client and server class needs to be modified.
*/
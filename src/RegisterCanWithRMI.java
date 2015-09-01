import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegisterCanWithRMI {

	static int port = -1;

	public static void main(String args[]) throws IOException {
/*
		try {
			Registry registry;
			parsArgs(args);

			CANInterface remoteCan = new CanImp();

			if (port != -1) {
				registry = LocateRegistry.getRegistry(port);
			} else {
				registry = LocateRegistry.getRegistry();
			}

			registry.rebind("remoteCan", remoteCan);

			System.out.println("remoteCan" + remoteCan
					+ " registered.");

		} catch (RemoteException ex) {
			System.out.println("Exception in Regisry.");
			System.out.println(ex.toString());
		}
		*/

	}

	private static void parsArgs(String args[]) {
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-p")) {
					port = new Integer(args[++i]).intValue();
				} else if (args[i].equals("-help")) {
					System.out
							.println("Command--> java RigisterCanWithRMI [-p port]");
					System.exit(0);
				}
			}
		}
	}

}

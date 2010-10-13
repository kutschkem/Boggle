package boggle.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import boggle.game.BoggleServer;

/**
 * responsible for asynchronously accepting incoming connections and registering the
 * remote clients to the BoggleServer
 * @author Michael
 *
 */
public class LocalBoggleServer {

	Thread thread;

	public LocalBoggleServer(final BoggleServer localServer) {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				ServerSocket socket = null;
				try {
					socket = new ServerSocket(8989);
					while (!Thread.interrupted()) {
						Socket s = socket.accept();
						localServer.registerClient(new RemoteBoggleClient(s,
								localServer));
						//TODO Benachrichtigung
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null)
						try {
							socket.close();
						} catch (IOException e) {
							// silently ignore
						}
				}
			}

		});
		thread.setDaemon(true);
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}

}

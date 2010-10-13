package boggle.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import boggle.game.BoggleServer;

public class LocalServer {
	
	Thread thread;
	
	public LocalServer(final BoggleServer localServer){
		thread = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					ServerSocket socket = new ServerSocket(8989);
					while(!Thread.interrupted()){
						Socket s = socket.accept();
						localServer.registerClient(
								new RemoteClient(s,localServer));
						//Benachrichtigung
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public void stop(){
		thread.interrupt();
	}

}

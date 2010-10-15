package boggle.net;

import java.io.IOException;

import boggle.game.BoggleGame;

public class DedicatedServer {

	BoggleGame game = new BoggleGame();
	
	
	public static void main(String... args) throws IOException, InterruptedException{
		final DedicatedServer server = new DedicatedServer();
		new LocalBoggleServer(server.game);
		Thread serverThread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					server.game.restart();
					try {
						Thread.sleep(210000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			
		});
		serverThread.setDaemon(true);
		serverThread.start();
		System.out.println("*****Server started*****");
		System.out.println("Press any key to shutdown");
		System.in.read();
		System.out.println("Shutdown Server");
		Thread.sleep(1000);
	}
}

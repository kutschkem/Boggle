package boggle.net;

import java.io.IOException;

import boggle.game.BoggleGame;
import boggle.game.BoggleRules;
import boggle.game.Dictionary;

public class DedicatedServer {

	BoggleGame game = new BoggleGame();
	
	
	public static void main(String... args) throws IOException, InterruptedException{
		final DedicatedServer server = new DedicatedServer();
		new LocalBoggleServer(server.game);
		Thread serverThread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					BoggleRules rules = new BoggleRules();
					rules.timeLimit = 30000;
					server.game.setRules(rules);
						Dictionary dictionary = new Dictionary(false);
						try {
						dictionary.load("deutsch.dic");
						server.game.setDictionary(dictionary);
						server.game.blacklist.load("deutsch.blk");
						server.game.restart();

						Thread.sleep(120000);
					} catch (InterruptedException e) {
						break;
					} catch(IOException e){
						e.printStackTrace();
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

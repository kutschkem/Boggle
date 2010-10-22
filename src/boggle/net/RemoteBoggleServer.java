package boggle.net;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kutschke.higherClass.ReflectiveFun;
import kutschke.interpreter.LispStyleInterpreter;
import kutschke.interpreter.Parser;
import kutschke.interpreter.SyntaxException;
import boggle.game.BoggleClient;
import boggle.game.BoggleRules;
import boggle.game.WordStatus;

public class RemoteBoggleServer {

	final BoggleClient client;
	final Socket socket;

	public RemoteBoggleServer(Socket socket, BoggleClient client) {
		this.client = client;
		this.socket = socket;
		Thread readingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					LispStyleInterpreter interpreter = new LispStyleInterpreter();
					Class<?> clazz = RemoteBoggleServer.class;
					Object t_this = RemoteBoggleServer.this;
					interpreter.addMethod("GET", 
							new ReflectiveFun<Void>(
									"processGet", 
									clazz, 
									new Class<?>[]{}).setBound(t_this));
					interpreter.addMethod("End", new ReflectiveFun<Void>(
							"processEnd", clazz, new Class<?>[]{Number.class,String[].class} )
							.setBound(t_this));
					interpreter.addMethod("Start", new ReflectiveFun<Void>(
							"processStart",clazz,new Class<?>[]
						{Number.class,Number.class,Number.class,Number.class,Number.class,String[].class})
						.setBound(t_this));
					interpreter.setDEBUG(true);
					Parser parser = Parser.standardParser();
					parser.setInterpreter(interpreter);
					parser.parse( new InputStreamReader(new BufferedInputStream(
									RemoteBoggleServer.this.socket
											.getInputStream())));
					} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		},"Remote BoggleServer Thread");
		readingThread.setDaemon(true);
		readingThread.start();
	}

	public void processStart(Number width, Number height, 
			Number minLetters, Number timeLimit, Number remaining, String... field) throws IOException {
		BoggleRules rules = new BoggleRules();

		rules.boggleWidth = width.intValue();
		rules.boggleHeight = height.intValue();
		rules.minLetters = minLetters.intValue();
		rules.timeLimit = timeLimit.longValue();

		char[][] field_arr = new char[rules.boggleWidth][rules.boggleHeight];

		for (int i = 0; i < field_arr.length; i++) {
			for (int j = 0; j < field_arr[0].length; j++) {
				field_arr[i][j] = (field[i*field_arr[0].length + j]).charAt(0);
			}
		}

		long timelimit = remaining.longValue();
		client.notifyGameStart(rules, field_arr, timelimit);

	}

	public void processGet() throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket
				.getOutputStream()));
		out.write("(WORDS (list ");
		for (String word : client.getWordList()) {
			out.write(word);
			out.write(" ");
		}
		out.write("))\n");
		out.flush();
	}

	public void processEnd(Number score, String... args) throws IOException {
		int t_score = score.intValue();
		Map<String, WordStatus> wordmap = new HashMap<String, WordStatus>();

		Iterator<String> it = Arrays.asList(args).iterator();
		while (it.hasNext()) {
			String word = it.next();
			WordStatus stat = Enum.valueOf(WordStatus.class,
					it.next());
			wordmap.put(word, stat);
		}
		client.notifyGameEnd(t_score, wordmap);
	}

}

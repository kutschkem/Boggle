package boggle.net;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kutschke.higherClass.ReflectiveFun;
import kutschke.interpreter.Parser;
import kutschke.interpreter.SimpleInterpreter;
import kutschke.interpreter.SyntaxException;
import kutschke.utility.CharFilterStream;
import boggle.game.BoggleClient;
import boggle.game.BoggleRules;
import boggle.game.BoggleServer;
import boggle.game.WordStatus;

public class RemoteBoggleClient implements BoggleClient {
	
	private Socket socket;
	private BoggleServer server;
	
	public RemoteBoggleClient(Socket socket, BoggleServer server){
		this.socket = socket;
		this.server = server;
	}

	@Override
	public Collection<String> getWordList() {
		final List<String> words = new ArrayList<String>();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write("(GET)\n");
			out.flush();
			SimpleInterpreter interpreter = new SimpleInterpreter();
			interpreter.addMethod("list", new ReflectiveFun<List<String>>("asList",Arrays.class,new Class<?>[]{Object[].class}));
			interpreter.addMethod("WORDS", new ReflectiveFun<Void>("addAll",words.getClass(),new Class<?>[]{Collection.class})
					.setBound(words));
			interpreter.setDEBUG(true);
			Parser parser = Parser.standardParser();
			parser.setGreedy(false);
			parser.setInterpreter(interpreter);
			parser.parse(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			endConnection();
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
		return words;
	}

	@Override
	public void notifyGameEnd(int score, Map<String, WordStatus> wordMap) {
		try {
			Writer out = new OutputStreamWriter(new CharFilterStream(new BufferedOutputStream(socket.getOutputStream())));
			out.write("(End ");
			out.write(String.valueOf(score));
			out.write(" ");
			for(Entry<String,WordStatus> e : wordMap.entrySet()){
				out.write(" ");
				out.write(e.getKey());
				out.write(" ");
				out.write(e.getValue().toString());
				out.write(" ");
			}
			out.write(")\n");
			out.flush();
		} catch (IOException e) {
			endConnection();
		}

	}

	@Override
	public void notifyGameStart(BoggleRules rules, char[][] field, long timeLimit) {
		try {
			Writer out = new OutputStreamWriter(new CharFilterStream(new BufferedOutputStream(socket.getOutputStream())));
			out.write("(Start ");
			out.write(String.valueOf(rules.boggleWidth));
			out.write(" ");
			out.write(String.valueOf(rules.boggleHeight));
			out.write(" ");
			out.write(String.valueOf(rules.minLetters));
			out.write(" ");
			out.write(String.valueOf(rules.timeLimit));
			out.write(" ");
			out.write(String.valueOf(timeLimit));
			out.write(" ");
			for(char[] c_arr : field){
				for(char c : c_arr){
					out.write(c);
					out.write(" ");
				}
			}

			out.write(")\n");
			out.flush();			
		}catch(IOException e){
			endConnection();
		}
	}
	
	public void endConnection(){
		server.unregisterClient(this);
	}

}

package boggle.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kutschke.generalStreams.InStream;
import kutschke.higherClass.ReflectiveFun;
import kutschke.interpreter.LispStyleInterpreter;
import kutschke.interpreter.Parser;
import kutschke.utility.CharFilterStream;
import boggle.game.BoggleClient;
import boggle.game.BoggleRules;
import boggle.game.BoggleServer;
import boggle.game.WordStatus;

public class RemoteBoggleClient implements BoggleClient {
	
	private Socket socket;
	private BoggleServer server;
	InStream<Object> parsingStream;
	
	public RemoteBoggleClient(Socket socket, BoggleServer server) throws IOException{
		this.socket = socket;
		this.server = server;
		LispStyleInterpreter interpreter = new LispStyleInterpreter();
		try {
			interpreter.addMethod("WORDS", new ReflectiveFun<List<String>>("asList",Arrays.class,new Class<?>[]{Object[].class}));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		interpreter.setDEBUG(true);
		Parser parser = Parser.standardParser();
		parser.setInterpreter(interpreter);
		parsingStream = parser.stream(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));

	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> getWordList() {
		List<String> words = new ArrayList<String>();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write("(GET)\n");
			out.flush();
			words.clear();
			words.addAll((Collection<? extends String>) parsingStream.read());
			} catch (IOException e) {
				e.printStackTrace();
			endConnection();
		} catch (SecurityException e) {
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

package boggle.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.io.StreamTokenizer.*;

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
		List<String> words = new ArrayList<String>();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write("[GET]");
			out.flush();
			StreamTokenizer in = new StreamTokenizer(new BufferedReader(new InputStreamReader(socket.getInputStream())));
			in.ordinaryChar('{');
			in.ordinaryChar('}');
			if(in.nextToken() != '{')
				throw new IOException("wrong start of message");
			
	Loop:	while(in.nextToken() != TT_EOF){
				switch(in.ttype){
				case TT_WORD:
					words.add(in.sval);
					break;
				case '}':
					break Loop;
					
				}
			}
		} catch (IOException e) {
			endConnection();
		}
		return words;
	}

	@Override
	public void notifyGameEnd(int score, Map<String, WordStatus> wordMap) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write("[End;");
			out.write(String.valueOf(score));
			out.write(";");
			for(Entry<String,WordStatus> e : wordMap.entrySet()){
				out.write("<");
				out.write(e.getKey());
				out.write(",");
				out.write(e.getValue().toString());
				out.write(">;");
			}
			out.write("]");
			out.flush();
		} catch (IOException e) {
			endConnection();
		}

	}

	@Override
	public void notifyGameStart(BoggleRules rules, char[][] field, long timeLimit) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write("[Start;");
			out.write(String.valueOf(rules.boggleWidth));
			out.write(";");
			out.write(String.valueOf(rules.boggleHeight));
			out.write(";");
			out.write(String.valueOf(rules.minLetters));
			out.write(";");
			out.write(String.valueOf(rules.timeLimit));
			out.write(";");
			for(char[] c_arr : field){
				for(char c : c_arr){
					out.write(c);
					out.write(";");
				}
			}
			out.write(String.valueOf(timeLimit));
			out.write("]");
			out.flush();			
		}catch(IOException e){
			endConnection();
		}
	}
	
	public void endConnection(){
		server.unregisterClient(this);
	}

}

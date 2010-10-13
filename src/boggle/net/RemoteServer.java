package boggle.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.io.StreamTokenizer.*;

import boggle.game.BoggleClient;
import boggle.game.BoggleRules;
import boggle.game.WordStatus;

public class RemoteServer{
	
	final BoggleClient client;
	final Socket socket;
	
	public RemoteServer(Socket socket, BoggleClient client){
		this.client = client;
		this.socket = socket;
		Thread readingThread = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					StreamTokenizer in = new StreamTokenizer(new BufferedReader(new InputStreamReader(RemoteServer.this.socket.getInputStream())));
					in.ordinaryChar('[');
					in.ordinaryChar(']');
					in.ordinaryChar(';');
					in.ordinaryChar('<');
					in.ordinaryChar('>');
					in.ordinaryChar(',');
					in.parseNumbers();
					while(in.nextToken() == '['){
						in.nextToken();
						if(in.sval.equals("Start"))
							processStart(in);
						else if(in.sval.equals("GET"))
							processGET(in);
						else if(in.sval.equals("End"))
							processEnd(in);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		});
		readingThread.setDaemon(true);
		readingThread.start();
	}
	
	public void processStart(StreamTokenizer in) throws IOException{
		BoggleRules rules = new BoggleRules();
		
		rules.boggleWidth = ((Number) processPart(in, TT_NUMBER)).intValue();
		rules.boggleHeight = ((Number) processPart(in, TT_NUMBER)).intValue();
		rules.minLetters = ((Number) processPart(in, TT_NUMBER)).intValue();
		rules.TimeLimit = ((Number) processPart(in, TT_NUMBER)).longValue();
		
		char[][] field = new char[rules.boggleWidth][rules.boggleHeight];
		
		for(int i = 0; i < field.length; i ++){
			for(int j = 0; j < field[0].length; j++){
				field[i][j] = ((String) processPart(in, TT_WORD)).charAt(0);
			}
		}
		
		long timelimit = ((Number) processPart(in, TT_NUMBER)).longValue();
		if(in.nextToken() != ']')
			throw new IOException("unexpected end of message");
		client.notifyGameStart(rules, field, timelimit);
		
	}
	
	private Object processPart(StreamTokenizer in, int ttNumber) throws IOException {
		while(in.nextToken() != TT_EOF){
			if(in.ttype == ttNumber){
				System.out.println(in.sval);
				switch(in.ttype){
				case TT_NUMBER:
					return in.nval;
				case TT_WORD:
					return in.sval;
				}
			}
		}
		throw new IOException("Unexpected end of Stream");
	}

	public void processGET(StreamTokenizer in) throws IOException{
		if(in.nextToken() != ']')
			throw new IOException("unexpected end of Message");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		out.write("{");
		for(String word: client.getWordList()){
			out.write(word);
			out.write(" ");
		}
		out.write("}");
		out.flush();
		
	}
	
	public void processEnd(StreamTokenizer in) throws IOException{
		int score = ((Number) processPart(in, TT_NUMBER)).intValue();
		Map<String,WordStatus> wordmap = new HashMap<String,WordStatus>();
		
		while(in.nextToken() != ']'){
			String word = (String) processPart(in, TT_WORD);
			WordStatus stat = Enum.valueOf(WordStatus.class, (String)
					processPart(in, TT_WORD));
			if(in.nextToken() != '>')
				throw new IOException("Malformed Request");
			if(in.nextToken() != ';')
				throw new IOException("Malformed Request");
			wordmap.put(word, stat);
		}
		client.notifyGameEnd(score, wordmap);
	}

}

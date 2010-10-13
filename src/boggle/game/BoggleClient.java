package boggle.game;

import java.util.Collection;
import java.util.Map;

public interface BoggleClient {

	public Collection<String> getWordList();
	public void notifyGameEnd(int score, Map<String,WordStatus> wordMap);
	public void notifyGameStart(BoggleRules Rules, char[][] field, long timeLimit);
}

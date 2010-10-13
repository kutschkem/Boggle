package boggle.game;

import java.util.Collection;
import java.util.Map;

public interface BoggleClient {

	/**
	 * 
	 * @return the past words the client found
	 */
	public Collection<String> getWordList();

	/**
	 * Postcondition: Clients need to empty their stored list of words
	 * @param score the accumulated score for all valid words
	 * @param wordMap
	 */
	public void notifyGameEnd(int score, Map<String, WordStatus> wordMap);

	/**
	 * 
	 * @param Rules
	 * @param field
	 * @param timeLimit the remaining game time
	 */
	public void notifyGameStart(BoggleRules Rules, char[][] field,
			long timeLimit);
}

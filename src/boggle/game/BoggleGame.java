package boggle.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kutschke.higherClass.AbstractFun;
import kutschke.higherClass.NoThrowLambda;

public class BoggleGame implements BoggleServer {

	protected char[][] field;
	public Dictionary dictionary = new Dictionary();
	public Dictionary blacklist = new Dictionary(false);

	private long timeMark = 0;
	private Thread timerThread = null;

	protected List<BoggleClient> clients = new LinkedList<BoggleClient>();

	BoggleRules Rules = new BoggleRules();

	/**
	 * 
	 * @return the game field
	 */
	public char[][] getField() {
		return field;
	}

	public boolean checkDictionary(String word) {
		return (!blacklist.check(word)) && dictionary.check(word);
	}

	/**
	 * checks whether or not the given word can be built with the current field, only
	 * connecting neighboring letters
	 * @param word
	 * @return
	 */
	public boolean checkPossibleWord(char[] word) {
		if (word.length < Rules.minLetters)
			return false;
		LinkedList<Point> positions = new LinkedList<Point>();
		for (int i = 0; i < field.length; i++)
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == word[0])
					positions.add(new Point(i, j));
			}
		return checkSequenceSanity(word, 0, positions, new LinkedList<Point>());
	}

	/**
	 * performs a DFS on the game field to determine whether or not the given
	 * character sequence (word) could possibly have been built using the Boggle
	 * Rules. That is, characters i and i+1 have to be direct neighbors on the
	 * field for every 0 <= i < sequence.length - 1
	 * 
	 * @param sequence
	 *            the word, as a character sequence
	 * @param index
	 *            the current i, as in the description above
	 * @param positions
	 *            the positions to be checked; at the beginning, these are all
	 *            points in the field, in further steps, these are the
	 *            neighbours of the previously visited point
	 * @param visited
	 *            Accumulator for the already visited points (no position may be
	 *            used more than once)
	 * @return true if the character sequence can possibly be built with the
	 *         current game field. False otherwise
	 */
	private boolean checkSequenceSanity(char[] sequence, int index,
			Collection<Point> positions, Collection<Point> visited) {
		for (Point p : positions) {
			if (p == null)
				continue;
			if (p.x < 0 || p.y < 0 || p.x >= field.length
					|| p.y >= field[p.x].length)
				continue;
			if (visited.contains(p))
				continue;
			if (field[p.x][p.y] != sequence[index])
				continue;
			if (index == sequence.length - 1)
				return true;
			visited.add(p);
			boolean check = checkSequenceSanity(sequence, index + 1, Arrays
					.asList(createNeighbourArray(p)), visited);
			if (check)
				return true;
			visited.remove(p);
		}
		return false;
	}

	/**
	 * Helper function that returns all neighbours of this point (no sanity
	 * checks are performed)
	 * 
	 * @param p
	 *            a point
	 * @return all points that are around p (-> 8 )
	 */
	private Point[] createNeighbourArray(Point p) {
		Point[] result = new Point[8];
		result[0] = new Point(p.x - 1, p.y);
		result[1] = new Point(p.x - 1, p.y - 1);
		result[2] = new Point(p.x - 1, p.y + 1);
		result[3] = new Point(p.x, p.y - 1);
		result[4] = new Point(p.x, p.y + 1);
		result[5] = new Point(p.x + 1, p.y - 1);
		result[6] = new Point(p.x + 1, p.y);
		result[7] = new Point(p.x + 1, p.y + 1);
		return result;
	}

	/**
	 * restarts the game <br/>
	 * This will also cause all Clients to get notified about the newly started
	 * game
	 */
	public void restart() {
		field = new char[Rules.boggleWidth][Rules.boggleHeight];
		timeMark = System.currentTimeMillis();
		// setzen der Felder
		for (int i = 0; i < field.length; i++)
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = Rules.getChar(Math.random());
			}

		if (timerThread != null)
			timerThread.interrupt();
		timerThread = new Thread(new Runnable(){
			public void run() {
				while (getElapsedTime() < Rules.timeLimit) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						return;
					}
				}
				evaluateAllClients();
			}
		});
		timerThread.start();
		for (BoggleClient c : clients)
			c.notifyGameStart(Rules, field, Rules.timeLimit);
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - timeMark;
	}

	/**
	 * evaluates a word according to it's length
	 * 
	 * @param words
	 * @return the score achieved for all the words together
	 */
	public int evaluate(Collection<String> words) {
		int result = 0;
		for (String s : words) {
			if (s.length() >= Rules.points.length)
				result += Rules.points[Rules.points.length - 1];
			else
				result += Rules.points[s.length() - 1];
		}
		return result;
	}

	/**
	 * gets every client's words, evaluates them and notifies the clients of the
	 * game end
	 */
	@SuppressWarnings("unchecked")
	private void evaluateAllClients() {
		final Collection<String>[] wordlists = new ArrayList[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			wordlists[i] = new ArrayList<String>();
			Collection<String> helpStrCol = clients.get(i).getWordList();
			synchronized (helpStrCol) {
				wordlists[i].addAll(helpStrCol);
			}
		}

		for (int i = 0; i < clients.size(); i++) {
			final Map<String, WordStatus> wordMap = new HashMap<String, WordStatus>();
			for (String str : wordlists[i]) {
				WordStatus status = WordStatus.ACCEPTED;
				for (int j = 0; j < clients.size(); j++) {//check for duplicates
					if (j != i)
						if (wordlists[j].contains(str))
							status = WordStatus.DOUBLE;
				}
				if (!checkPossibleWord(str.toCharArray()))
					status = WordStatus.IMPOSSIBLE_WORD;
				else if (blacklist.check(str))
					status = WordStatus.ON_BLACKLIST;
				else if (!checkDictionary(str))
					status = WordStatus.UNKNOWN;
				wordMap.put(str, status);
			}
			Collection<String> words = AbstractFun.filter(wordlists[i],
					new NoThrowLambda<String, Boolean>() {

						@Override
						public Boolean apply(String arg) {
							return wordMap.get(arg) == WordStatus.ACCEPTED;
						}

					});// filters all the words that have been accepted
			clients.get(i).notifyGameEnd(evaluate(words), wordMap);
		}
	}

	@Override
	public void registerClient(BoggleClient cl) {
		if (!clients.contains(cl))
			clients.add(cl);
		if(timerThread != null && timerThread.isAlive())
		cl.notifyGameStart(Rules, getField(), Rules.timeLimit
				- getElapsedTime());

	}

	@Override
	public void setDictionary(Dictionary dic) {
		dictionary = dic;

	}

	@Override
	public void setRules(BoggleRules rules) {
		Rules = rules;

	}

	@Override
	public void unregisterClient(BoggleClient cl) {
		clients.remove(cl);

	}
}

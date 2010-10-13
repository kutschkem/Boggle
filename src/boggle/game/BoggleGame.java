package boggle.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kutschke.higherClass.AbstractFun;
import kutschke.higherClass.Lambda;

public class BoggleGame implements BoggleServer {

	protected char[][] field;
	public Dictionary dictionary = new Dictionary();
	public Dictionary blacklist = new Dictionary(false);

	private long timeMark = 0;
	private Thread timerThread = null;

	protected ArrayList<BoggleClient> clients = new ArrayList<BoggleClient>();
	
	BoggleRules Rules = new BoggleRules();

	public char[][] getField() {
		return field;
	}

	public boolean checkDictionary(String word) {
		return (!blacklist.check(word)) && dictionary.check(word);
	}

	public boolean checkPossibleWord(String word) {
		return checkPossibleWord(word.toCharArray());
	}

	public boolean checkPossibleWord(char[] word) {
		if (word.length < Rules.minLetters)
			return false;
		LinkedList<Point> positions = new LinkedList<Point>();
		for (int i = 0; i < field.length; i++)
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == word[0])
					positions.add(new Point(i, j));
			}
		return checkSequenceList(word, 0, positions,
				new LinkedList<Point>());
	}

	private boolean checkSequenceList(char[] sequence, int index,
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
			boolean check = checkSequenceList(sequence, index + 1,
					Arrays.asList(createNeighbourArray(p)), visited);
			if (check)
				return true;
			visited.remove(p);
		}
		return false;
	}

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
		timerThread = new Thread() {
			public void run() {
				while (getElapsedTime() < Rules.TimeLimit) {
					try {
						sleep(50);
					} catch (InterruptedException e) {
						return;
					}
				}
				evaluateAllClients();
			}
		};
		timerThread.start();
		for(BoggleClient c : clients)
			c.notifyGameStart(Rules, field, Rules.TimeLimit);
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - timeMark;
	}

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

	@SuppressWarnings("unchecked")
	private void evaluateAllClients() {
		final Collection<String>[] wordlists = new ArrayList[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			wordlists[i] = new ArrayList<String>();
			Collection<String> helpStrCol = clients.get(i).getWordList();
			synchronized(helpStrCol){
			wordlists[i].addAll(helpStrCol);
			}
		}

		for (int i = 0; i < clients.size(); i++) {
			final Map<String, WordStatus> wordMap = new HashMap<String, WordStatus>();
			for(String str : wordlists[i]){
				WordStatus status = WordStatus.ACCEPTED;
				for (int j = 0; j < clients.size(); j++) {
					if (j != i)
						if (wordlists[i].contains(str))
							status = WordStatus.DOUBLE;
				}
				if(! checkPossibleWord(str))
					status = WordStatus.IMPOSSIBLE_WORD;
				else
				if(blacklist.check(str))
					status = WordStatus.ON_BLACKLIST;
				else
					if(! checkDictionary(str))
						status = WordStatus.UNKNOWN;
				wordMap.put(str, status);
			}
			Collection<String> words = AbstractFun.filter(wordlists[i],
					new Lambda<String, Boolean>() {

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
		clients.add(cl);

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

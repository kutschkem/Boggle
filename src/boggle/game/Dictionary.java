package boggle.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class Dictionary {

	private static final String WIKTIONARY = "http://de.wiktionary.org/wiki/";
    protected HashSet<String> dictionary = new HashSet<String>();
	private boolean wiktionaryActive = true;

	public Dictionary() {
	}

	public Dictionary(boolean UseWiktionary) {
		wiktionaryActive = UseWiktionary;
	}

	public void setWiktionaryActive(boolean wiktionaryActive) {
		this.wiktionaryActive = wiktionaryActive;
	}

	public boolean isWiktionaryActive() {
		return wiktionaryActive;
	}

	public void addWord(String word) {
		dictionary.add(word);
	}

	public void removeWord(String word) {
		dictionary.remove(word);
	}

	public void reset() {
		dictionary = new HashSet<String>();
	}

	/**
	 * 
	 * @param word
	 * @return true if word is contained in the dictionary, false otherwise
	 */
	public boolean check(String word) {
		word = word.toLowerCase();
		boolean result = dictionary.contains(word);
		if (!result && wiktionaryActive) {
			result = checkThroughWiki(word);
			if (!result)
				result = checkThroughWiki(toUppercase(word));
			if (!result)
				result = checkThroughWiki(replaceUmlauts(word));
			if (!result)
				result = checkThroughWiki(toUppercase(replaceUmlauts(word)));
			if (result)
				addWord(word);
		}
		return result;
	}

	/**
	 * loads a list of words from a file - the words being each in an own line
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void load(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename),
				256);
		try {
			String str = null;
			while ((str = reader.readLine()) != null)
				addWord(str.trim());
		} finally {
			reader.close();
		}
	}

	public void save(String filename) throws IOException {
		File file = new File(filename);
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename),
				1024);
		for (String str : dictionary) {
			writer.write(str);
			writer.newLine();
		}
		writer.close();
	}

	/**
	 * tries to look the word up in Wiktionary - no guarantee of correctness,
	 * but it's a chance<br/>
	 * btw this takes forever in comparison to just looking up the word in the
	 * internal dictionary
	 * 
	 * @param word
	 * @return
	 */
	protected boolean checkThroughWiki(String word) {
		URL url = null;
		try {
			url = new URL(WIKTIONARY + word);
		} catch (MalformedURLException e) {
			return false;
		}
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			if (con.getResponseCode() == 200)
				return true;
			return false;
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * makes the first letter of the word Uppercase
	 * 
	 * @param str
	 * @return
	 */
	protected String toUppercase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * replaces ae with ä, ue with ü and oe with ö
	 * 
	 * @param str
	 * @return
	 */
	protected String replaceUmlauts(String str) {
		String help = str.replace("ae", "ä");
		help = help.replace("Ae", "Ä");
		help = help.replace("oe", "ö");
		help = help.replace("Oe", "Ö");
		help = help.replace("ue", "ü");
		help = help.replace("Ue", "Ü");
		return help;
	}
}

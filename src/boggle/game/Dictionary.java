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

	public boolean check(String word) {
		word = word.toLowerCase();
		boolean result = dictionary.contains(word);
		if (!result && wiktionaryActive) {
			result = checkThroughWiki(word);
			if (!result)
				result = checkThroughWiki(toUppercase(word));
			if (!result)
				result = checkThroughWiki(Umlautersetzung(word));
			if (!result)
				result = checkThroughWiki(toUppercase(Umlautersetzung(word)));
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
		String str = null;
		while ((str = reader.readLine()) != null)
			addWord(str.trim());
		reader.close();
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
	private boolean checkThroughWiki(String word) {
		URL url = null;
		try {
			url = new URL("http://de.wiktionary.org/wiki/" + word);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	private String toUppercase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * replaces ae with ä, ue with ü and oe with ö
	 * 
	 * @param str
	 * @return
	 */
	private String Umlautersetzung(String str) {
		String help = str.replace("ae", "ä");
		help = help.replace("Ae", "Ä");
		help = help.replace("oe", "ö");
		help = help.replace("Oe", "Ö");
		help = help.replace("ue", "ü");
		help = help.replace("Ue", "Ü");
		return help;
	}
}

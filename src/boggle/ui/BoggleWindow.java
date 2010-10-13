package boggle.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import boggle.game.BoggleClient;
import boggle.game.BoggleGame;
import boggle.game.BoggleRules;
import boggle.game.Dictionary;
import boggle.game.WordStatus;

@SuppressWarnings("serial")
public class BoggleWindow extends JFrame implements BoggleClient {

	BoggleGame game = new BoggleGame();
	BoggleCharPanel charPanel = new BoggleCharPanel(
			BoggleRules.STANDARD_BOGGLE_WIDTH,
			BoggleRules.STANDARD_BOGGLE_HEIGHT);
	BoggleInputPanel inputPanel = new BoggleInputPanel();
	BoggleStatusPanel statusPanel = new BoggleStatusPanel(this);
	private long maxtime = 0;
	char[][] field;
	Socket remoteSocket;

	String dictFileName = "deutsch";

	public BoggleWindow() {
		super("Boggle");
		initComponents();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BoggleWindow wnd = new BoggleWindow();
		wnd.setVisible(true);

		((BoggleGame) wnd.game).restart();
	}

	private void initComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(charPanel, BorderLayout.CENTER);
		getContentPane().add(inputPanel, BorderLayout.SOUTH);
		getContentPane().add(statusPanel, BorderLayout.EAST);

		charPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() / BoggleCharPanel.charPanelSize;
				int y = e.getY() / BoggleCharPanel.charPanelSize;
				inputPanel.setText(inputPanel.getText() + field[y][x]);
			}

		});

		this.setJMenuBar(BoggleMenuMaker.createMenu(this));

		pack();
		game.registerClient(this);
		try {
			Dictionary dictionary = new Dictionary();
			dictionary.load(dictFileName + ".dic");
			game.setDictionary(dictionary);
			game.blacklist.load(dictFileName + ".blk");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public Collection<String> getWordList() {
		return inputPanel.getWords();
	}

	@Override
	public void notifyGameEnd(int score, Map<String, WordStatus> wordMap) {
		JOptionPane.showMessageDialog(this, "Sie haben " + score
				+ " Punkte erreicht", "Spielende", JOptionPane.PLAIN_MESSAGE);
		try {
			game.dictionary.save(dictFileName + ".dic");
			game.blacklist.save(dictFileName + ".blk");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void notifyGameStart(BoggleRules rules, char[][] field,
			long timeLimit) {
		this.field = field;
		charPanel.setBoggleWidth(rules.boggleWidth);
		charPanel.setBoggleHeight(rules.boggleHeight);
		charPanel.initLabels();
		charPanel.setLabels(field);
		inputPanel.getWords().clear();
		maxtime = timeLimit;
		statusPanel.updateStartTime();
	}

	public long getMaxTime() {
		return maxtime;
	}
}

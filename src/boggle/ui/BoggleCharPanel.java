package boggle.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import boggle.exceptions.BoggleRuleException;

@SuppressWarnings("serial")
public class BoggleCharPanel extends JPanel {

	public static final int charPanelSize = 50;
	JLabel[] labels;
	private int boggleWidth;
	private int boggleHeight;

	public BoggleCharPanel(int width, int height) {
		super(new GridLayout(height, width));
		boggleWidth = width;
		boggleHeight = height;
		initLabels();
	}

	public void initLabels() {
		removeAll();
		labels = new JLabel[boggleWidth * boggleHeight];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new JLabel();
			labels[i].setMinimumSize(new Dimension(charPanelSize, charPanelSize));
			labels[i].setPreferredSize(new Dimension(charPanelSize, charPanelSize));
			labels[i].setHorizontalAlignment(SwingConstants.CENTER);
			labels[i].setBorder(BorderFactory.createLineBorder(Color.black));
			labels[i].setText(new Character((char) ('a' + i)).toString());
			add(labels[i]);
		}

	}

	public void setLabels(char[][] newField) throws BoggleRuleException {
		if (newField.length != boggleWidth
				|| newField[0].length != boggleHeight)
			throw new BoggleRuleException(
					"New Field doesn't match the Boggle rules");
		for (int i = 0; i < newField.length; i++)
			for (int j = 0; j < newField[0].length; j++)
				labels[i * newField[0].length + j].setText(new Character(
						newField[i][j]).toString());
	}

	public void setBoggleWidth(int boggleWidth) {
		this.boggleWidth = boggleWidth;
	}

	public int getBoggleWidth() {
		return boggleWidth;
	}

	public void setBoggleHeight(int boggleHeight) {
		this.boggleHeight = boggleHeight;
	}

	public int getBoggleHeight() {
		return boggleHeight;
	}

}

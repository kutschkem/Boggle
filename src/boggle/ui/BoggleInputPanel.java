package boggle.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class BoggleInputPanel extends JPanel implements ActionListener {

	private JTextField textField;
	private JButton checkBtn;

	private SortedSet<String> words = Collections.synchronizedSortedSet(new TreeSet<String>());

	public BoggleInputPanel() {
		super(new BorderLayout());
		textField = new JTextField();
		checkBtn = new JButton(new AbstractAction("Input") {

			@Override
			public void actionPerformed(ActionEvent e) {
				BoggleInputPanel.this.actionPerformed(e);

			}

		});
		textField.addActionListener(this);
		add(textField, BorderLayout.CENTER);
		add(checkBtn, BorderLayout.EAST);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		words.add(textField.getText());
		textField.setText(null);
	}

	public Collection<String> getWords() {
		return words;
	}
	
	public String getText(){
		return textField.getText();
	}
	
	public void setText(String text){
		textField.setText(text);
	}
}

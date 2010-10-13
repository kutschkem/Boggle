package boggle.ui;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class BoggleInputPanel extends JPanel implements ActionListener,
Observable<String>{

	private JTextField textField;
	private JButton checkBtn;

	private SortedSet<String> words = Collections.synchronizedSortedSet(new TreeSet<String>());
	private List<Observer<String>> observers = new LinkedList<Observer<String>>();
	
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
		for(Observer<String> ob : observers)
			ob.update(this, textField.getText());
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

	@Override
	public void addObserver(Observer<String> observer) {
		observers.add(observer);
		
	}

	@Override
	public void deleteObserver(Observer<String> observer) {
		observers.remove(observer);
		
	}
}

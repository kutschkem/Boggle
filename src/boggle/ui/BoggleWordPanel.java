package boggle.ui;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BoggleWordPanel extends JPanel implements Observer<String> {
	
	DefaultListModel model = new DefaultListModel();
	JList wordlist = new JList(model);

	public BoggleWordPanel(){
		super(new GridLayout(1,1));
		JScrollPane scrollpane = new JScrollPane(wordlist);
		add(scrollpane);
		
	}
	
	@Override
	public void update(Observable<String> observable, String arg) {
		if(arg == null)
			model.clear();
		else
			model.addElement(arg);
	}

}

package boggle.ui;

import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kutschke.interfaces.Observer;

@SuppressWarnings("serial")
public class BoggleWordPanel extends JPanel implements Observer<String> {

    DefaultListModel model = new DefaultListModel();
    JList wordlist = new JList(model);

    public BoggleWordPanel() {
        super(new GridLayout(1, 1));
        JScrollPane scrollpane = new JScrollPane(wordlist);
        add(scrollpane);

    }

    @Override
    public void update(String arg) {
        model.addElement(arg);
    }

    public void clear() {
        model.clear();
    }

}

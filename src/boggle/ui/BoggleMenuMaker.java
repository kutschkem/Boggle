package boggle.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public final class BoggleMenuMaker {

	private BoggleMenuMaker(){}
	
	@SuppressWarnings("serial")
	public static JMenuBar createMenu(final BoggleWindow parent){
		JMenuBar menu = new JMenuBar();
		
		JMenu fileMenu = new JMenu("Datei");
		menu.add(fileMenu);
		JMenuItem newItm = new JMenuItem(new AbstractAction("Neu"){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.game.restart();
				
			}
			
		});
		
		fileMenu.add(newItm);
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new AbstractAction("Ende"){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		}));
		
		return menu;
	}
}

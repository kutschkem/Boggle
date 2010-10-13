package boggle.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import boggle.net.LocalBoggleServer;
import boggle.net.RemoteBoggleServer;

public final class BoggleMenuMaker {

	private static final class MenuItemServer extends AbstractAction {
		private final BoggleWindow parent;
		static LocalBoggleServer serv;

		private MenuItemServer(String name, BoggleWindow parent) {
			super(name);
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (serv == null)
				serv = new LocalBoggleServer(parent.game);

		}
	}

	private BoggleMenuMaker() {
	}

	@SuppressWarnings("serial")
	public static JMenuBar createMenu(final BoggleWindow parent) {
		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu("Datei");
		menu.add(fileMenu);
		JMenuItem newItm = new JMenuItem(new AbstractAction("Neu") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (parent.remoteSocket != null)
					try {
						parent.remoteSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				parent.game.registerClient(parent);
				parent.game.restart();

			}

		});

		fileMenu.add(newItm);
		JMenuItem servItm = new JMenuItem(new MenuItemServer("Starte Server",
				parent));
		fileMenu.add(servItm);

		JMenuItem conItm = new JMenuItem(new AbstractAction("LAN Spiel") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String IP = JOptionPane
						.showInputDialog("Geben sie die IP-Adresse des Servers an:");
				try {
					parent.remoteSocket = new Socket(IP, 8989);
					parent.game.unregisterClient(parent);
					new RemoteBoggleServer(parent.remoteSocket, parent);
				} catch (UnknownHostException e) {
					JOptionPane.showMessageDialog(parent, "Invalid IP-address",
							"Connection failure", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(parent,
							"Unable to establish connection",
							"Connection failure", JOptionPane.ERROR_MESSAGE);
				}
			}

		});

		fileMenu.add(conItm);

		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new AbstractAction("Ende") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		}));

		return menu;
	}
}

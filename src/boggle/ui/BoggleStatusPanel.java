package boggle.ui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class BoggleStatusPanel extends JPanel {
	
	private BoggleWindow parentWindow;
	JLabel time = new JLabel();
	Thread timerThread = new Thread(){			
		@Override
		public void run(){
			while(true)
		if(parentWindow.game.getElapsedTime() <= parentWindow.getMaxTime()){
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Long el = (Long)(parentWindow.getMaxTime() - parentWindow.game.getElapsedTime())/1000;
			time.setText("<html><div align =\"center\">Time:<br> "
					+(el / 60)+":"+ (el%60 < 10 ? "0" : "")
					+(el%60)
					+"</div></html>");
		}
	}
		
	};
	
	public BoggleStatusPanel(BoggleWindow parent){
		parentWindow = parent;
		add(time);
		time.setPreferredSize(new Dimension(50,50));
		time.setHorizontalAlignment(SwingConstants.CENTER);
		timerThread.setDaemon(true);
		timerThread.start();

	}

}

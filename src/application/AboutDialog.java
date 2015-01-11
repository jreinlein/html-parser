package application;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Class for the "about" dialog which displays information about the author and
 * external libraries used
 * 
 * @author James Reinlein
 */
public class AboutDialog extends JDialog {
	public AboutDialog() {
		initUI();
	}

	public final void initUI() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		// spacer from top
		add(Box.createRigidArea(new Dimension(0, 10)));

		ImageIcon icon = new ImageIcon("notes.png");
		JLabel label = new JLabel(icon);
		label.setAlignmentX(0.5f);
		add(label);

		add(Box.createRigidArea(new Dimension(0, 10)));

		String text = "Written by James Reinlein, 2015" + "<br>"
				+ "Using JSoup and Apache Commons Lang" + "<br><br>"
				+ "Questions? Comments? Lonely?" + "<br>"
				+ "Email me at: jamesreinlein@gmail.com";
		// text-align: center sets the text inside centered, whereas the second
		// parameter to new JLabel() sets the text BOX centered to the window
		JLabel name = new JLabel("<html><div style=\"text-align: center;\">"
				+ text + "</html>", JLabel.CENTER);
		name.setFont(new Font("Serif", Font.BOLD, 22));
		name.setAlignmentX(0.5f);
		add(name);
		// spacer between text and button
		add(Box.createRigidArea(new Dimension(0, 20)));
		// close button, returns to applet
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		close.setAlignmentX(0.5f);
		add(close);

		pack(); // ensures window is centred!

		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("About");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(480, 270);
	}
}

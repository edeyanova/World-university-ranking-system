import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

public class UI {

	JFrame frame;
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException,
			IOException, URISyntaxException {
		new UI();
	}

	public UI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
				}

				frame = new JFrame("Testing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new TestPane());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	 public class TestPane extends JPanel {

	        private JTextField findText;
	        private JButton search;
	        private JButton update;
	        private DefaultListModel<String> model;

	        public TestPane() {
	            setLayout(new BorderLayout());
	            JPanel searchPane = new JPanel(new GridBagLayout());
	            JPanel updatePane = new JPanel(new GridBagLayout());
	            GridBagConstraints gbc = new GridBagConstraints();
	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            gbc.insets = new Insets(2, 2, 2, 2);
	            searchPane.add(new JLabel("Find: "), gbc);
	            gbc.gridx++;
	            gbc.fill = GridBagConstraints.HORIZONTAL;
	            gbc.weightx = 1;
	            findText = new JTextField(20);
	            searchPane.add(findText, gbc);

	            gbc.gridx++;
	            gbc.fill = GridBagConstraints.NONE;
	            gbc.weightx = 0;
	            search = new JButton("Search");
	            update = new JButton("Update");
	            searchPane.add(search, gbc);
	            updatePane.add(update, gbc);

	            add(searchPane, BorderLayout.NORTH);
	            add(updatePane, BorderLayout.SOUTH);

	            model = new DefaultListModel<>();
	            JList list = new JList(model);
	            add(new JScrollPane(list));

	            ActionHandler handler = new ActionHandler();
	            UpdateHandler handlerUpdate = new UpdateHandler();

	            search.addActionListener(handler);
	            update.addActionListener(handlerUpdate);
	            findText.addActionListener(handler);
	        }
	        
	        public class UpdateHandler implements ActionListener {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String filePath = "universities.txt";
					try {
						
						Download.download(filePath);
					} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String indexDirectoryPath = "index";
					try {
						Retrieval.indexFile(filePath, indexDirectoryPath);
						
						JOptionPane.showMessageDialog(frame, "Update complete");
					} catch (IOException | URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

	        }

		public class ActionHandler implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.removeAllElements();
				String searchText = findText.getText();
				try {
					HashSet<String> result = Retrieval.search(searchText, "index");
					
					if (result.size() == 0) {
						model.addElement("No results found");
					}
					for (String university : result) {
						model.addElement(university);
					}

				} catch (IOException | ParseException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}

}

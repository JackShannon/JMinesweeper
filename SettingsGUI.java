import java.awt.event.*;
import javax.swing.*;


public class SettingsGUI
{
    private Difficulty difficulty;

    public SettingsGUI()
    {
        difficulty = Difficulty.EASY;
    }
    
    private JPanel getPanel()
    {
        JPanel panel = new JPanel();
        
        JLabel difficultyLbl = new JLabel("Difficulty:");
        String[] difficultyStrs = {"Easy", "Medium", "Hard"};
        JComboBox difficultyCmb = new JComboBox(difficultyStrs);
        difficultyCmb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox c = (JComboBox)e.getSource();
                    String dif = (String)c.getSelectedItem();
                    if (dif.equals("Easy")) {
                        difficulty = Difficulty.EASY;
                    }
                    else if (dif.equals("Medium")) {
                        difficulty = Difficulty.MEDIUM;
                    }
                    else if (dif.equals("Hard")) {
                        difficulty = Difficulty.HARD;
                    }
                }
            });
		difficultyCmb.setSelectedIndex(difficulty.ordinal());
        
        panel.add(difficultyLbl);
        panel.add(difficultyCmb);
        
        return panel;
    }
            
    public void showGUI()
    {
        Difficulty prev = difficulty;
        int option = JOptionPane.showConfirmDialog(null, getPanel(), "Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            difficulty = prev;
        }
    }
    
    public Difficulty getDifficulty() 
    {
        return difficulty;
    }

}
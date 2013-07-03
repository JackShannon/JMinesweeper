import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;

public class MinesweeperGUI
{
    private JFrame frame;
    private Minesweeper game;
    private SettingsGUI settings;
    private JPanel hud;
    private JPanel board;
    private ArrayList<JButton> buttons;
    private JLabel time;
    private JLabel minesLeft;
    private Timer timer;
    private long timeRunning;
    private ImageIcon mineIcon;
    private ImageIcon flagIcon;
    private MouseListener buttonListener;
    //private Settings settings;
    
    public MinesweeperGUI()
    {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        game = new Minesweeper();
        settings = new SettingsGUI();
        hud = new JPanel();
        board = new JPanel();
        
        loadIcons();
        
        Container contentPane = frame.getContentPane();
        
        contentPane.setLayout(new BorderLayout());
        contentPane.add(hud, BorderLayout.NORTH);
        contentPane.add(board, BorderLayout.CENTER);
        
        drawMenu();
        drawHUD();
        drawBoard();
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        setUpTimer();
        resetTime();
        updateChanges();
    }
    
    private void resetTime()
    {
        time.setText("00:00");
        timeRunning = 0;
        timer.restart();
    }
    
    private void setUpTimer()
    {
        ActionListener timePerformer = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timeRunning += 1000;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timeRunning);
                    time.setText(new SimpleDateFormat("mm:ss").format(cal.getTime()));
                }
            };
        timer = new Timer(1000, timePerformer);
        timer.start();
    }
    
    private void drawMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");

        JMenuItem restartMenuItem = new JMenuItem("Restart");
        restartMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reset();
                }
            });
        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    settings.showGUI();
                    game.changeDifficulty(settings.getDifficulty());
                    drawButtons();
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                }
            });
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(1);
                }
            });

        fileMenu.add(restartMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(settingsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);
        menuBar.add(fileMenu);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Email jos6@kent.ac.uk for help.", "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpMenu.add(helpMenuItem);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
    private void drawHUD()
    {
        time = new JLabel();
        time.setPreferredSize(new Dimension(50, 30));
        time.setHorizontalAlignment(JLabel.CENTER);
        
        minesLeft = new JLabel();
        minesLeft.setPreferredSize(new Dimension(50, 30));
        minesLeft.setHorizontalAlignment(JLabel.CENTER);
        
        JButton restartBtn = new JButton("Restart");
        restartBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reset();
                }
            });
        restartBtn.setFocusable(false);
        
        hud.setLayout(new BorderLayout());
        hud.add(time, BorderLayout.WEST);
        hud.add(restartBtn, BorderLayout.CENTER);
        hud.add(minesLeft, BorderLayout.EAST);
    }
    
    private void drawBoard()
    {
        board.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED));

        buttons = new ArrayList<JButton>();
        buttonListener = createButtonListener();
        
        drawButtons();
    }
    
    private void drawButtons()
    {
        board.removeAll();
        board.setLayout(new GridLayout(game.getMapRows(), game.getMapColumns()));
        buttons.clear();
        int buttonCount = game.getMapRows() * game.getMapColumns();
        for (int i = 0; i < (buttonCount); i++) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(20, 20));
            btn.addMouseListener(buttonListener);
            btn.setFocusable(false);
            btn.setMargin(new Insets(0, 0, 0, 0));
            board.add(btn);
            buttons.add(btn);
        }
    }
    
    private MouseListener createButtonListener()
    {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JButton b = (JButton)e.getSource();
                int i = buttons.indexOf(b);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    game.makeMove(i);
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    game.toggleFlag(i);
                }
                
                updateChanges();
            }
        };
    }
    
    private void loadIcons()
    {
        try {
            mineIcon = new ImageIcon(ImageIO.read(getClass().getResource("mine.png")));
            flagIcon = new ImageIcon(ImageIO.read(getClass().getResource("flag.png")));
        }
        catch (IOException ex) {
            System.out.println("Couldn't load icons.");
        }
    }
    
    private void updateChanges()
    {
        Map<Integer, BoxState> changes = game.getLastChanges();
        for (Map.Entry<Integer, BoxState> entry : changes.entrySet()) {
            int index = entry.getKey();
            BoxState state = entry.getValue();
            switch (state) {
            case SAFE:
                setButton(index, false, null, Integer.toString(game.getMapValue(index)));
                break;
                
            case MINE:
                setButton(index, false, mineIcon, "");
                break;
                
            case UNEXPLORED:
                setButton(index, true, null, "");
                break;
                
            case FLAGGED:
                setButton(index, true, flagIcon, "");
                break;

            default:
                break;
            }
        }
        minesLeft.setText(Integer.toString(game.minesLeft()));
        if (game.isGameOver()) {
            newGameDialog("Game Over!");
        }
        if (game.isWon()) {
            newGameDialog("You win!");
        }
    }
    
    private void setButton(int index, boolean enabled, ImageIcon icon, String text)
    {
        JButton b = buttons.get(index);
        b.setEnabled(enabled);
        b.setIcon(icon);
        b.setDisabledIcon(icon);
        b.setText(text);
    }
    
    private void reset()
    {
        game.reset();
        for (JButton b : buttons) {
            b.addMouseListener(buttonListener);
        }
        updateChanges();
        resetTime();
    }
    
    private void newGameDialog(String title)
    {
        timer.stop();
        for (JButton b : buttons) {
            b.removeMouseListener(buttonListener);
        }
        
        int option = JOptionPane.showConfirmDialog(null, "Would you like to start a new game?", title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            reset();
        }
    }
    
    public static void main(String[] args) {
        MinesweeperGUI m = new MinesweeperGUI();
    }
    
    
}

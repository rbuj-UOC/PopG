package popg;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Timer;

import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class PopGUserInterface extends JPanel implements ActionListener {

  private JPanel displayPanel;
  private JFrame frmPopG;
  private JMenuBar menuFile;
  private JMenu mnFile;
  private JMenuItem mntmSaveParams;
  private JMenuItem mntmPrint;
  private JMenuItem mntmAbout;
  private JMenuItem mntmQuit;
  private JMenu mnRun;
  private JMenu mnWindow;
  private JMenu mnLookAndFeel;
  private JMenuItem mntmContinuew;
  private JMenuItem mntmContinue;
  private JMenuItem mntmNewRun;
  private JMenuItem mntmLoadRun;
  private JMenuItem mntmRestart;
  private JMenuItem mntmWholePlot;
  private JMenuItem mntmTakeScreenshot;
  public int numberOfGen;
  public Graphics2D g2d;
  private PopGData inputvals;
  private JPanel contentPane;
  private JTextField txtPopSize;
  private JTextField txtFitGenAA;
  private JLabel lblPopSize;
  private JLabel lblFitGenAA;
  private JLabel lblFitGenAa;
  private JTextField txtFitGenAa;
  private JLabel lblFitGenaa;
  private JTextField txtFitGenaa;
  private JLabel lblMutAa;
  private JTextField txtMutAa;
  private JLabel lblMutaA;
  private JTextField txtMutaA;
  private JLabel lblMigRate;
  private JTextField txtMigRate;
  private JLabel lblInitFreq;
  private JTextField txtInitFreq;
  private JLabel lblGenRun;
  public JTextField txtGenRun;
  private JLabel lblNumPop;
  private JTextField txtNumPop;
  private JLabel lblRandSeed;
  private JTextField txtRandSeed;
  private JButton btnOK;
  private JButton btnQuit;
  private JButton btnDefaults;
  public double numberSeed1;
  private Random numberSeed;
  private JFrame frmPopGSettingsMenu;
  private JFrame frmContinue;
  private JLabel lblContinueGenRun;
  private JTextField txtContinueGenRun;
  private JButton btnContinueGenRunOK;
  private JButton btnContinueGenCancel;
  private JFrame frmAbout;

  private ArrayList<ArrayList<Double>> genArray;
  private int xOffset;
  private int yOffset;
  private int xLen;
  private int yLen;
  private int numFixedPops;
  private int numLostPops;
  private int beginGen;
  private int endGen;
  private Timer timer;
  private static final int TIMELEN = 1;
  private static final String DEFAULTS_JSON_TEXT = "{\n"
      + "  \"popSize\": 100,\n"
      + "  \"fitGenAA\": 1.0,\n"
      + "  \"fitGenAa\": 1.0,\n"
      + "  \"fitGenaa\": 1.0,\n"
      + "  \"mutAa\": 0.0,\n"
      + "  \"mutaA\": 0.0,\n"
      + "  \"migRate\": 0.0,\n"
      + "  \"initFreq\": 0.5,\n"
      + "  \"genRun\": 100,\n"
      + "  \"numPop\": 10,\n"
      + "  \"genSeed\": true,\n"
      + "  \"randSeed\": 0\n"
      + "}";
  private String filedir;
  private String pendingScreenshotPath;
  private boolean screenshotCaptured;
  private String currentLookAndFeelClassName;
  private boolean canSaveParameters;

  public class PopGData {
    Integer popSize;
    Double fitGenAA;
    Double fitGenAa;
    Double fitGenaa;
    Double mutAa;
    Double mutaA;
    Double migRate;
    Double initFreq;
    Integer genRun;
    Integer numPop;
    Boolean genSeed;
    Long randSeed;
  }

  private static class CliOptions {
    String defaultsJsonPath;
    boolean autoStartNewRun;
    String screenshotPath;
  }

  private static CliOptions parseCliOptions(String[] args) {
    CliOptions options = new CliOptions();
    if (args == null) {
      return options;
    }

    for (String arg : args) {
      if ("-n".equals(arg)) {
        options.autoStartNewRun = true;
      } else if (arg.startsWith("-p=")) {
        String screenshotPathArg = arg.substring(3).trim();
        if (screenshotPathArg.isEmpty()) {
          System.err.println("Warning: -p requires a non-empty path, example: -p=plot.png");
        } else {
          options.screenshotPath = screenshotPathArg;
        }
      } else if (!arg.startsWith("-") && options.defaultsJsonPath == null) {
        options.defaultsJsonPath = arg;
      } else {
        System.err.println("Warning: unrecognized argument '" + arg + "'");
      }
    }

    if (options.screenshotPath != null && !options.autoStartNewRun) {
      System.err.println("Warning: -p requires -n; screenshot option will be ignored.");
      options.screenshotPath = null;
    }

    return options;
  }

  /**
   * Launch the application.
   */

  public static void main(String[] args) {
    final CliOptions options = parseCliOptions(args);
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PopGUserInterface frame = new PopGUserInterface(options);
          frame.frmPopG.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */

  public PopGUserInterface(CliOptions options) {
    // initialize data
    inputvals = new PopGData();
    initInputVals();
    initializeLookAndFeelFromPlatform();
    if (options != null && options.defaultsJsonPath != null) {
      try {
        loadInputValsFromJson(options.defaultsJsonPath);
      } catch (IOException ioe) {
        System.err.println("Warning: could not read defaults file '" + options.defaultsJsonPath + "': "
            + ioe.getMessage());
      }
    }
    genArray = new ArrayList<ArrayList<Double>>();
    filedir = System.getProperty("user.dir");

    frmPopG = new JFrame();
    frmPopG.setTitle("PopG");
    frmPopG.setSize(650, 430);
    frmPopG.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmPopG.getContentPane().setLayout(null);
    frmPopG.setLocationRelativeTo(null);
    frmPopG.setVisible(true);
    setBounds(100, 100, 485, 382);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(null);

    displayPanel = new JPanel() {
      public void paintComponent(Graphics graph) {
        draw(graph);
      }
    };
    displayPanel.setBackground(Color.WHITE);
    displayPanel.setBounds(0, 0, 5000, 5000);
    displayPanel.setLayout(null);
    frmPopG.getContentPane().add(displayPanel);

    menuFile = new JMenuBar();
    frmPopG.setJMenuBar(menuFile);

    mnFile = new JMenu("File");
    menuFile.add(mnFile);

    mnRun = new JMenu("Run");
    menuFile.add(mnRun);

    mnWindow = new JMenu("Window");
    menuFile.add(mnWindow);
    mnLookAndFeel = new JMenu("Look and Feel");
    mnWindow.add(mnLookAndFeel);
    populateLookAndFeelMenu();
    mntmTakeScreenshot = new JMenuItem("Take Screenshot");
    mntmTakeScreenshot.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_S, getMenuShortcutMask() | InputEvent.SHIFT_DOWN_MASK));
    mntmTakeScreenshot.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        takeWindowScreenshot();
      }
    });
    mnWindow.add(mntmTakeScreenshot);
    mntmLoadRun = new JMenuItem("Open...");
    mntmLoadRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, getMenuShortcutMask()));
    mntmLoadRun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadRunFromJsonChooser();
      }
    });
    mnFile.add(mntmLoadRun);

    mntmSaveParams = new JMenuItem("Save...");
    mntmSaveParams.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, getMenuShortcutMask()));
    mntmSaveParams.setEnabled(false);
    mntmSaveParams.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveInputValsToJsonChooser();
      }
    });
    mnFile.add(mntmSaveParams);

    mntmContinuew = new JMenuItem("Continue w/ 100");
    mntmContinuew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, getMenuShortcutMask()));
    mntmContinuew.setEnabled(false);
    mntmContinuew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runPopGThreads(false, inputvals.genRun);
      }
    });
    mnRun.add(mntmContinuew);

    mntmContinue = new JMenuItem("Continue");
    mntmContinue.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_G, getMenuShortcutMask() | InputEvent.SHIFT_DOWN_MASK));
    mntmContinue.setEnabled(false);
    mntmContinue.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frmContinue = new JFrame();
        frmContinue.setSize(300, 130);
        frmContinue.setLocationRelativeTo(frmPopG);
        frmContinue.setTitle("enter a number");
        frmContinue.setVisible(true);
        frmContinue.setLayout(null);
        frmContinue.setResizable(false);

        lblContinueGenRun = new JLabel("How many generations to run?");
        lblContinueGenRun.setBounds(50, 10, 200, 14);
        frmContinue.add(lblContinueGenRun);

        txtContinueGenRun = new JTextField();
        txtContinueGenRun.setText(inputvals.genRun.toString());
        txtContinueGenRun.setBounds(35, 45, 200, 20);
        frmContinue.add(txtContinueGenRun);

        btnContinueGenRunOK = new JButton();
        btnContinueGenRunOK.setText("OK");
        btnContinueGenRunOK.setBounds(205, 75, 84, 25);
        btnContinueGenRunOK.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            int continueGenRun = Integer.parseInt(txtContinueGenRun.getText());
            frmContinue.dispose();
            inputvals.genRun = continueGenRun;
            mntmContinuew.setText("Continue w/ " + inputvals.genRun);
            runPopGThreads(false, continueGenRun);
          }
        });
        frmContinue.add(btnContinueGenRunOK);

        btnContinueGenCancel = new JButton();
        btnContinueGenCancel.setText("Cancel");
        btnContinueGenCancel.setBounds(125, 75, 84, 25);
        btnContinueGenCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            frmContinue.dispose();
            frmPopG.repaint();
          }
        });
        frmContinue.add(btnContinueGenCancel);
      }
    });
    mnRun.add(mntmContinue);

    mntmNewRun = new JMenuItem("New Run");
    mntmNewRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, getMenuShortcutMask()));
    mntmNewRun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frmPopGSettingsMenu = new JFrame();
        frmPopGSettingsMenu.setSize(480, 350);
        frmPopGSettingsMenu.setTitle("PopG Settings");
        frmPopGSettingsMenu.setLocationRelativeTo(frmPopG);
        frmPopGSettingsMenu.setVisible(true);
        frmPopGSettingsMenu.setLayout(null);

        lblPopSize = new JLabel("Population size:");
        lblPopSize.setBounds(10, 10, 275, 14);
        lblPopSize.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblPopSize);

        txtPopSize = new JTextField();
        txtPopSize.setText(inputvals.popSize.toString());
        txtPopSize.setBounds(295, 8, 151, 20);
        frmPopGSettingsMenu.add(txtPopSize);
        txtPopSize.setColumns(10);

        lblFitGenAA = new JLabel("Fitness of genotype AA:");
        lblFitGenAA.setBounds(10, 35, 275, 14);
        lblFitGenAA.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblFitGenAA);

        txtFitGenAA = new JTextField();
        txtFitGenAA.setText(inputvals.fitGenAA.toString());
        txtFitGenAA.setBounds(295, 33, 151, 20);
        frmPopGSettingsMenu.add(txtFitGenAA);
        txtFitGenAA.setColumns(10);

        lblFitGenAa = new JLabel("Fitness of genotype Aa:");
        lblFitGenAa.setBounds(10, 60, 275, 14);
        lblFitGenAa.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblFitGenAa);

        txtFitGenAa = new JTextField();
        txtFitGenAa.setText(inputvals.fitGenAa.toString());
        txtFitGenAa.setBounds(295, 58, 151, 20);
        frmPopGSettingsMenu.add(txtFitGenAa);
        txtFitGenAa.setColumns(10);

        lblFitGenaa = new JLabel("Fitness of genotype aa:");
        lblFitGenaa.setBounds(10, 85, 275, 14);
        lblFitGenaa.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblFitGenaa);

        txtFitGenaa = new JTextField();
        txtFitGenaa.setText(inputvals.fitGenaa.toString());
        txtFitGenaa.setBounds(295, 83, 151, 20);
        frmPopGSettingsMenu.add(txtFitGenaa);
        txtFitGenaa.setColumns(10);

        lblMutAa = new JLabel("Mutation from A to a:");
        lblMutAa.setBounds(10, 110, 275, 14);
        lblMutAa.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblMutAa);

        txtMutAa = new JTextField();
        txtMutAa.setText(inputvals.mutAa.toString());
        txtMutAa.setBounds(295, 108, 151, 20);
        frmPopGSettingsMenu.add(txtMutAa);
        txtMutAa.setColumns(10);

        lblMutaA = new JLabel("Mutation from a to A:");
        lblMutaA.setBounds(10, 135, 275, 14);
        lblMutaA.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblMutaA);

        txtMutaA = new JTextField();
        txtMutaA.setText(inputvals.mutaA.toString());
        txtMutaA.setBounds(295, 133, 151, 20);
        frmPopGSettingsMenu.add(txtMutaA);
        txtMutaA.setColumns(10);

        lblMigRate = new JLabel("Migration rate between populations:");
        lblMigRate.setBounds(10, 160, 275, 14);
        lblMigRate.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblMigRate);

        txtMigRate = new JTextField();
        txtMigRate.setText(inputvals.migRate.toString());
        txtMigRate.setBounds(295, 158, 151, 20);
        frmPopGSettingsMenu.add(txtMigRate);
        txtMigRate.setColumns(10);

        lblInitFreq = new JLabel("Initial freqency of allele A:");
        lblInitFreq.setBounds(10, 185, 275, 14);
        lblInitFreq.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblInitFreq);

        txtInitFreq = new JTextField();
        txtInitFreq.setText(inputvals.initFreq.toString());
        txtInitFreq.setBounds(295, 183, 151, 20);
        frmPopGSettingsMenu.add(txtInitFreq);
        txtInitFreq.setColumns(10);

        lblGenRun = new JLabel("Generations to run:");
        lblGenRun.setBounds(10, 210, 275, 14);
        lblGenRun.setHorizontalAlignment(SwingConstants.TRAILING);
        frmPopGSettingsMenu.add(lblGenRun);

        txtGenRun = new JTextField();
        txtGenRun.setText(inputvals.genRun.toString());
        txtGenRun.setBounds(295, 208, 151, 20);
        frmPopGSettingsMenu.add(txtGenRun);
        txtGenRun.setColumns(10);

        lblNumPop = new JLabel("Populations evolving simultaneously:");
        lblNumPop.setHorizontalAlignment(SwingConstants.TRAILING);
        lblNumPop.setBounds(10, 235, 275, 14);
        frmPopGSettingsMenu.add(lblNumPop);

        txtNumPop = new JTextField();
        txtNumPop.setText(inputvals.numPop.toString());
        txtNumPop.setBounds(295, 233, 151, 20);
        frmPopGSettingsMenu.add(txtNumPop);
        txtNumPop.setColumns(6);

        lblRandSeed = new JLabel("Random number seed:");
        lblRandSeed.setHorizontalAlignment(SwingConstants.TRAILING);
        lblRandSeed.setBounds(10, 260, 275, 14);
        frmPopGSettingsMenu.add(lblRandSeed);

        txtRandSeed = new JTextField();
        if (inputvals.genSeed) {
          txtRandSeed.setText("(Autogenerate)");
        } else {
          txtRandSeed.setText(inputvals.randSeed.toString());
        }
        txtRandSeed.setBounds(295, 258, 151, 20);
        frmPopGSettingsMenu.add(txtRandSeed);
        txtRandSeed.setColumns(6);

        btnDefaults = new JButton("Defaults");
        btnDefaults.setBounds(180, 285, 84, 25);
        btnDefaults.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            initInputVals();
            refreshSettingsFieldsFromInputVals();
          }
        });
        frmPopGSettingsMenu.add(btnDefaults);

        btnQuit = new JButton("Cancel");
        btnQuit.setBounds(270, 285, 84, 25);
        btnQuit.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            frmPopGSettingsMenu.dispose();
            displayPanel.repaint();
          }
        });
        frmPopGSettingsMenu.add(btnQuit);

        btnOK = new JButton("OK");
        btnOK.setBounds(362, 285, 84, 25);
        btnOK.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            genArray.clear();
            frmPopG.repaint();
            if (updateInputValsFromSettingsFields()) {
              startNewRunFromCurrentInputVals(true);
            }
          }
        });

        frmPopGSettingsMenu.add(btnOK);
      }
    });
    mnRun.add(mntmNewRun);

    mntmRestart = new JMenuItem("Restart");
    mntmRestart.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_R, getMenuShortcutMask() | InputEvent.SHIFT_DOWN_MASK));
    mntmRestart.setEnabled(false);
    mntmRestart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frmPopG.repaint();
        initializeRandomSeedFromInputVals();
        runPopGThreads(true, inputvals.genRun);
      }
    });
    mnRun.add(mntmRestart);

    mntmWholePlot = new JMenuItem("Display whole plot");
    mntmWholePlot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, getMenuShortcutMask()));
    mntmWholePlot.setEnabled(false);
    mntmWholePlot.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        beginGen = 0;
        frmPopG.repaint();
      }
    });
    mnRun.add(mntmWholePlot);

    frmPopG.setVisible(true);

    mntmPrint = new JMenuItem("Print");
    mntmPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, getMenuShortcutMask()));
    mntmPrint.setEnabled(false);
    mntmPrint.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        printPopG();
      }
    });

    mnFile.add(mntmPrint);
    frmPopG.setVisible(true);

    mntmAbout = new JMenuItem("About");
    mntmAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, getMenuShortcutMask()));
    mntmAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutWindow();
      }
    });
    mnFile.add(mntmAbout);

    frmPopG.setVisible(true);

    mntmQuit = new JMenuItem("Quit");
    mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, getMenuShortcutMask()));
    mntmQuit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    mnFile.add(mntmQuit);
    frmPopG.setVisible(true);

    if (options != null && options.screenshotPath != null) {
      pendingScreenshotPath = options.screenshotPath;
      screenshotCaptured = false;
    }

    if (options != null && options.autoStartNewRun) {
      runNewRunDirectly();
    }

    updateSaveMenuItemState();
  }

  private int getMenuShortcutMask() {
    return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
  }

  public boolean checkInputVals() {

    String msg;
    if (inputvals.popSize < 1) {
      msg = "Population size must be between 1 and 10000";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.popSize > 10000) {
      msg = "Population size must be between 1 and 10000";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.fitGenAA < 0) {
      msg = "Fitness of AA must be greater than or equal to 0";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.fitGenAa < 0) {
      msg = "Fitness of Aa must be greater than or equal to 0";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.fitGenaa < 0) {
      msg = "Fitness of aa must be greater than or equal to 0";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.mutAa < 0) {
      msg = "Mutation rate from A to a must be between 0 and 1";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.mutaA > 1) {
      msg = "Mutation rate from A to a must be between 0 and 1";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.initFreq < 0) {
      msg = "The Initial frequency of allele A must be between 0 and 1";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.initFreq > 1) {
      msg = "The Initial frequency of allele A must be between 0 and 1";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.genRun < 1) {
      msg = "Generations to run must be between 1 and 100000000";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.genRun > 100000000) {
      msg = "Generations to run must be between 1 and 100000000";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.numPop < 0) {
      msg = "Number of populations must be between 0 and 1000";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.numPop > 1000) {
      msg = "Number of populations must be between 0 and 1000";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (inputvals.migRate < 0) {
      msg = "Migration rate cannot be negative";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      return false;

    }
    return true;
  }

  protected void runPopGThreads(final Boolean newRun, final Integer genLim) {

    Thread popgRunThread = new Thread() {
      public void run() {
        calcPopG(newRun, genLim);
      }
    };
    popgRunThread.start();

    timer = new Timer(TIMELEN, this);
    timer.addActionListener(this);
    timer.start();
  }

  public void actionPerformed(ActionEvent e) {
    displayPanel.repaint();
  }

  private void initializeRandomSeedFromInputVals() {
    if (inputvals.genSeed) {
      numberSeed = new Random();
    } else {
      numberSeed = new Random(inputvals.randSeed);
    }
  }

  private void enableRunMenuItems() {
    mntmContinuew.setEnabled(true);
    mntmContinue.setEnabled(true);
    mntmRestart.setEnabled(true);
    mntmContinuew.setText("Continue w/ " + inputvals.genRun);
    mntmWholePlot.setEnabled(true);
    mntmPrint.setEnabled(true);
  }

  private void startNewRunFromCurrentInputVals(boolean closeSettingsWindow) {
    enableRunMenuItems();
    if (checkInputVals()) {
      canSaveParameters = true;
      updateSaveMenuItemState();
      if (closeSettingsWindow && frmPopGSettingsMenu != null) {
        frmPopGSettingsMenu.dispose();
      }
      runPopGThreads(true, inputvals.genRun);
    }
  }

  private boolean updateInputValsFromSettingsFields() {
    String msg;
    boolean runtests = true;
    try {
      inputvals.popSize = Integer.parseInt(txtPopSize.getText());
    } catch (NumberFormatException nfe) {
      msg = "Population Size must be an integer";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.fitGenAA = Double.parseDouble(txtFitGenAA.getText());
    } catch (NumberFormatException nfe) {
      msg = "Fitness of Genotype AA must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.fitGenAa = Double.parseDouble(txtFitGenAa.getText());
    } catch (NumberFormatException nfe) {
      msg = "Fitness of Genotype Aa must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.fitGenaa = Double.parseDouble(txtFitGenaa.getText());
    } catch (NumberFormatException nfe) {
      msg = "Fitness of Genotype aa must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.mutAa = Double.parseDouble(txtMutAa.getText());
    } catch (NumberFormatException nfe) {
      msg = "Mutations from A to a must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.mutaA = Double.parseDouble(txtMutaA.getText());
    } catch (NumberFormatException nfe) {
      msg = "Mutations from a to A must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.migRate = Double.parseDouble(txtMigRate.getText());
    } catch (NumberFormatException nfe) {
      msg = "Migration Rate must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.initFreq = Double.parseDouble(txtInitFreq.getText());
    } catch (NumberFormatException nfe) {
      msg = "Initial Frequency of A must be a number";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.genRun = Integer.parseInt(txtGenRun.getText());
    } catch (NumberFormatException nfe) {
      msg = "Generations to run must be an integer";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      inputvals.numPop = Integer.parseInt(txtNumPop.getText());
    } catch (NumberFormatException nfe) {
      msg = "Populations evolving must be an integer";
      JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      runtests = false;
    }

    try {
      long longVal = Long.parseLong(txtRandSeed.getText());
      inputvals.genSeed = false;
      inputvals.randSeed = longVal;
    } catch (NumberFormatException nfe) {
      inputvals.genSeed = true;
    }

    initializeRandomSeedFromInputVals();
    return runtests;
  }

  private void runNewRunDirectly() {
    genArray.clear();
    frmPopG.repaint();
    initializeRandomSeedFromInputVals();
    startNewRunFromCurrentInputVals(false);
  }

  void calcPopG(Boolean newRun, Integer genLim) {
    // random number test case
    /*
     * numberSeed = new Random();
     * genArray.clear();
     * ArrayList<Double> popArray;
     * for(int i = 0; i < inputvals.genRun; i++){
     * popArray = new ArrayList<Double>();
     * for(int j = 0; j <= inputvals.numPop; j++) {
     * //popArray.add((double) numberSeed.nextFloat());
     * double val = (double) numberSeed.nextFloat();
     * popArray.add(val);
     * //System.out.println("i: "+ i +" j: "+ j +" val: "+ val);
     * }
     * genArray.add(popArray);
     * }
     */

    ArrayList<Double> nextPopArray = new ArrayList<Double>();
    if (newRun) {
      genArray.clear();
      for (int j = 0; j <= inputvals.numPop; j++) {
        nextPopArray.add(inputvals.initFreq);
      }
      genArray.add(nextPopArray);
      beginGen = 0;
      endGen = inputvals.genRun;
    } else {
      beginGen = endGen;
      endGen += genLim;
    }

    int currentPopSize = inputvals.popSize * 2;

    double pbar;
    double p = 0.0;
    double q;
    double w;
    double pp1;
    double pp2;

    long nx;
    long ny;

    for (int i = beginGen; i < endGen; i++) {
      // calculate the mean frequency of AA over all the populations
      numFixedPops = 0;
      numLostPops = 0;
      pbar = 0.0;
      nextPopArray = new ArrayList<Double>();

      // count number of populations no longer active
      ArrayList<Double> popArray = genArray.get(i);
      for (int j = 1; j <= inputvals.numPop; j++) {
        double end = popArray.get(j);
        pbar += end;
        if (end <= 0.0) {
          numLostPops += 1;
        }
        if (end >= 1.0) {
          numFixedPops += 1;
        }
      }

      // for each population _________
      pbar /= inputvals.numPop;
      for (int j = 0; j <= inputvals.numPop; j++) { // get new frequency for population j
        p = popArray.get(j); // current gene frequency
        if (j > 0) {
          p = p * (1.0 - inputvals.migRate) + inputvals.migRate * pbar; // all but pop. 0 receive migrants
        }
        p = (1 - inputvals.mutAa) * p + inputvals.mutaA * (1 - p);
        // if genotype is not fixed calculate the new frequency
        if ((p > 0.0) && (p < 1.0)) {
          q = 1 - p;
          w = (p * p * inputvals.fitGenAA) + (2.0 * p * q * inputvals.fitGenAa)
              + (q * q * inputvals.fitGenaa); // get mean fitness
          pp1 = (p * p * inputvals.fitGenAA) / w; // get frequency of AA after sel.
          pp2 = (2.0 * p * q * inputvals.fitGenAa) / w; // get frequency of Aa after sel.
          if (j > 0) { // calculate the next generation
            nx = binomial(inputvals.popSize, pp1);
            // draw how many AA's survive
            if (pp1 < 1.0 && nx < inputvals.popSize) {
              ny = binomial((inputvals.popSize - nx), (pp2 / (1.0 - pp1))); // and Aa's too
            } else {
              ny = 0;
            }
            // compute new number of A's and make it a frequency
            nextPopArray.add(((nx * 2.0) + ny) / currentPopSize);
          } else { // calculate what the "true" average would be. What you would expect with an
                   // infinite population
            nextPopArray.add(pp1 + (pp2 / 2.0));
          }
        } else {
          if (p <= 0.0) {
            p = 0.0;
          } else {
            p = 1.0;
          }
          nextPopArray.add(p); // so we don't loose a population and mess up the plot
        }
      }
      genArray.add(nextPopArray);
    }

    // shut timer off and display the last of the data
    timer.stop();
    // frmPopG.repaint();
    displayPanel.repaint();
    captureAndExitIfRequested();

  }

  private void captureAndExitIfRequested() {
    if (pendingScreenshotPath == null || pendingScreenshotPath.isEmpty() || screenshotCaptured) {
      return;
    }

    screenshotCaptured = true;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          savePlotImageToPath(pendingScreenshotPath);
        } catch (IOException ioe) {
          System.err.println("Warning: could not save screenshot to '" + pendingScreenshotPath + "': "
              + ioe.getMessage());
        } finally {
          System.exit(0);
        }
      }
    });
  }

  long binomial(long n, double pp) {
    /*
     * binomial distribution
     * Parameters:
     * long n: number of trials
     * Double pp: the probability of heads per trial
     * Return value : the number of "heads"
     */
    long j;
    long bnl;
    bnl = 0;
    for (j = 1; j <= n; j++) {
      if (numberSeed.nextFloat() < pp) {
        bnl++;
      }
    }
    return bnl;
  }

  void initInputVals() {
    applyInputValsFromJsonText(DEFAULTS_JSON_TEXT);
  }

  private void refreshSettingsFieldsFromInputVals() {
    if (txtPopSize != null) {
      txtPopSize.setText(inputvals.popSize.toString());
    }
    if (txtFitGenAA != null) {
      txtFitGenAA.setText(inputvals.fitGenAA.toString());
    }
    if (txtFitGenAa != null) {
      txtFitGenAa.setText(inputvals.fitGenAa.toString());
    }
    if (txtFitGenaa != null) {
      txtFitGenaa.setText(inputvals.fitGenaa.toString());
    }
    if (txtMutAa != null) {
      txtMutAa.setText(inputvals.mutAa.toString());
    }
    if (txtMutaA != null) {
      txtMutaA.setText(inputvals.mutaA.toString());
    }
    if (txtMigRate != null) {
      txtMigRate.setText(inputvals.migRate.toString());
    }
    if (txtInitFreq != null) {
      txtInitFreq.setText(inputvals.initFreq.toString());
    }
    if (txtGenRun != null) {
      txtGenRun.setText(inputvals.genRun.toString());
    }
    if (txtNumPop != null) {
      txtNumPop.setText(inputvals.numPop.toString());
    }
    if (txtRandSeed != null) {
      if (inputvals.genSeed) {
        txtRandSeed.setText("(Autogenerate)");
      } else {
        txtRandSeed.setText(inputvals.randSeed.toString());
      }
    }
  }

  private void loadRunFromJsonChooser() {
    JFileChooser filechooser = new JFileChooser(filedir);
    filechooser.setAcceptAllFileFilterUsed(false);
    FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON File", "json");
    filechooser.addChoosableFileFilter(jsonFilter);
    filechooser.setFileFilter(jsonFilter);

    int result = filechooser.showOpenDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File selectedFile = filechooser.getSelectedFile();
    filedir = filechooser.getCurrentDirectory().getAbsolutePath();

    try {
      loadInputValsFromJson(selectedFile.getAbsolutePath());
      runNewRunDirectly();
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(null, "Could not load JSON file: " + ioe.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void loadInputValsFromJson(String jsonFilePath) throws IOException {
    String jsonText = Files.readString(Path.of(jsonFilePath), StandardCharsets.UTF_8);
    applyInputValsFromJsonText(jsonText);
    canSaveParameters = true;
    updateSaveMenuItemState();
  }

  private void updateSaveMenuItemState() {
    if (mntmSaveParams != null) {
      mntmSaveParams.setEnabled(canSaveParameters);
    }
  }

  private void saveInputValsToJsonChooser() {
    JFileChooser filechooser = new JFileChooser(filedir);
    filechooser.setSelectedFile(new File("popg-params.json"));
    filechooser.setAcceptAllFileFilterUsed(false);
    FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON File", "json");
    filechooser.addChoosableFileFilter(jsonFilter);
    filechooser.setFileFilter(jsonFilter);

    int result = filechooser.showSaveDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    filedir = filechooser.getCurrentDirectory().getAbsolutePath();
    String selectedPath = filechooser.getSelectedFile().getPath();
    if (!selectedPath.toLowerCase().endsWith(".json")) {
      selectedPath += ".json";
    }

    File saveFile = new File(selectedPath);
    if (saveFile.exists()) {
      int overwrite = JOptionPane.showConfirmDialog(
          this,
          "File already exists. Overwrite?",
          "Confirm overwrite",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
      if (overwrite != JOptionPane.YES_OPTION) {
        return;
      }
    }

    try {
      Files.writeString(saveFile.toPath(), buildInputValsJsonText(), StandardCharsets.UTF_8);
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(this, "Could not save JSON file: " + ioe.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private String buildInputValsJsonText() {
    return "{\n"
        + "  \"popSize\": " + inputvals.popSize + ",\n"
        + "  \"fitGenAA\": " + inputvals.fitGenAA + ",\n"
        + "  \"fitGenAa\": " + inputvals.fitGenAa + ",\n"
        + "  \"fitGenaa\": " + inputvals.fitGenaa + ",\n"
        + "  \"mutAa\": " + inputvals.mutAa + ",\n"
        + "  \"mutaA\": " + inputvals.mutaA + ",\n"
        + "  \"migRate\": " + inputvals.migRate + ",\n"
        + "  \"initFreq\": " + inputvals.initFreq + ",\n"
        + "  \"genRun\": " + inputvals.genRun + ",\n"
        + "  \"numPop\": " + inputvals.numPop + ",\n"
        + "  \"genSeed\": " + inputvals.genSeed + ",\n"
        + "  \"randSeed\": " + inputvals.randSeed + "\n"
        + "}\n";
  }

  private void applyInputValsFromJsonText(String jsonText) {
    setFieldFromJson(jsonText, "popSize", "-?\\d+", Integer::valueOf, value -> inputvals.popSize = value);
    setFieldFromJson(jsonText, "fitGenAA", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.fitGenAA = value);
    setFieldFromJson(jsonText, "fitGenAa", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.fitGenAa = value);
    setFieldFromJson(jsonText, "fitGenaa", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.fitGenaa = value);
    setFieldFromJson(jsonText, "mutAa", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.mutAa = value);
    setFieldFromJson(jsonText, "mutaA", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.mutaA = value);
    setFieldFromJson(jsonText, "migRate", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.migRate = value);
    setFieldFromJson(jsonText, "initFreq", "-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?", Double::valueOf,
        value -> inputvals.initFreq = value);
    setFieldFromJson(jsonText, "genRun", "-?\\d+", Integer::valueOf, value -> inputvals.genRun = value);
    setFieldFromJson(jsonText, "numPop", "-?\\d+", Integer::valueOf, value -> inputvals.numPop = value);
    setFieldFromJson(jsonText, "genSeed", "true|false", value -> Boolean.valueOf(value.toLowerCase()),
        value -> inputvals.genSeed = value);
    setFieldFromJson(jsonText, "randSeed", "-?\\d+", Long::valueOf, value -> inputvals.randSeed = value);
  }

  private <T> void setFieldFromJson(String jsonText, String fieldName, String valuePattern,
      Function<String, T> parser, Consumer<T> setter) {
    String rawValue = extractJsonFieldValue(jsonText, fieldName, valuePattern);
    if (rawValue != null) {
      setter.accept(parser.apply(rawValue));
    }
  }

  private String extractJsonFieldValue(String jsonText, String fieldName, String valuePattern) {
    Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*(" + valuePattern + ")",
        Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(jsonText);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  public void printPopG() {
    PrinterJob pj = PrinterJob.getPrinterJob();
    Book book = new Book();
    PageFormat documentPageFormat = new PageFormat();
    documentPageFormat.setOrientation(PageFormat.LANDSCAPE);
    book.append(new Document(), documentPageFormat);
    pj.setPageable(book);
    if (pj.printDialog()) {
      try {
        pj.print();
      } catch (Exception PrintException) {
        PrintException.printStackTrace();
      }
    }
  }

  private class Document implements Printable {
    public int print(Graphics g, PageFormat pageFormat, int page) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      xOffset = 100;
      yOffset = 40;
      xLen = (int) pageFormat.getImageableWidth() - 200;
      yLen = (int) pageFormat.getImageableHeight() - 130;
      Font titleFont = new Font("helvetica", Font.BOLD, 18);
      g2d.setFont(titleFont);
      g2d.drawString("PopG plot", xOffset + 150, yOffset - 20);
      plotPopG(g2d);
      return (PAGE_EXISTS);
    }
  }

  public void savePopG() {
    JFileChooser filechooser = new JFileChooser(filedir);
    filechooser.setSelectedFile(new File("popg"));

    // set up filter
    filechooser.setAcceptAllFileFilterUsed(false);
    FileNameExtensionFilter jpgfilter = new FileNameExtensionFilter("JPG Image", "jpg");
    FileNameExtensionFilter pngfilter = new FileNameExtensionFilter("PNG Image", "png");
    // FileNameExtensionFilter psfilter = new FileNameExtensionFilter("PS File",
    // "ps");
    filechooser.addChoosableFileFilter(jpgfilter);
    filechooser.addChoosableFileFilter(pngfilter);
    // filechooser.addChoosableFileFilter(psfilter);

    int result = filechooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      filedir = filechooser.getCurrentDirectory().getAbsolutePath();
      BufferedImage bi = new BufferedImage(frmPopG.getWidth(), frmPopG.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bi.createGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
      g2d.setColor(Color.BLACK);
      xOffset = 100;
      yOffset = 40;
      xLen = frmPopG.getWidth() - 200;
      yLen = frmPopG.getHeight() - 130;
      Font titleFont = new Font("helvetica", Font.BOLD, 18);
      g2d.setFont(titleFont);
      g2d.drawString("PopG plot", xOffset + 150, yOffset - 20);
      plotPopG(g2d);

      String curpath = filechooser.getSelectedFile().getPath();
      FileFilter ff = filechooser.getFileFilter();
      FileNameExtensionFilter extFilter = (FileNameExtensionFilter) ff;
      String ext = extFilter.getExtensions()[0];
      String fullpath;
      if (curpath.contains("." + ext)) {
        fullpath = curpath;
      } else {
        fullpath = curpath + "." + ext;
      }
      File saveFile = new File(fullpath);
      if (!saveFile.exists()) {
        try {
          saveFile.createNewFile();
        } catch (IOException e1) {
          System.out.println("Cannot create: " + fullpath);
          return;
        }
      }

      if (filechooser.getFileFilter() == jpgfilter) {
        try {
          ImageIO.write(bi, "jpg", saveFile);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else // if (filechooser.getFileFilter() == pngfilter)
      {
        try {
          ImageIO.write(bi, "png", saveFile);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      /*
       * else //if (filechooser.getFileFilter() == psfilter)
       * {
       * // this will need to call explicit postscript writing code (probably cloned
       * from old PopG code)
       * }
       */
    }
  }

  private void initializeLookAndFeelFromPlatform() {
    String lafClassName = UIManager.getSystemLookAndFeelClassName();
    if (lafClassName == null || lafClassName.isEmpty()) {
      lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
    }

    try {
      UIManager.setLookAndFeel(lafClassName);
      currentLookAndFeelClassName = lafClassName;
    } catch (Exception e) {
      try {
        String fallbackLaf = UIManager.getCrossPlatformLookAndFeelClassName();
        UIManager.setLookAndFeel(fallbackLaf);
        currentLookAndFeelClassName = fallbackLaf;
      } catch (Exception ignored) {
        if (UIManager.getLookAndFeel() != null) {
          currentLookAndFeelClassName = UIManager.getLookAndFeel().getClass().getName();
        }
      }
    }
  }

  private void populateLookAndFeelMenu() {
    mnLookAndFeel.removeAll();
    ButtonGroup group = new ButtonGroup();
    UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();

    if (lookAndFeelInfos == null || lookAndFeelInfos.length == 0) {
      JMenuItem noThemesItem = new JMenuItem("No themes available");
      noThemesItem.setEnabled(false);
      mnLookAndFeel.add(noThemesItem);
      return;
    }

    String selectedLookAndFeel = currentLookAndFeelClassName;
    if (selectedLookAndFeel == null && UIManager.getLookAndFeel() != null) {
      selectedLookAndFeel = UIManager.getLookAndFeel().getClass().getName();
    }

    for (int i = 0; i < lookAndFeelInfos.length; i++) {
      final String lafName = lookAndFeelInfos[i].getName();
      final String lafClassName = lookAndFeelInfos[i].getClassName();
      JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem(lafName);
      lafItem.setActionCommand(lafClassName);
      lafItem.setSelected(lafClassName.equals(selectedLookAndFeel));
      lafItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          applyLookAndFeelFromMenu(lafClassName);
        }
      });
      group.add(lafItem);
      mnLookAndFeel.add(lafItem);
    }
  }

  private void applyLookAndFeelFromMenu(String lafClassName) {
    String previousLookAndFeel = currentLookAndFeelClassName;
    if (lafClassName.equals(previousLookAndFeel)) {
      return;
    }

    try {
      UIManager.setLookAndFeel(lafClassName);
      currentLookAndFeelClassName = lafClassName;
      refreshLookAndFeelForOpenWindows();
      syncLookAndFeelMenuSelection(lafClassName);
    } catch (Exception e) {
      syncLookAndFeelMenuSelection(previousLookAndFeel);
      JOptionPane.showMessageDialog(this, "Could not apply look and feel: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void refreshLookAndFeelForOpenWindows() {
    refreshLookAndFeelForFrame(frmPopG);
    refreshLookAndFeelForFrame(frmPopGSettingsMenu);
    refreshLookAndFeelForFrame(frmContinue);
    refreshLookAndFeelForFrame(frmAbout);
    SwingUtilities.updateComponentTreeUI(this);
    revalidate();
    repaint();
  }

  private void refreshLookAndFeelForFrame(JFrame frame) {
    if (frame == null) {
      return;
    }
    SwingUtilities.updateComponentTreeUI(frame);
    frame.invalidate();
    frame.validate();
    frame.repaint();
  }

  private void syncLookAndFeelMenuSelection(String selectedClassName) {
    if (mnLookAndFeel == null || selectedClassName == null) {
      return;
    }

    for (int i = 0; i < mnLookAndFeel.getItemCount(); i++) {
      JMenuItem item = mnLookAndFeel.getItem(i);
      if (item instanceof JRadioButtonMenuItem) {
        JRadioButtonMenuItem radioItem = (JRadioButtonMenuItem) item;
        radioItem.setSelected(selectedClassName.equals(radioItem.getActionCommand()));
      }
    }
  }

  private void showAboutWindow() {
    if (frmAbout != null && frmAbout.isDisplayable()) {
      frmAbout.toFront();
      frmAbout.requestFocus();
      return;
    }

    frmAbout = new JFrame("About PopG");
    frmAbout.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frmAbout.setSize(700, 360);
    frmAbout.setResizable(false);
    frmAbout.setLocationRelativeTo(frmPopG);

    JPanel root = new JPanel(new BorderLayout(0, 12));
    root.setBorder(new EmptyBorder(18, 22, 18, 22));
    frmAbout.setContentPane(root);

    JLabel titleLabel = new JLabel("PopG");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
    JLabel subtitleLabel = new JLabel("Population Genetics Simulation");
    subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

    JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
    headerPanel.add(titleLabel, BorderLayout.NORTH);
    headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
    root.add(headerPanel, BorderLayout.NORTH);

    String aboutText = "Copyright 1993-2013. University of Washington and Joseph Felsenstein. All rights reserved.\n\n"
        + "Permission is granted to reproduce, perform, and modify this program.\n\n"
        + "Permission is granted to distribute or provide access to this program provided that:\n"
        + "1) this copyright notice is not removed\n"
        + "2) this program is not integrated with or called by any product or service that generates revenue\n"
        + "3) your distribution of this program is free\n\n"
        + "Any modified versions of this program that are distributed or accessible shall indicate that they are based on this program. "
        + "Educational institutions are granted permission to distribute this program to their students and staff for a fee to recover distribution costs.\n\n"
        + "Permission requests for any other distribution of this program should be directed to: license (at) u.washington.edu.";

    JTextArea aboutTextArea = new JTextArea(aboutText);
    aboutTextArea.setEditable(false);
    aboutTextArea.setLineWrap(true);
    aboutTextArea.setWrapStyleWord(true);
    aboutTextArea.setOpaque(false);
    aboutTextArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
    aboutTextArea.setBorder(new EmptyBorder(2, 0, 2, 0));

    JScrollPane aboutScrollPane = new JScrollPane(aboutTextArea);
    aboutScrollPane.setBorder(null);
    aboutScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    root.add(aboutScrollPane, BorderLayout.CENTER);

    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frmAbout.dispose();
        frmPopG.repaint();
      }
    });
    footerPanel.add(closeButton);
    root.add(footerPanel, BorderLayout.SOUTH);

    frmAbout.setVisible(true);
  }

  private void takeWindowScreenshot() {
    JFileChooser filechooser = new JFileChooser(filedir);
    filechooser.setSelectedFile(new File("popg-screenshot.png"));
    filechooser.setAcceptAllFileFilterUsed(false);
    FileNameExtensionFilter pngfilter = new FileNameExtensionFilter("PNG Image", "png");
    filechooser.addChoosableFileFilter(pngfilter);
    filechooser.setFileFilter(pngfilter);

    int result = filechooser.showSaveDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    filedir = filechooser.getCurrentDirectory().getAbsolutePath();
    String selectedPath = filechooser.getSelectedFile().getPath();
    if (!selectedPath.toLowerCase().endsWith(".png")) {
      selectedPath += ".png";
    }

    File saveFile = new File(selectedPath);
    if (saveFile.exists()) {
      int overwrite = JOptionPane.showConfirmDialog(
          this,
          "File already exists. Overwrite?",
          "Confirm overwrite",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
      if (overwrite != JOptionPane.YES_OPTION) {
        return;
      }
    }

    try {
      savePlotImageToPath(saveFile.getAbsolutePath());
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(this, "Could not save screenshot: " + ioe.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void savePlotImageToPath(String outputPath) throws IOException {
    BufferedImage bi = new BufferedImage(frmPopG.getWidth(), frmPopG.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bi.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
    g2d.setColor(Color.BLACK);
    xOffset = 100;
    yOffset = 40;
    xLen = frmPopG.getWidth() - 200;
    yLen = frmPopG.getHeight() - 130;
    Font titleFont = new Font("helvetica", Font.BOLD, 18);
    g2d.setFont(titleFont);
    g2d.drawString("PopG plot", xOffset + 150, yOffset - 20);
    plotPopG(g2d);
    g2d.dispose();

    File saveFile = new File(outputPath);
    File parentDir = saveFile.getAbsoluteFile().getParentFile();
    if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
      throw new IOException("cannot create directories for output path");
    }

    String ext = getImageExtension(saveFile.getName());
    if (ext == null) {
      ext = "png";
      saveFile = new File(outputPath + ".png");
    }

    if (!ImageIO.write(bi, ext, saveFile)) {
      throw new IOException("unsupported image format: " + ext);
    }
  }

  private String getImageExtension(String fileName) {
    int idx = fileName.lastIndexOf('.');
    if (idx < 0 || idx == fileName.length() - 1) {
      return null;
    }

    String ext = fileName.substring(idx + 1).toLowerCase();
    if ("png".equals(ext) || "jpg".equals(ext) || "jpeg".equals(ext)) {
      return ext;
    }
    return null;
  }

  void draw(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    xOffset = 100;
    yOffset = 20;
    xLen = frmPopG.getWidth() - 200;
    yLen = frmPopG.getHeight() - 130;
    g2d.setColor(getBackground());
    g2d.fillRect(0, 0, frmPopG.getWidth(), frmPopG.getHeight());
    g2d.setColor(Color.BLACK);

    plotPopG(g2d);
  }

  void plotPopG(Graphics2D g2d) {
    BasicStroke stroke1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
        new float[] { 4.0f, 4.0f }, 0.0f);
    BasicStroke stroke2 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f);
    BasicStroke stroke3 = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f);
    Font plotFont = new Font("helvetica", Font.PLAIN, 14);
    g2d.setFont(plotFont);

    // draw axes
    g2d.setStroke(stroke1);
    g2d.drawLine(xOffset, yOffset, xOffset + xLen, yOffset); // top x axis
    g2d.drawLine(xOffset, yOffset + yLen, xOffset + xLen, yOffset + yLen); // bottom x axis

    g2d.setStroke(stroke2);
    g2d.drawLine(xOffset, yOffset, xOffset, yOffset + yLen); // y axis

    int minX;
    int maxX;

    if (!genArray.isEmpty()) {
      minX = beginGen;
      maxX = endGen;
    } else {
      minX = 0;
      maxX = inputvals.genRun;
    }
    int xAxisLen = maxX - minX;

    int labelDiv;
    if (xAxisLen < 5) {
      labelDiv = xAxisLen;
    } else {
      labelDiv = 5;
    }
    int xLbl = xAxisLen / labelDiv;
    int xLabelTweak = 8;

    // draw ticks and labels on x axis
    int lblpos = 0;
    while (lblpos <= xAxisLen) {
      int tickXCor = (int) ((double) xLen / xAxisLen * lblpos);
      g2d.drawLine(xOffset + tickXCor, yOffset + yLen - 7, xOffset + tickXCor, yOffset + yLen + 7);
      g2d.drawString(String.valueOf(minX + lblpos), xOffset + tickXCor - xLabelTweak, yOffset + yLen + 23);
      lblpos += xLbl;
    }

    if ((xLbl > 1) && ((minX + lblpos - xLbl) != maxX)) {
      int xRmdr = (maxX - minX) % 5;
      if (xRmdr != 0) {
        // display last x label below
        g2d.drawLine(xOffset + xLen, yOffset + yLen - 7, xOffset + xLen, yOffset + yLen + 7);
        g2d.drawString(String.valueOf(maxX), xOffset + xLen - xLabelTweak, yOffset + yLen + 34);

      }
    }

    // draw ticks and labels on y axis
    int yLabelTweak = 5;
    for (int i = 0; i < 11; i++) {
      g2d.drawLine(xOffset - 12, yOffset + (int) (i * yLen / 10), xOffset, yOffset + (int) (i * yLen / 10));
      g2d.drawString(String.format("%.1f", (double) (10 - i) * 0.1), xOffset - 35,
          yOffset + (int) (i * yLen / 10) + yLabelTweak);
    }

    // write the labels
    g2d.drawString("P(A)", xOffset / 2 - 3 * xLabelTweak, yOffset + (int) (5 * yLen / 10) + yLabelTweak);
    g2d.drawString("Generation", xOffset + (int) (2 * xLen / 5) + xLabelTweak, yOffset + yLen + 40);
    String msg = "Fixed: " + numFixedPops;
    g2d.drawString(msg, xOffset + xLen + 10, yOffset + yLabelTweak);
    msg = "Lost: " + numLostPops;
    g2d.drawString(msg, xOffset + xLen + 10, yOffset + yLen + yLabelTweak);

    // translate data into screen coordinates and display
    ArrayList<Double> popArray = new ArrayList<Double>();
    int startXCor;
    int startYCor;
    int endYCor;

    if (!genArray.isEmpty()) {
      int beginXCor = xOffset;
      int endXCor = beginXCor;
      int beginYCor = ((int) (yLen * (1 - inputvals.initFreq))) + yOffset;
      ;

      int firstlen = genArray.get(0).size();
      // skip j = 0 because it is rendered last so it will be on top
      for (int j = 1; j <= inputvals.numPop; j++) {

        startXCor = beginXCor;
        startYCor = beginYCor;

        for (int i = minX; i <= maxX; i++) {
          if (i < genArray.size()) {
            popArray = genArray.get(i);
            if ((!popArray.isEmpty()) && (popArray.size() == firstlen)) {
              double yval = popArray.get(j);
              if (i == minX) {
                startXCor = xOffset;
                startYCor = ((int) (yLen * (1 - yval))) + yOffset;
              } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(stroke2);
                endXCor = xOffset + (int) (((float) xLen / (float) xAxisLen) * (float) (i - minX));
                endYCor = ((int) (yLen * (1 - yval))) + yOffset;
                g2d.drawLine(startXCor, startYCor, endXCor, endYCor);
                startXCor = endXCor;
                startYCor = endYCor;
              }
            }
          }
        }
      }

      // now render j==0 so it will be on top
      int j0startXCor = beginXCor;
      int j0startYCor = beginYCor;
      int j0endXCor;
      int j0endYCor;
      Color j0color = new Color(135, 206, 235);
      for (int i = minX; i <= maxX; i++) {
        if (i < genArray.size()) {
          popArray = genArray.get(i);
          if (!popArray.isEmpty()) {
            double yval = popArray.get(0);
            if (i == minX) {
              j0startXCor = xOffset;
              j0startYCor = ((int) (yLen * (1 - yval))) + yOffset;
            } else {
              g2d.setColor(j0color);
              g2d.setStroke(stroke3);
              j0endXCor = xOffset + (int) (((float) xLen / (float) xAxisLen) * (float) (i - minX));
              j0endYCor = ((int) (yLen * (1 - yval))) + yOffset;
              g2d.drawLine(j0startXCor, j0startYCor, j0endXCor, j0endYCor);
              j0startXCor = j0endXCor;
              j0startYCor = j0endYCor;
            }
          }
        }
      }

      // create legend
      int lnStartX = (int) (xLen / 5 - 20);
      int lnStartY = yOffset + yLen + 34;
      g2d.setColor(Color.BLACK);
      g2d.setStroke(stroke2);
      g2d.drawLine(lnStartX, lnStartY, lnStartX + 20, lnStartY);
      g2d.drawString("evolving populations", lnStartX + 30, lnStartY + 6);
      lnStartY += 14;
      g2d.drawString("with no drift", lnStartX + 30, lnStartY + 6);
      g2d.setColor(j0color);
      g2d.setStroke(stroke3);
      g2d.drawLine(lnStartX, lnStartY, lnStartX + 20, lnStartY);
    }
  }
}

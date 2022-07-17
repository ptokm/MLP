package mlp;

import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Frame extends JFrame{
    // Fields for GUI
    private final MenuBar menuBar;
    private final Menu menuMenu, datasetMenu, educationMenu;
    private final MenuItem[] menuItems, datasetItems, educationItems;
    private static JLabel label;
    private final String information, about;
    // Fields for training
    private Algorithm _algorithm = new Algorithm();
    private ArrayList <ArrayList <Double>> _patterns = new ArrayList<>();
    private ArrayList <ArrayList <Double>> _testPatterns = new ArrayList<>();
    private boolean _canTrainData;
    private String _currentFileName;
    
    Frame(String title) {
        // Configuration of display window
        super(title);
        setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        // A flow layout arranges components in a left-to-right flow, 
        // much like lines of text in a paragraph
        setLayout(new FlowLayout());
        
        //Menu configuration
        menuBar = new MenuBar();
        menuMenu = new Menu("MENU");
        datasetMenu = new Menu("DATASETS");
        educationMenu = new Menu("TRAIN");
        
        menuItems = new MenuItem[2];
        menuItems[0] = new MenuItem("Home");
        menuItems[1] = new MenuItem("About");
        for (short i=0; i<menuItems.length; i++) {
            menuMenu.add(menuItems[i]);
        }
        
        datasetItems = new MenuItem[4];
        datasetItems[0] = new MenuItem("Load ionosphere train dataset");
        datasetItems[1] = new MenuItem("Load ionosphere test dataset");
        datasetItems[2] = new MenuItem("Load train dataset");
        datasetItems[3] = new MenuItem("Load test dataset");
        for (short i=0; i<datasetItems.length; i++)
            datasetMenu.add(datasetItems[i]);
        
        educationItems = new MenuItem[1];
        educationItems[0] = new MenuItem("Back Propagation");
        educationMenu.add(educationItems[0]);
        
        menuBar.add(menuMenu);
        menuBar.add(datasetMenu);
        menuBar.add(educationMenu);
        setMenuBar(menuBar);
        
        information = "<html><h2>Info</h2></html>";
        
        about = "<html><h2>About</h2></html>";
        
        //Instructions for users
        label = new JLabel();
        setTextLabel(information);
        this.add(label);
    }
    
    private void chooseTrainDataset() {
        if (!this._canTrainData) {
            //Load train dataset
            setTextLabel("<html><h2 align = 'center'>Loading patterns..</h2></html>");

            //Permisions for MAC devices to see files in Download folder
            System.setProperty("apple.awt.fileDialogForDirectories", "true");

            //Prompt the user to choose a .txt file from his system
            JFileChooser chooser=new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                this.loadTrainDataset(filename);
            } else {
                this._currentFileName = null;
                //The user clicks on Cancel button
                //when we suggest him to select a file from his system
                setTextLabel("<html><br/><br/><h2 align = 'center'>Data upload canceled</h2></html>");

                String text = "Want to load file now?";
                String title = "You cancelled loading..";
                int optionType = JOptionPane.OK_CANCEL_OPTION;
                int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                if (result == JOptionPane.OK_OPTION) {
                    setTextLabel("<html><h2>Load dataset...</h2></html>");
                    Frame.this.chooseTrainDataset();     
                }
            }
        } else {
            String text = "Want to load another file now?";
            String title = "Loading..";
            int optionType = JOptionPane.OK_CANCEL_OPTION;
            int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
            if (result == JOptionPane.OK_OPTION) {
                this._canTrainData = false;
                setTextLabel("<html><h2>Load dataset...</h2></html>");
                Frame.this.chooseTrainDataset();     
            } 
        }
    }
    
    private void chooseTestDataset() {
        if (this._canTrainData) {
            //Load test dataset
            setTextLabel("<html><h2 align = 'center'>Loading patterns..</h2></html>");

            //Permisions for MAC devices to see files in Download folder
            System.setProperty("apple.awt.fileDialogForDirectories", "true");

            //Prompt the user to choose a .txt file from his system
            JFileChooser chooser=new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                this.loadTestDataset(filename);
            } else {
                //The user clicks on Cancel button
                //when we suggest him to select a file from his system
                setTextLabel("<html><br/><br/><h2 align = 'center'>Data upload canceled</h2></html>");

                String text = "Want to load file now?";
                String title = "You cancelled loading..";
                int optionType = JOptionPane.OK_CANCEL_OPTION;
                int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                if (result == JOptionPane.OK_OPTION) {
                    setTextLabel("<html><h2>Load dataset...</h2></html>");
                    chooseTestDataset();     
                }
            }
        } else {
            setTextLabel("<html><h2>Unable to load test data because you have not <br/> trained the network before</h2></html>");
        }
    }
    
    private void loadTrainDataset(String filename) {
        this._currentFileName = filename;
        boolean isValid = true;
        ArrayList <ArrayList <Double>> patterns = new ArrayList<>();
        int dimension = -1;
        try {
            FileReader file = new FileReader(filename);
            try (Scanner in = new Scanner(file)) {
                int i = 0;
                //Read the file line-by-line
                while(in.hasNextLine())  {
                    if (isValid) {
                        String line=in.nextLine();

                        ArrayList <Double> newPattern = new ArrayList<>();
                        String[] characteristics = line.split(",");
                        if (i == 0) {
                            dimension = characteristics.length;
                            for (String characteristic : characteristics) {
                                newPattern.add(Double.parseDouble(characteristic));
                            }
                            patterns.add(newPattern);
                        }else if (characteristics.length == dimension) {
                            for (String characteristic : characteristics) {
                                newPattern.add(Double.parseDouble(characteristic));
                            }
                            patterns.add(newPattern);
                        }else {
                            isValid = false;
                            this._currentFileName = null;
                        }
                    }
                }

                if (isValid) {
                    this._canTrainData = true;
                    this._patterns = patterns;
                    setTextLabel("<html><h2 align = 'center'>Ready Data<br/>Go to Train</h2></html>");
                } else {
                    setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
                }
            }
        }catch (FileNotFoundException | NumberFormatException ex) {
            setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
            isValid = false;
            this._currentFileName = null;
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadTestDataset(String filename) {
        ArrayList <ArrayList <Double>> patterns = new ArrayList<>();
        int dimension = this._patterns.get(0).size();
        boolean isValid = true;
            
        try {
            FileReader file = new FileReader(filename);
            try (Scanner in = new Scanner(file)) {
                //Read the file line-by-line
                while(in.hasNextLine())  {
                    if (isValid) {
                        String line=in.nextLine();

                        ArrayList <Double> newPattern = new ArrayList<>();
                        String[] characteristics = line.split(",");
                        if (characteristics.length == dimension) {
                            for (String characteristic : characteristics) {
                                newPattern.add(Double.parseDouble(characteristic));
                            }
                            patterns.add(newPattern);
                        }else {
                            isValid = false;
                        }
                    }
                }

                if (isValid) {
                    this._testPatterns = patterns;
                    this._algorithm.setTestPatterns(this._testPatterns);
                    double testError = this._algorithm.getTestError();
                    setTextLabel("<html><h2 align = 'center'>The test dataset has "+testError + " train error</h2></html>");
                } else {
                    setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
                }
            }
        } catch (FileNotFoundException | NumberFormatException ex) {
            setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
            isValid = false;
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void train() {
        if (this._canTrainData) {
            int nodes = 1;
            int maxEpoches = 10;
            this._algorithm = new Algorithm(this._patterns, nodes, maxEpoches);
            double train = this._algorithm.train();
            if (train != 0.0) {
                setTextLabel("<html><h2>Trained the train dataset: " +this._currentFileName +"<br/> with train error: " +train+" </h2></html>");
            }else {
                 setTextLabel("<html><h2>Cannot train the train dataset: " +this._currentFileName +"</h2></html>");
            }
        } else {
            setTextLabel("<html><h2>Cannot train</h2></html>");
        }
    }

    //Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Home"                           -> setTextLabel(information);
                case "Load ionosphere train dataset"  -> loadTrainDataset("ionosphere_perceptron.train");
                case "Load ionosphere test dataset"   -> loadTestDataset("ionosphere_perceptron.test");
                case "Load train dataset"             -> chooseTrainDataset();
                case "Load test dataset"              -> chooseTestDataset();
                case "About"                          -> setTextLabel(about);
                case "Back Propagation"               -> train();
            }
        }
        else
            super.action(event,obj);
        return true;
    }
    
    private static void setTextLabel(String text){
        label.setText(text);
    }
    
}
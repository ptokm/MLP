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
    private final Menu menuMenu, datasetMenu, educationMenu, educateNewPatternMenu;
    private final MenuItem[] menuItems, datasetItems, educationItems, educateNewPatternItems;
    private static JLabel label;
    private final String information, about;
    // Fields for training
    private Algorithm _algorithm;
    private ArrayList <ArrayList <Double>> _testPatterns = new ArrayList<>();
    private boolean _canTrainData, _trainedData;
    private String _currentFileName;
    private boolean _defaultIonosphereDatasetLoaded, _defaultLiverPerceptronDatasetLoaded;
    
    Frame(String title) {
        // Configuration of display window
        super(title);
        setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        // A flow layout arranges components in a left-to-right flow, 
        // much like lines of text in a paragraph
        setLayout(new FlowLayout());
        
        // Menu configuration
        menuBar = new MenuBar();
        menuMenu = new Menu("MENU");
        datasetMenu = new Menu("DATASETS");
        educationMenu = new Menu("TRAINING ALGORITHMS");
        educateNewPatternMenu = new Menu("TRAIN A NEW PATTERN");
        
        menuItems = new MenuItem[2];
        menuItems[0] = new MenuItem("Home");
        menuItems[1] = new MenuItem("About");
        for (short i=0; i<menuItems.length; i++) {
            menuMenu.add(menuItems[i]);
        }
        
        datasetItems = new MenuItem[6];
        datasetItems[0] = new MenuItem("Load ionosphere train dataset");
        datasetItems[1] = new MenuItem("Load ionosphere test dataset");
        datasetItems[2] = new MenuItem("Load liver_perceptron train dataset");
        datasetItems[3] = new MenuItem("Load liver_perceptron test dataset");
        datasetItems[4] = new MenuItem("Load train dataset");
        datasetItems[5] = new MenuItem("Load test dataset");
        for (short i=0; i<datasetItems.length; i++)
            datasetMenu.add(datasetItems[i]);
        
        educationItems = new MenuItem[1];
        educationItems[0] = new MenuItem("Back Propagation");
        educationMenu.add(educationItems[0]);
        
        educateNewPatternItems = new MenuItem[1];
        educateNewPatternItems[0] = new MenuItem("Load file with new patterns to train");
        educateNewPatternMenu.add(educateNewPatternItems[0]);
        
        menuBar.add(menuMenu);
        menuBar.add(datasetMenu);
        menuBar.add(educationMenu);
        menuBar.add(educateNewPatternMenu);
        setMenuBar(menuBar);
        
        information = "<html><h2>Info</h2></html>";
        
        about = "<html><h2>About</h2></html>";
        
        // Instructions for users
        label = new JLabel();
        setTextLabel(information);
        this.add(label);
    }
    
    private void chooseTrainDataset() {
        if (!this._canTrainData) {
            // Load train dataset
            setTextLabel("<html><h2 align = 'center'>Loading patterns..</h2></html>");

            // Permisions for MAC devices to see files in Download folder
            System.setProperty("apple.awt.fileDialogForDirectories", "true");

            // Prompt the user to choose a .txt file from his system
            JFileChooser chooser=new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                this.loadTrainDataset(filename);
            } else {
                this._currentFileName = null;
                // The user clicks on Cancel button
                // when we suggest him to select a file from his system
                setTextLabel("<html><br/><br/><h2 align = 'center'>Data upload canceled</h2></html>");

                String text = "Want to load file now?";
                String title = "You cancelled loading..";
                int optionType = JOptionPane.OK_CANCEL_OPTION;
                int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
                if (result == JOptionPane.OK_OPTION) {
                    setTextLabel("<html><h2>Load dataset...</h2></html>");
                    this.chooseTrainDataset();     
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
                this.chooseTrainDataset();     
            } 
        }
    }
    
    private void chooseTestDataset() {
        if (this._canTrainData) {
            // Load test dataset
            setTextLabel("<html><h2 align = 'center'>Loading patterns..</h2></html>");

            // Permisions for MAC devices to see files in Download folder
            System.setProperty("apple.awt.fileDialogForDirectories", "true");

            // Prompt the user to choose a .txt file from his system
            JFileChooser chooser=new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                this.loadTestDataset(filename);
            } else {
                // The user clicks on Cancel button
                // when we suggest him to select a file from his system
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
                // Read the file line-by-line
                while(in.hasNextLine())  {
                    if (isValid) {
                        ConfigureAlgorithm.reset();
                        String line=in.nextLine();

                        if (line.startsWith("#")) {
                            String[] parts = line.split(":");
                            switch(parts[0]) {
                                case "# Nodes" -> {
                                    try{
                                        ConfigureAlgorithm.setNodes(Integer.parseInt(parts[1].trim()));
                                    }
                                    catch (NumberFormatException ex){
                                        ConfigureAlgorithm.setNodes(1);
                                    }
                                    break;
                                }
                                case "# Max Epoches" -> {
                                    try{
                                        ConfigureAlgorithm.setMaxEpoches(Integer.parseInt(parts[1].trim()));
                                    }
                                    catch (NumberFormatException ex){
                                        ConfigureAlgorithm.setMaxEpoches(100);
                                    }
                                    break;
                                }
                                case "# Learning rate" -> {
                                    try{
                                        ConfigureAlgorithm.setLearning_rate(Double.parseDouble(parts[1].trim()));
                                    }
                                    catch (NumberFormatException ex){
                                        ConfigureAlgorithm.setLearning_rate(0.01);
                                    }
                                    
                                    break;
                                }
                                case "# Initialize weights" -> {
                                    if (parts[1].trim().equals("1.0") || parts[1].trim().equals("0.0"))
                                        ConfigureAlgorithm.setInitializeWeightsOption(Double.parseDouble(parts[1].trim()));
                                    else
                                        ConfigureAlgorithm.setInitializeWeightsOption(1.0);
                                    break;
                                }
                            }
                            continue;
                        }
                        
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
                    Dataset.setPatterns(patterns);
                    
                    switch (filename) {
                        case "ionosphere_perceptron.train" -> {
                            this._defaultLiverPerceptronDatasetLoaded = false;
                            this._defaultIonosphereDatasetLoaded = true;
                        }
                        case "liver_perceptron.test" -> {
                            this._defaultLiverPerceptronDatasetLoaded = true;
                            this._defaultIonosphereDatasetLoaded = false;
                        }
                        default -> {
                            this._defaultLiverPerceptronDatasetLoaded = false;
                            this._defaultIonosphereDatasetLoaded = false;
                        }
                    }      
                    setTextLabel("<html><h2 align = 'center'>Ready Data<br/>Go to Train</h2></html>");
                } else {
                    this._defaultLiverPerceptronDatasetLoaded = false;
                    this._defaultIonosphereDatasetLoaded = false;
                    ConfigureAlgorithm.reset();
                    setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
                }
            }
        }catch (FileNotFoundException | NumberFormatException ex) {
            setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
            isValid = false;
            this._defaultLiverPerceptronDatasetLoaded = false;
            this._defaultIonosphereDatasetLoaded = false;
            this._currentFileName = null;
            ConfigureAlgorithm.reset();
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadTestDataset(String filename) {
        if (!this._trainedData || Dataset.getPatterns() == null || Dataset.getPatterns().isEmpty() ) {
            setTextLabel("<html><h2>Unable to load test data because you have not <br/> trained the network before</h2></html>");
            return;
        }
        
        switch (filename) {
            case "ionosphere_perceptron.test" -> {
                if (!this._defaultIonosphereDatasetLoaded) {
                    setTextLabel("<html><h2>Need to train ionosphere_perceptron.train before</h2></html>");
                    return;
                }
            }
            case "liver_perceptron.test" -> {
                 if (!this._defaultLiverPerceptronDatasetLoaded) {
                    setTextLabel("<html><h2>Need to train liver_perceptron.train before</h2></html>");
                    return;
                }
            }

        }
        
        ArrayList <ArrayList <Double>> patterns = new ArrayList<>();
        int dimension = Dataset.getPatterns().get(0).size();
        boolean isValid = true;
            
        try {
            FileReader file = new FileReader(filename);
            try (Scanner in = new Scanner(file)) {
                // Read the file line-by-line
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
                    setTextLabel("<html><h2 align = 'center'>The test dataset has " + testError + " train error</h2></html>");
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
            this._algorithm = new Algorithm(nodes, maxEpoches);
            double train = this._algorithm.train();
            if (train != 0.0) {
                this._trainedData = true;
                setTextLabel("<html><h2>Trained the train dataset: " + this._currentFileName + "<br/> with train error: " + train + " </h2></html>");
            }else {
                 setTextLabel("<html><h2>Cannot train the train dataset: " + this._currentFileName + "</h2></html>");
            }
        } else {
            setTextLabel("<html><h2>Cannot train</h2></html>");
        }
    }

    private void trainNewPatterns() {
        if (!this._trainedData) {
            setTextLabel("<html><h2>Need to train a dataset first</h2></html>");
            return;
        }
        
        ArrayList <Double> calculatedOutput = new ArrayList <>();
        // Load new patterns dataset
        setTextLabel("<html><h2 align = 'center'>Loading new patterns..</h2></html>");

        // Permisions for MAC devices to see files in Download folder
        System.setProperty("apple.awt.fileDialogForDirectories", "true");

        // Prompt the user to choose a .txt file from his system
        JFileChooser chooser=new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            
            try {
                FileReader file = new FileReader(filename);
                try (Scanner in = new Scanner(file)) {
                    int i = 0;
                    // Read the file line-by-line
                    while(in.hasNextLine())  {
                        String line = in.nextLine();
                        ArrayList <Double> newPattern = new ArrayList<>();
                        
                        String[] characteristics = line.split(",");
                        for (String characteristic : characteristics) {
                            newPattern.add(Double.parseDouble(characteristic.trim()));
                        }
                        calculatedOutput.add(this._algorithm.trainNewPattern(newPattern));
                        
                    }
                }
            }catch (FileNotFoundException | NumberFormatException ex) {
                setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
            }
            
            calculatedOutput.forEach(element -> {
                if (element == -1.0) {
                    setTextLabel("<html><h2>Something went wrong!</h2></html>");
                    return;
                }
            });
            
            String calculatedClasses = "<html><h2><ul>";
            for (int i = 0; i < calculatedOutput.size(); i++) {
                calculatedClasses += "<li> Pattern: " + i + " belongs in class: " + calculatedOutput.get(i) + "</li>";
            }
            calculatedClasses += "</ul></h2></html>";
            
            setTextLabel(calculatedClasses);
        } else {
            // The user clicks on Cancel button
            // when we suggest him to select a file from his system
            setTextLabel("<html><br/><br/><h2 align = 'center'>New patterns upload canceled</h2></html>");

            String text = "Want to load file now?";
            String title = "You cancelled loading..";
            int optionType = JOptionPane.OK_CANCEL_OPTION;
            int result = JOptionPane.showConfirmDialog(null, text, title, optionType);
            if (result == JOptionPane.OK_OPTION) {
                setTextLabel("<html><h2>Load new patterns dataset...</h2></html>");
                this.trainNewPatterns();     
            }
        } 
    }
    
    // Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Home"                                 -> setTextLabel(information);
                case "About"                                -> setTextLabel(about);
                case "Load ionosphere train dataset"        -> loadTrainDataset("ionosphere_perceptron.train");
                case "Load ionosphere test dataset"         -> loadTestDataset("ionosphere_perceptron.test");
                case "Load liver_perceptron train dataset"  -> loadTrainDataset("liver_perceptron.train");
                case "Load liver_perceptron test dataset"   -> loadTestDataset("liver_perceptron.test");
                case "Load train dataset"                   -> chooseTrainDataset();
                case "Load test dataset"                    -> chooseTestDataset();
                case "Back Propagation"                     -> train();
                case "Load file with new patterns to train" -> trainNewPatterns();
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
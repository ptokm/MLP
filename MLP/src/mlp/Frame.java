package mlp;

import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Frame extends JFrame{
    //Fields for GUI
    private MenuBar menuBar = null;
    private Menu menu = null, dataset = null, education = null;
    private MenuItem[] menuItems = null, datasetItems = null, educationItems = null;
    private static JLabel label = null;
    private final String information, about;
    
    Frame(String title) {
        //Configuration of display window
        super(title);
        setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        // A flow layout arranges components in a left-to-right flow, 
        // much like lines of text in a paragraph
        setLayout(new FlowLayout());
        
        //Menu configuration
        menuBar = new MenuBar();
        menu = new Menu("MENU");
        dataset = new Menu("DATASETS");
        education = new Menu("TRAIN");
        
        menuItems = new MenuItem[2];
        menuItems[0] = new MenuItem("Home");
        menuItems[1] = new MenuItem("About");
        for (short i=0; i<menuItems.length; i++) {
            menu.add(menuItems[i]);
        }
        
        datasetItems = new MenuItem[1];
        datasetItems[0] = new MenuItem("Load dataset");
        for (short i=0; i<datasetItems.length; i++)
            dataset.add(datasetItems[i]);
        
        educationItems = new MenuItem[1];
        educationItems[0] = new MenuItem("Train");
        education.add(educationItems[0]);
        
        menuBar.add(menu);
        menuBar.add(dataset);
        menuBar.add(education);
        setMenuBar(menuBar);
        
        information = "<html><h2>Info</h2></html>";
        
        about = "<html><h2>About</h2></html>";
        
        //Instructions for users
        label = new JLabel();
        setTextLabel(information);
        this.add(label);
    }
    
    private void loadDataset() {}
    
    private void train() {}
    
    //Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Home" -> setTextLabel(information);
                case "Load dataset" ->  loadDataset();
                case "About" -> setTextLabel(about);
                case "Train" -> train();
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
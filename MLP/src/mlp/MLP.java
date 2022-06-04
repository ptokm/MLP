package mlp;

import javax.swing.WindowConstants;

public class MLP {

    public static void main(String[] args) {
        Frame frame = new Frame("Multilayer Perceptron");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(600, 200, 750, 600); //.setBounds(x, y, width, height)
        frame.show();
    }
    
}

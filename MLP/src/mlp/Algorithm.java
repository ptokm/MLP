package mlp;

import java.util.ArrayList;

public class Algorithm {
    private ArrayList <ArrayList <Double>> _patterns = new ArrayList<>(); // The last column is for the desired output
    private ArrayList <ArrayList <Double>> _testPatterns = new ArrayList<>();
    private ArrayList <Double> _weights = new ArrayList<>();
    private ArrayList <Double> _uniqueOutputClasses = new ArrayList<>();
    private int _dimension = -1;
    private int _nodes = 1;
    private final int _maxEpoches;
    
    Algorithm() {
        this._patterns = new ArrayList<>();
        this._dimension = -1;
        this._nodes = 1;
        this._maxEpoches = 0;
    }
    
    Algorithm(ArrayList <ArrayList <Double>> patterns, int nodes, int maxEpoches) {
        this._patterns = patterns;
        this._dimension = this._patterns.get(0).size() - 1;
        this._nodes = nodes;
        this._maxEpoches = maxEpoches;
    }
    
    public void initializeWeights() {
        int countOfWeights = (this._dimension + 2) * this._nodes;
        
        for (int i = 0; i < countOfWeights; i++) {
            this._weights.add(1.0);
        }
    }
    
    public void findUniqueClasses() {
        this._uniqueOutputClasses = new ArrayList<>();
                
        for (int i = 0; i < this._patterns.size(); i++) {
            if (this._uniqueOutputClasses.isEmpty())
                this._uniqueOutputClasses.add(this._patterns.get(i).get(this._dimension));
            else {
                if (this._uniqueOutputClasses.indexOf(this._patterns.get(i).get(this._dimension)) == -1) {
                    this._uniqueOutputClasses.add(this._patterns.get(i).get(this._dimension));
                }
            }
        }
    }
    
    public double sig(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    public double getOutput(ArrayList <Double> pattern) {
        double sum = 0.0;
        
        for (int i = 0; i < this._nodes; i++) {
            double arg = 0.0;
            
            for (int j = 0; j < this._dimension; j++) {
                int posj = (this._dimension + 2) * (i + 1) - (this._dimension + 1) + (j + 1);
                arg += this._weights.get(posj - 1) * pattern.get((j+1) - 1);
            }
           
            int posbias = (this._dimension + 2) * (i+1);
            arg += this._weights.get(posbias - 1);
            int pos = (this._dimension + 2) * (i + 1) - (this._dimension + 1);
            sum += this._weights.get(pos - 1) * sig (arg);
        }
        
        return sum;
    }
    
    public double getTrainError() {
        double sum = 0.0;
        
        for (int i = 0; i < this._patterns.size(); i++) {
            double yx = this._patterns.get(i).get(this._dimension); // The desired output
            double ox = getOutput(this._patterns.get(i));
            sum += (ox - yx) * (ox - yx);
        }
        
        return sum;
    }
    
    public double sigder(double x) {
        return sig(x) * (1.0 - sig(x));
    }
    
    public ArrayList <Double> getPatternDeriv(ArrayList <Double> pattern) {
        ArrayList <Double> patternDeriv = new ArrayList <>();
        this._weights.forEach(_item -> {
            patternDeriv.add(0.0);
        });
        
        for (int i = 0; i < this._nodes; i++) {
            double arg = 0.0;
            
            for (int j = 0; j < this._dimension; j++) {
                int pos = (this._dimension + 2) * (i+1) - (this._dimension + 1) + (j+1);
                arg += pattern.get(j+1) * this._weights.get(pos - 1);
            }
            arg += this._weights.get((this._dimension + 2) * (i+1) - 1);
            double s = this.sig(arg);
            double s1 = this.sigder(arg);
            patternDeriv.set((this._dimension + 2) * (i+1) - (this._dimension + 1) - 1, s);
            patternDeriv.set((this._dimension + 2) * (i+1) - 1, this._weights.get((this._dimension + 2) * (i+1) - (this._dimension + 1) - 1) * s1);
            for (int j = 0; j < this._dimension; j++) {
                int pos = (this._dimension + 2) * (i+1) - (this._dimension + 1) + j;
                patternDeriv.set(pos - 1, this._weights.get((this._dimension + 2) * (i+1) - (this._dimension + 1) - 1) * pattern.get((j+1) - 1) * s1);
            }
        }
        
        return patternDeriv;
    }
    
    public ArrayList <Double> getDeriv() {
        ArrayList <Double> deriv = new ArrayList<>();
        this._weights.forEach(_item -> {
            deriv.add(0.0);
        });
        
        for (int i = 0; i < this._patterns.size(); i++) {
            ArrayList <Double> patternDeriv = getPatternDeriv(this._patterns.get(i));
            double yx = this._patterns.get(i).get(this._dimension);
            double ox = this.getOutput(this._patterns.get(i));
            
            
            for (int j = 0; j < deriv.size(); j++) {
                deriv.set(j, 2.0 * (ox - yx) * patternDeriv.get(j));
            }
        }
        return deriv;
    }
    
    public double train() {
        this.initializeWeights();
        this.findUniqueClasses();
        
        double trainError = 0.0;
        for (int i = 0; i < this._maxEpoches; i++) {
            trainError = getTrainError();
            if (trainError < 1e-5)
               break;
            else {
                ArrayList <Double> deriv = getDeriv();
                
                for (int j = 0; j < this._weights.size(); j++) {
                    this._weights.set(j, this._weights.get(j) * 0.01 * deriv.get(j));
                }
            }
        }
        
        return trainError;
    }

    
    /**
     * @param testPatterns the _testPatterns to set
     */
    public void setTestPatterns(ArrayList <ArrayList <Double>> testPatterns) {
        this._testPatterns = testPatterns;
    }
    
    public double getTestError() {
        double sum = 0.0;
        
        for (int i = 0; i < this._testPatterns.size(); i++) {
            double yx = this._testPatterns.get(i).get(this._dimension); // The desired output
            double ox = getOutput(this._testPatterns.get(i));
            sum += (ox - yx) * (ox - yx);
        }
        
        return sum;
    }
}

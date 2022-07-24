package mlp;

public class ConfigureAlgorithm {
    private static int nodes = 1;
    private static int maxEpoches = 100;
    private static double learning_rate = 0.01;
    private static double initializeWeightsOption = 1.0;

    /**
     * @return the nodes
     */
    public static int getNodes() {
        return nodes;
    }

    /**
     * @param aNodes the nodes to set
     */
    public static void setNodes(int aNodes) {
        nodes = aNodes;
    }

    /**
     * @return the maxEpoches
     */
    public static int getMaxEpoches() {
        return maxEpoches;
    }

    /**
     * @param aMaxEpoches the maxEpoches to set
     */
    public static void setMaxEpoches(int aMaxEpoches) {
        maxEpoches = aMaxEpoches;
    }

    /**
     * @return the learning_rate
     */
    public static double getLearning_rate() {
        return learning_rate;
    }

    /**
     * @param aLearning_rate the learning_rate to set
     */
    public static void setLearning_rate(double aLearning_rate) {
        learning_rate = aLearning_rate;
    }

    /**
     * @return the initializeWeightsOption
     */
    public static double getInitializeWeightsOption() {
        return initializeWeightsOption;
    }

    /**
     * @param aInitializeWeightsOption the initializeWeightsOption to set
     */
    public static void setInitializeWeightsOption(double aInitializeWeightsOption) {
        initializeWeightsOption = aInitializeWeightsOption;
    }
    
    public static void reset() {
        nodes = 1;
        maxEpoches = 100;
        learning_rate = 0.01;
        setInitializeWeightsOption(1.0);
    }
}

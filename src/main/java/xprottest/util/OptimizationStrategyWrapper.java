package xprottest.util;

/**
 * The class OptimizationStrategyWrapper models a ProtTest optimization
 * strategy (about topology usage), providing a description and a option
 * value together.
 * 
 * @author diego
 */
public class OptimizationStrategyWrapper {

    private String description;
    private int value;
    
    public int getValue() {
        return value;
    }
    
    public OptimizationStrategyWrapper(String description, int value) {
        this.description = description;
        this.value = value;
    }

    @Override
    public String toString() {
        return description;
    }
    
}

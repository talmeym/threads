package data;

public class ObservableChangeEvent
{
    public static final int s_CHANGE = 0;
    public static final int s_ADDED = 1;
    public static final int s_REMOVED = 2;
    
    private final ObservableObserver o_component;
    
    private final int o_type;
    
    public ObservableChangeEvent(ObservableObserver p_component, int p_type)
    {
        o_component = p_component;
        o_type = p_type;
    }
    
    public ObservableObserver getObservableObserver()    
    {
        return o_component;
    }
    
    public int getType()
    {
        return o_type;
    }
}

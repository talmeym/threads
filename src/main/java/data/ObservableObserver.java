package data;

import java.util.Observable;
import java.util.Observer;

class ObservableObserver extends Observable implements Observer
{
    void startObserve(ObservableObserver p_observable)
    {
        p_observable.addObserver(this);
    }
    
    void stopObserve(ObservableObserver p_observable)
    {
        p_observable.deleteObserver(this);
    }
        
    public void update(Observable o, Object arg)
    {
        changed((ObservableChangeEvent)arg);
    }
    
    protected void changed()
    {
        changed(new ObservableChangeEvent(this, ObservableChangeEvent.s_CHANGE));
    }

    protected void changed(ObservableChangeEvent p_event)
    {
        setChanged();
        notifyObservers(p_event);
    }
}

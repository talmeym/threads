package data;

import java.util.*;

class ObservableObserver extends Observable implements Observer {
    void observe(ObservableObserver p_observable) {
        p_observable.addObserver(this);
    }
    
    void unobserve(ObservableObserver p_observable) {
        p_observable.deleteObserver(this);
    }
        
    public void update(Observable o, Object arg) {
        changed((ObservableChangeEvent)arg);
    }
    
    protected void changed() {
        changed(new ObservableChangeEvent(this, ObservableChangeEvent.s_CHANGE, -1));
    }

    protected void changed(ObservableChangeEvent p_event) {
        setChanged();
        notifyObservers(p_event);
    }
}

package data;

import java.io.File;
import java.util.*;

public abstract class ThreadItem <CONTENTS extends Component> extends CollectionComponent<CONTENTS> {
    ThreadItem(UUID id, Date p_creationDate, boolean p_active, String p_text, List<CONTENTS> p_components, Comparator<CONTENTS> p_comparator, File p_docFolder) {
        super(id, p_creationDate, p_active, p_text, p_components, p_comparator, p_docFolder);
    }
    
    public Thread getParentThread() {
        return (Thread) getParentComponent();
    }
}

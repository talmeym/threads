package data;

import java.io.File;
import java.util.*;

public abstract class Component extends ObservableObserver {
	private final UUID o_id;
	private final Date o_creationDate;
	private Date o_modifiedDate;
    private boolean o_active;
    private String o_text;
    private Component o_parentComponent;
	private File o_docFolder;


	Component(UUID p_id, Date p_creationDate, Date p_modifiedDate, boolean p_activeFlag, String p_text, File p_docFolder) {
		o_id = p_id;
		o_creationDate = p_creationDate;
		o_modifiedDate = p_modifiedDate;
        o_active = p_activeFlag;
        o_text = p_text;
		o_docFolder = p_docFolder;
    }

	public UUID getId() {
		return o_id;
	}

    public Date getCreationDate() {
        return o_creationDate;
    }
    
	public Date getModifiedDate() {
		return o_modifiedDate;
	}

	protected void modified() {
		o_modifiedDate = new Date();
	}

    public boolean isActive() {
        return o_active;
    }

    public String getText() {
        return o_text;
    }

    public Component getParentComponent() {
        return o_parentComponent;
    }

    public void setActive(boolean p_active) {
        o_active = p_active;
        changed();
		modified();
    }

    public void setText(String p_text) {
        o_text = p_text;
        changed();
		modified();
    }

    void setParentComponent(Component p_parentComponent) {
        o_parentComponent = p_parentComponent;
    }

	public File getDocFolder() {
		return o_docFolder;
	}

	public void setDocFolder(File p_docFolder) {
		o_docFolder = p_docFolder;
		changed();
		modified();
	}

    public String toString() {
        return o_text;
    }

	public abstract String getType();

	public abstract Component findComponent(UUID p_id);
}

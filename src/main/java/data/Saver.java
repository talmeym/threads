package data;

import org.jdom.*;
import org.jdom.output.*;

import java.io.*;
import java.util.Date;

public class Saver {
    public static void saveDocument(Thread p_topThread, File p_xmlFile) {
        try {
            Element x_rootElem = new Element(XmlConstants.s_THREADS);
            x_rootElem.addContent(addThread(p_topThread));
            Document x_doc = new Document(x_rootElem);
            addSchema(x_doc);
            XMLOutputter x_outputter = new XMLOutputter(Format.getPrettyFormat());
            x_outputter.output(x_doc, new FileOutputStream(p_xmlFile));
        } catch(Exception ioe) {
            System.err.println("Error saving threads file: " + ioe);
            System.exit(1);
        }
    }

	private static Element addThread(Thread p_thread) {
        Element x_threadElem = new Element(XmlConstants.s_THREAD);
        addComponentData(x_threadElem, p_thread);
    
        for(ThreadItem x_groupItem: p_thread.getThreadItems()) {
            if(x_groupItem instanceof Thread) {
                x_threadElem.addContent(addThread((Thread) x_groupItem));
            }
            if(x_groupItem instanceof Item) {
                x_threadElem.addContent(addItem((Item) x_groupItem));
            }
        }
        
        addDocFolder(x_threadElem, p_thread);
        return x_threadElem;
    }
    
    private static Element addItem(Item p_item) {
        Element x_itemElem = new Element(XmlConstants.s_ITEM);
        addComponentData(x_itemElem, p_item);

		if(p_item.getDueDate() != null) {
			addContent(x_itemElem, XmlConstants.s_DUE, addDateTime(p_item.getDueDate()));

			for(Reminder x_reminder: p_item.getReminders()) {
				x_itemElem.addContent(addReminder(x_reminder));
			}
		}

		addDocFolder(x_itemElem, p_item);
		return x_itemElem;
    }
    
    private static Element addReminder(Reminder p_reminder) {
        Element x_reminderElem = new Element(XmlConstants.s_REMINDER);
        addComponentData(x_reminderElem, p_reminder);        
        addContent(x_reminderElem, XmlConstants.s_REM_DATE, addDateTime(p_reminder.getDueDate()));
		addDocFolder(x_reminderElem, p_reminder);
		return x_reminderElem;
    }

    private static void addComponentData(Element p_element, Component p_component) {
		p_element.setAttribute(XmlConstants.s_ID, p_component.getId().toString());
        p_element.setAttribute(XmlConstants.s_CREATED, addDateTime(p_component.getCreationDate()));
        p_element.setAttribute(XmlConstants.s_MODIFIED, addDateTime(p_component.getModifiedDate()));
        p_element.setAttribute(XmlConstants.s_ACTIVE, addBoolean(p_component.isActive()));
        addContent(p_element, XmlConstants.s_TEXT, p_component.getText());
    }

    private static void addContent(Element p_element, String p_name, String p_value) {
        p_element.addContent(new Element(p_name).setText(p_value));
    }
    
    private static String addDateTime(Date p_date) {
        return XmlConstants.s_DATE_TIME_FORMAT.format(p_date);
    }

    private static String addBoolean(boolean p_boolean) {
        return String.valueOf(p_boolean);
    }
    
    private static void addSchema(Document p_document) {
        Namespace x_nameSpace = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        p_document.getRootElement().addNamespaceDeclaration(x_nameSpace);
        p_document.getRootElement().setAttribute("noNamespaceSchemaLocation", "threads.xsd", x_nameSpace);
    }
    
    private static void addDocFolder(Element p_element, Component p_component) {
        if(p_component.getDocFolder() != null) {
            p_element.addContent(new Element(XmlConstants.s_DOC_FOLDER).setText(p_component.getDocFolder().getAbsolutePath()));
        }
    }
}

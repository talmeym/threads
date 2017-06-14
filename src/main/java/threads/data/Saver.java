package threads.data;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import threads.data.ActionTemplate.ReminderTemplate;

import java.io.FileOutputStream;
import java.util.Date;

import static threads.data.XmlConstants.*;

public class Saver {
    public static void saveConfiguration(Configuration p_configuration, boolean p_backup) {
        try {
            Element x_rootElem = new Element(s_THREADS);
            x_rootElem.addContent(addThread(p_configuration.getTopLevelThread()));
            p_configuration.getActionTemplates().forEach(a -> x_rootElem.addContent(addActionTemplate(a)));
            Document x_doc = new Document(x_rootElem);
            addSchema(x_doc);
            XMLOutputter x_outputter = new XMLOutputter(Format.getPrettyFormat());
            x_outputter.output(x_doc, new FileOutputStream(p_backup ? p_configuration.getBackupFile() : p_configuration.getXmlFile()));
        } catch(Exception ioe) {
            System.err.println("Error saving threads file: " + ioe);
            System.exit(1);
        }

        p_configuration.getSettings().save();
    }

	private static Element addActionTemplate(ActionTemplate p_actionTemplate) {
		Element x_templateElem = new Element(s_ACTION_TEMPLATE);
		addContent(x_templateElem, s_NAME, p_actionTemplate.getName());
		addContent(x_templateElem, s_TOKEN_PROMPT, p_actionTemplate.getTokenPrompt());
		addContent(x_templateElem, s_TOKEN_DEFAULT, p_actionTemplate.getTokenDefault());
		addContent(x_templateElem, s_TEXT_TEMPLATE, p_actionTemplate.getTextTemplate());

		for(ReminderTemplate x_reminderTemplate: p_actionTemplate.getReminderTemplates()) {
			x_templateElem.addContent(addReminderTemplate(x_reminderTemplate));
		}

		return x_templateElem;
	}

	private static Element addReminderTemplate(ReminderTemplate p_reminderTemplate) {
		Element x_templateElem = new Element(s_REMINDER_TEMPLATE);
		addContent(x_templateElem, s_TEXT_TEMPLATE, p_reminderTemplate.getTextTemplate());
		addContent(x_templateElem, s_OFFSET, String.valueOf(p_reminderTemplate.getOffset()));
		return x_templateElem;
	}

	private static Element addThread(Thread p_thread) {
        Element x_threadElem = new Element(s_THREAD);
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
        Element x_itemElem = new Element(s_ITEM);
        addComponentData(x_itemElem, p_item);

		String x_notes = p_item.getNotes();

		if(x_notes != null && x_notes.length() > 0) {
			Element x_notesElem = new Element(s_NOTES);
			x_itemElem.addContent(x_notesElem);
			addContent(x_notesElem, s_PRE, x_notes);
		}

		if(p_item.getDueDate() != null) {
			addContent(x_itemElem, s_DUE_DATE, addDateTime(p_item.getDueDate()));

			for(Reminder x_reminder: p_item.getReminders()) {
				x_itemElem.addContent(addReminder(x_reminder));
			}
		}


		addDocFolder(x_itemElem, p_item);
		return x_itemElem;
    }
    
    private static Element addReminder(Reminder p_reminder) {
        Element x_reminderElem = new Element(s_REMINDER);
        addComponentData(x_reminderElem, p_reminder);

		String x_notes = p_reminder.getNotes();

		if(x_notes != null && x_notes.length() > 0) {
			Element x_notesElem = new Element(s_NOTES);
			x_reminderElem.addContent(x_notesElem);
			addContent(x_notesElem, s_PRE, x_notes);
		}

		addContent(x_reminderElem, s_DUE_DATE, addDateTime(p_reminder.getDueDate()));
		addDocFolder(x_reminderElem, p_reminder);
		return x_reminderElem;
    }

    private static void addComponentData(Element p_element, Component p_component) {
		p_element.setAttribute(s_ID, p_component.getId().toString());
        p_element.setAttribute(s_CREATED, addDateTime(p_component.getCreationDate()));
        p_element.setAttribute(s_MODIFIED, addDateTime(p_component.getModifiedDate()));
        p_element.setAttribute(s_ACTIVE, addBoolean(p_component.isActive()));
        addContent(p_element, s_TEXT, p_component.getText());
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

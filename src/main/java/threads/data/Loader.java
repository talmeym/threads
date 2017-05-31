package threads.data;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import threads.data.ActionTemplate.ReminderTemplate;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static threads.data.XmlConstants.*;

public class Loader {
    public static Configuration loadConfiguration(File p_xmlFile) {
        SAXBuilder x_builder = new SAXBuilder(true);
        x_builder.setFeature("http://apache.org/xml/features/validation/schema", true);
        x_builder.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
        x_builder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String something, String entityPath) throws SAXException, IOException {
			    entityPath = entityPath.indexOf('/') != -1 ? entityPath.substring(entityPath.lastIndexOf('/')) : entityPath;
				return new InputSource(getClass().getResourceAsStream(entityPath));
			}
		});

        try {
            Document x_doc = x_builder.build(p_xmlFile);
			return new Configuration(loadThread(x_doc.getRootElement().getChild(s_THREAD)), loadActionTemplates(x_doc.getRootElement()));
        }        
        catch(Exception ioe) {
            System.err.println("Error loading threads file: " + ioe);
            System.exit(1);
        }
        
        return null;
    }

	private static List<ActionTemplate> loadActionTemplates(Element p_element) {
		List x_actionTemplates = p_element.getChildren(s_ACTION_TEMPLATE);
		List<ActionTemplate> x_result = new ArrayList<>();

		for(Object x_actionTemplate: x_actionTemplates) {
			Element x_element = (Element) x_actionTemplate;

			x_result.add(loadActionTemplate(x_element));
		}

		return x_result;
	}

	private static ActionTemplate loadActionTemplate(Element p_element) {
		String x_name = p_element.getChildText(s_NAME);
		String x_tokenPrompt = p_element.getChildText(s_TOKEN_PROMPT);
		String x_tokenDefault = p_element.getChildText(s_TOKEN_DEFAULT);
		String x_textTemplate = p_element.getChildText(s_TEXT_TEMPLATE);

		List x_reminderTemplates = p_element.getChildren(s_REMINDER_TEMPLATE);
		List<ReminderTemplate> x_reminderTemplateList = new ArrayList<>();

		for(Object x_reminderTemplate: x_reminderTemplates) {
			Element x_element = (Element) x_reminderTemplate;
			x_reminderTemplateList.add(loadReminderTemplate(x_element));
		}

		return new ActionTemplate(x_name, x_tokenPrompt, x_tokenDefault, x_textTemplate, x_reminderTemplateList);
	}

	private static ReminderTemplate loadReminderTemplate(Element p_element) {
		String x_textTemplate = p_element.getChildText(s_TEXT_TEMPLATE);
		Long x_offset = Long.parseLong(p_element.getChildText(s_OFFSET));
		return new ReminderTemplate(x_textTemplate, x_offset);
	}

	private static Thread loadThread(Element p_element) {
		UUID id = loadId(p_element);
        Date x_creationDate = loadCreatedDate(p_element);
		Date x_modifiedDate = loadModifiedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);
        String x_text = loadText(p_element); 
        List x_threads = p_element.getChildren();
        List<ThreadItem> x_itemList = new ArrayList<>();

		for (Object x_thread : x_threads) {
			Element x_element = (Element) x_thread;

			if (x_element.getName().equals(s_THREAD)) {
				x_itemList.add(loadThread(x_element));
			}
			if (x_element.getName().equals(s_ITEM)) {
				x_itemList.add(loadItem(x_element));
			}
		}

		return new Thread(id, x_creationDate, x_modifiedDate, x_active, x_text, x_itemList, loadDocFolder(p_element));
    }

    private static Item loadItem(Element p_element) {
		UUID id = loadId(p_element);
        Date x_creationDate = loadCreatedDate(p_element);
		Date x_modifiedDate = loadModifiedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);
        String x_text = loadText(p_element);
		String x_dueDateStr = p_element.getChildText(s_DUE);
		Date x_dueDate = null;
		List<Reminder> x_reminderList = new ArrayList<>();

		if(x_dueDateStr != null) {
			x_dueDate = loadDateTime(x_dueDateStr);
			List x_reminders = p_element.getChildren(s_REMINDER);

			for (Object x_reminder : x_reminders) {
				Element x_reminderElem = (Element) x_reminder;
				x_reminderList.add(loadReminder(x_reminderElem));
			}
		}

		Element x_notesElem = p_element.getChild(s_NOTES);
		String x_notes = null;

		if(x_notesElem != null) {
			x_notes = x_notesElem.getChildText(s_PRE);
		}

		return new Item(id, x_creationDate, x_modifiedDate, x_active, x_text, x_dueDate, x_notes, x_reminderList, loadDocFolder(p_element));
    }

    private static Reminder loadReminder(Element p_element) {
		UUID id = loadId(p_element);
		Date x_creationDate = loadCreatedDate(p_element);
		Date x_modifiedDate = loadModifiedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);
        String x_text = loadText(p_element);
        Date x_date = loadDateTime(p_element.getChildText(s_REM_DATE));
		return new Reminder(id, x_creationDate, x_modifiedDate, x_active, x_text, x_date, loadDocFolder(p_element));
    }

	private static UUID loadId(Element p_element) {
		return UUID.fromString(p_element.getAttributeValue(s_ID));
	}

    private static Date loadCreatedDate(Element p_element) {
        return loadDateTime(p_element.getAttributeValue(s_CREATED));
    }
    
    private static Date loadModifiedDate(Element p_element) {
		return loadDateTime(p_element.getAttributeValue(s_MODIFIED));
    }

    private static boolean loadActiveFlag(Element p_element) {
        return loadBoolean(p_element.getAttributeValue(s_ACTIVE));
    }
    
    private static String loadText(Element p_element) {
        return p_element.getChildText(s_TEXT);
    }

    private static Date loadDateTime(String p_dateTimeText) {
        try {
            return s_DATE_TIME_FORMAT.parse(p_dateTimeText);
        }
        catch(ParseException pe) {
            throw new IllegalArgumentException("Invalid date time text:" + p_dateTimeText);
        }
    }
    
    private static boolean loadBoolean(String p_booleanText) {
        return "true".equalsIgnoreCase(p_booleanText);
    }

    private static File loadDocFolder(Element o_element) {
        String x_docFolderStr = o_element.getChildText(s_DOC_FOLDER);
        File x_docFolder = null;
        
        if(x_docFolderStr != null) {
            x_docFolder = new File(x_docFolderStr);
            
            if(!x_docFolder.exists()) {
                x_docFolder = null;
            }
        }
        
        return x_docFolder;
    }
    
    public static class Configuration {
    	private Thread o_topLevelThread;
		private List<ActionTemplate> o_actionTemplates;

		public Configuration(Thread p_topLevelThread, List<ActionTemplate> p_actionTemplates) {
			this.o_topLevelThread = p_topLevelThread;
			this.o_actionTemplates = p_actionTemplates;
		}

		public Thread getTopLevelThread() {
			return o_topLevelThread;
		}

		public List<ActionTemplate> getActionTemplates() {
			return o_actionTemplates;
		}
	}
}
package data;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.xml.sax.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class Loader {
    public static Thread loadDocumentThread(String p_xmlPath) {
        SAXBuilder x_builder = new SAXBuilder(false);
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
            Document x_doc = x_builder.build(p_xmlPath);            
            return loadThread(x_doc.getRootElement().getChild(XmlConstants.s_THREAD));
        }        
        catch(Exception ioe) {
            System.err.println("Error loading threads file: " + ioe);
            System.exit(1);
        }
        
        return null;
    }
    
    public static Properties loadDocumentSettings(String p_xmlPath) {
        SAXBuilder x_builder = new SAXBuilder(false);
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
            Document x_doc = x_builder.build(p_xmlPath);
            return loadSettings(x_doc.getRootElement().getChild(XmlConstants.s_SETTINGS));
        }
        catch(Exception ioe) {
            System.err.println("Error loading threads file: " + ioe);
            System.exit(1);
        }

        return null;
    }

	private static Properties loadSettings(Element p_element) {
		Properties x_settings = new Properties();
		List x_properties = p_element.getChildren();

		for(Object x_property: x_properties) {
			Element x_propertyElem = (Element) x_property;
			x_settings.setProperty(x_propertyElem.getAttributeValue(XmlConstants.s_PROPERTY_NAME), x_propertyElem.getAttributeValue(XmlConstants.s_PROPERTY_VALUE));
		}

		return x_settings;
	}

	private static Thread loadThread(Element p_element) {
		UUID id = loadId(p_element);
        Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element); 
        List x_threads = p_element.getChildren();
        List<ThreadItem> x_itemList = new ArrayList<ThreadItem>();

		for (Object x_thread : x_threads) {
			Element x_element = (Element) x_thread;

			if (x_element.getName().equals(XmlConstants.s_THREAD)) {
				x_itemList.add(loadThread(x_element));
			}
			if (x_element.getName().equals(XmlConstants.s_ITEM)) {
				x_itemList.add(loadItem(x_element));
			}
		}
        
        File x_docFolder = loadDocFolder(p_element);
        return new Thread(id, x_creationDate, x_active, x_text, x_itemList, x_docFolder);
    }

    private static Item loadItem(Element p_element) {
		UUID id = loadId(p_element);
        Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element);
		String x_dueDateStr = p_element.getChildText(XmlConstants.s_DUE);
		Date x_dueDate = null;
		List<Reminder> x_reminderList = new ArrayList<Reminder>();

		if(x_dueDateStr != null) {
			x_dueDate = loadDateTime(x_dueDateStr);
			List x_reminders = p_element.getChildren(XmlConstants.s_REMINDER);

			for (Object x_reminder : x_reminders) {
				Element x_reminderElem = (Element) x_reminder;
				x_reminderList.add(loadReminder(x_reminderElem));
			}
		}

		return new Item(id, x_creationDate, x_active, x_text, x_dueDate, x_reminderList);
    }

    private static Reminder loadReminder(Element p_element) {
		UUID id = loadId(p_element);
		Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element);
        Date x_date = loadDateTime(p_element.getChildText(XmlConstants.s_REM_DATE));
        return new Reminder(id, x_creationDate, x_active, x_text, x_date);
    }

	private static UUID loadId(Element p_element) {
		String id = p_element.getAttributeValue(XmlConstants.s_ID);

		// TODO remove this soon
		if(id == null) {
			return UUID.randomUUID();
		}

		return UUID.fromString(id);
	}

    private static Date loadCreatedDate(Element p_element) {
        return loadDateTime(p_element.getAttributeValue(XmlConstants.s_CREATED));
    }
    
    private static boolean loadActiveFlag(Element p_element) {
        return loadBoolean(p_element.getAttributeValue(XmlConstants.s_ACTIVE));
    }
    
    private static String loadText(Element p_element) {
        return p_element.getChildText(XmlConstants.s_TEXT);
    }

    private static Date loadDateTime(String p_dateTimeText) {
        try {
            return XmlConstants.s_DATE_TIME_FORMAT.parse(p_dateTimeText);
        }
        catch(ParseException pe) {
            throw new IllegalArgumentException("Invalid date time text:" + p_dateTimeText);
        }
    }
    
    private static boolean loadBoolean(String p_booleanText) {
        return "true".equalsIgnoreCase(p_booleanText);
    }

    private static File loadDocFolder(Element o_element) {
        String x_docFolderStr = o_element.getChildText(XmlConstants.s_DOC_FOLDER);
        File x_docFolder = null;
        
        if(x_docFolderStr != null) {
            x_docFolder = new File(x_docFolderStr);
            
            if(!x_docFolder.exists()) {
                x_docFolder = null;
            }
        }
        
        return x_docFolder;
    }
}
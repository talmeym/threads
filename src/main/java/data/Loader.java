package data;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.xml.sax.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class Loader
{
    public static ThreadGroup loadDocument(String p_xmlPath)
    {
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

        try
        {        
            Document x_doc = x_builder.build(p_xmlPath);            
            return loadThreadGroup(x_doc.getRootElement().getChild(XmlConstants.s_THREAD_GROUP));
        }        
        catch(Exception ioe)
        {
            System.err.println("Error loading threads file: " + ioe);
            System.exit(1);
        }
        
        return null;
    }
    
    private static ThreadGroup loadThreadGroup(Element p_element)
    {
        Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element); 
        
        List x_threads = p_element.getChildren();
        List x_itemList = new ArrayList();
        
        Iterator x_iterator = x_threads.iterator();
        
        while(x_iterator.hasNext())
        {
            Element x_element = (Element) x_iterator.next();
            
            if(x_element.getName().equals(XmlConstants.s_THREAD))
            {
                x_itemList.add(loadThread(x_element));
            }
            else if(x_element.getName().equals(XmlConstants.s_THREAD_GROUP))
            {
                x_itemList.add(loadThreadGroup(x_element));
            }
        }
        
        File x_docFolder = loadDocFolder(p_element);
        
        return new ThreadGroup(x_creationDate,
                               x_active,
                               x_text,
                               (ThreadGroupItem[])x_itemList.toArray(new ThreadGroupItem[0]), 
                               x_docFolder);
    }

    private static Thread loadThread(Element p_element)
    {
        Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element);               
        
        List x_items = p_element.getChildren(XmlConstants.s_ITEM);
        List x_itemList = new ArrayList();
        Iterator x_itemIterator = x_items.iterator();
        
        while(x_itemIterator.hasNext())
        {
            Element x_itemElem = (Element) x_itemIterator.next();
            x_itemList.add(loadItem(x_itemElem));
        }
        
        File x_docFolder = loadDocFolder(p_element);
        
        return new Thread(x_creationDate,
                          x_active,
                          x_text,
                          (Item[]) x_itemList.toArray(new Item[0]), x_docFolder);
    }
    
    private static Item loadItem(Element p_element)
    {
        Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element);        
        
		Date x_dueDate = loadDateTime(p_element.getChildText(XmlConstants.s_DUE));

		List x_reminders = p_element.getChildren(XmlConstants.s_REMINDER);
		List x_reminderList = new ArrayList();
		Iterator x_reminderIterator = x_reminders.iterator();

		while(x_reminderIterator.hasNext())
		{
			Element x_reminderElem = (Element) x_reminderIterator.next();
			x_reminderList.add(loadReminder(x_reminderElem));
		}

		return new Item(x_creationDate,
                        x_active,
                        x_text, 
                        x_dueDate,
						(Reminder[])x_reminderList.toArray(new Reminder[0])
				);
    }

    private static Reminder loadReminder(Element p_element)
    {
        Date x_creationDate = loadCreatedDate(p_element);
        boolean x_active = loadActiveFlag(p_element);        
        String x_text = loadText(p_element);
        
        Date x_date = loadDateTime(p_element.getChildText(XmlConstants.s_REM_DATE));

        return new Reminder(x_creationDate,
                            x_active,
                            x_text, 
                            x_date);
    }
    
    private static Date loadCreatedDate(Element p_element)
    {
        return loadDateTime(p_element.getAttributeValue(XmlConstants.s_CREATED));
    }
    
    private static boolean loadActiveFlag(Element p_element)
    {
        return loadBoolean(p_element.getAttributeValue(XmlConstants.s_ACTIVE));
    }
    
    private static String loadText(Element p_element)
    {
        return p_element.getChildText(XmlConstants.s_TEXT);
    }

    private static Date loadDateTime(String p_dateTimeText)
    {
        try
        {
            return XmlConstants.s_DATE_TIME_FORMAT.parse(p_dateTimeText);
        }
        catch(ParseException pe)
        {
            throw new IllegalArgumentException("Invalid date time text:" + p_dateTimeText);
        }
    }
    
    private static boolean loadBoolean(String p_booleanText)
    {
        return "true".equalsIgnoreCase(p_booleanText);
    }
    
    
    private static File loadDocFolder(Element o_element)
    {
        String x_docFolderStr = o_element.getChildText(XmlConstants.s_DOC_FOLDER);
        
        File x_docFolder = null;
        
        if(x_docFolderStr != null)
        {
            x_docFolder = new File(x_docFolderStr);
            
            if(!x_docFolder.exists())
            {
                x_docFolder = null;
            }
        }
        
        return x_docFolder;
    }
}
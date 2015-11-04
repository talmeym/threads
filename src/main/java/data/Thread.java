package data;

import util.CreationDateComparator;

import java.io.File;
import java.util.Date;

public class Thread extends ThreadGroupItem
{    
    public Thread()
    {
        this(new Date(), true, "new thread", null, null);
    }
    
    public Thread(Date p_creationDate, boolean p_active, String p_text, Item[] p_items, File p_docFolder)
    {
        super(p_creationDate, p_active, p_text, p_items, new CreationDateComparator(), p_docFolder);
    }

    public int getItemCount()
    {
        return getComponentCount();
    }
    
    public Item getItem(int p_index)
    {
        return(Item) getComponent(p_index);
    }
    
    Item[] getItems()
    {
        return (Item[]) getComponents().toArray(new Item[0]);
    }
    
    public void addItem(Item p_item)
    {
        addComponent(p_item);
    }
    
    public void removeItem(Item p_item)
    {
        removeComponent(p_item);
    }
}
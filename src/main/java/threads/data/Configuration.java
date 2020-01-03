package threads.data;

import threads.util.Settings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static threads.util.Settings.Setting.UUID;

public class Configuration {
    private final File o_xmlFile;
    private final Settings o_settings;
    private final Thread o_topLevelThread;
    private final List<ActionTemplate> o_actionTemplates;
    private final List<AutoSortRule> o_autoSortRules;

    public Configuration(File p_xmlFile, Thread p_topLevelThread, List<ActionTemplate> p_actionTemplates, List<AutoSortRule> p_autoSortRules) {
        o_xmlFile = p_xmlFile;
        o_autoSortRules = p_autoSortRules;
        o_settings = new Settings(new File(o_xmlFile.getParentFile(), o_xmlFile.getName() + ".properties"));
        o_topLevelThread = p_topLevelThread;
        o_actionTemplates = p_actionTemplates;
    }

    public File getXmlFile() {
        return o_xmlFile;
    }

    public Thread getTopLevelThread() {
        return o_topLevelThread;
    }

    public List<ActionTemplate> getActionTemplates() {
        return o_actionTemplates;
    }

    public List<AutoSortRule> getAutoSortRules() {
        return o_autoSortRules;
    }

    File getBackupFile() {
        String x_fileName = o_xmlFile.getName();
        File x_originalFolder = o_xmlFile.getParentFile();
        File x_backupFolder = new File(x_originalFolder, "backups");
        x_backupFolder.mkdirs();
        return new File(x_backupFolder, x_fileName.substring(0, x_fileName.indexOf(".xml")) + ".backup." + new SimpleDateFormat("yyMMddHH").format(new Date()) + ".xml");
    }

    public Settings getSettings() {
        return o_settings;
    }

    public Component getFirstComponent() {
        Thread x_topLevelThread = getTopLevelThread();

        String x_firstUuid = o_settings.getStringSetting(UUID);
        Component x_firstComponent = x_topLevelThread;

        if(x_firstUuid != null) {
            List<Component> x_searchResults = x_topLevelThread.search(new Search.Builder().withId(java.util.UUID.fromString(x_firstUuid)).build());
            x_firstComponent = x_searchResults.size() > 0 ? x_searchResults.get(0) : x_firstComponent;
        }

        return x_firstComponent;
    }
}
package threads.util;

import threads.Threads;
import threads.data.Component;
import threads.data.Configuration;
import threads.data.Item;
import threads.data.Thread;
import threads.gui.WindowManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.awt.SystemTray.getSystemTray;
import static java.awt.TrayIcon.MessageType.ERROR;
import static java.awt.TrayIcon.MessageType.INFO;
import static java.lang.System.getProperty;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;
import static threads.data.ComponentChangeEvent.Field.TEXT;
import static threads.data.Loader.loadConfiguration;
import static threads.gui.Actions.addAction;
import static threads.gui.Actions.addUpdate;
import static threads.gui.ItemDateSuggestionPanel.getDateSuggestion;
import static threads.util.FileUtil.getRecentFiles;
import static threads.util.FileUtil.storeRecentFile;
import static threads.util.GoogleUtil.addNewGoogleAccount;
import static threads.util.ImageUtil.getThreadsIcon;
import static threads.util.ImageUtil.getThreadsImage;

public class SystemTrayUtil {
    private static final PopupMenu s_popUpMenu = new PopupMenu();
    private static TrayIcon x_trayIcon = new TrayIcon(getThreadsImage(), "Threads", s_popUpMenu);
    private static final Map<Configuration, Menu> s_configurationMenus = new HashMap<>();
    private static final Menu x_recentFilesMenu = new Menu("Open Recent");
    private static final Map<File, MenuItem> s_recentMenuItems = new HashMap<>();

    public static void initialise() {
		try {
			x_trayIcon.setImageAutoSize(true);
            getSystemTray().add(x_trayIcon);

            MenuItem x_openFileItem = new MenuItem("Open File");
            s_popUpMenu.add(x_openFileItem);

            x_openFileItem.addActionListener(e -> {
                JFileChooser x_fileChooser = new JFileChooser(new File(getProperty("user.dir")));
                x_fileChooser.setAcceptAllFileFilterUsed(false);
                x_fileChooser.setFileHidingEnabled(true);
                x_fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File x_file) {
                        for(Configuration x_configuration: s_configurationMenus.keySet()) {
                            if(x_configuration.getXmlFile().equals(x_file)) {
                                return false;
                            }
                        }

                        return x_file.getName().endsWith(".xml");
                    }

                    @Override
                    public String getDescription() {
                        return "XML Files";
                    }
                });

                if(x_fileChooser.showDialog(null, "Load") == JFileChooser.APPROVE_OPTION) {
                    File x_xmlFile = x_fileChooser.getSelectedFile();
                    storeRecentFile(x_xmlFile);
                    loadXmlFile(x_xmlFile);
                }
            });

            s_popUpMenu.add(x_recentFilesMenu);

            for(File x_xmlFile: getRecentFiles()) {
                MenuItem x_menuItem = new MenuItem(x_xmlFile.getName());
                x_menuItem.addActionListener(e -> loadXmlFile(x_xmlFile));
                x_recentFilesMenu.add(x_menuItem);
                s_recentMenuItems.put(x_xmlFile, x_menuItem);
            }

            MenuItem x_addGoogleItem = new MenuItem("Add Google Account");
			s_popUpMenu.add(x_addGoogleItem);

			x_addGoogleItem.addActionListener(e -> {
				String x_name = (String) showInputDialog(null, "Name the new account:", "Add Google Account ?", INFORMATION_MESSAGE, getThreadsIcon(), null, "Home");

				if(x_name != null) {
					boolean x_added = addNewGoogleAccount(x_name);
					String text = (x_added ? "successfully" : "unsuccessfully") + " added.";
					x_trayIcon.displayMessage("Google Account '" + x_name + "'", text, INFO);
				}
			});

            s_popUpMenu.addSeparator();
            MenuItem x_quitItem = new MenuItem("Quit Threads");

			x_quitItem.addActionListener(e -> {
			    WindowManager.getInstance().closeAllWindows();
                TimedSaver.getInstance().stopRunning();
                GoogleSyncer.getInstance().stopRunning();
                TimedUpdater.getInstance().stopRunning();
                System.exit(0);
            });

			s_popUpMenu.add(x_quitItem);
			s_popUpMenu.addSeparator();

			Map<Component, MenuItem> x_menuItems = new HashMap<>();

			NotificationUpdater.getInstance().addNotificationListener(p_dueComponents -> {
				if (p_dueComponents.size() == 1) {
					Component x_component = p_dueComponents.get(0);
					String caption = x_component instanceof Item ? "Action Overdue" : "Reminder";
					x_trayIcon.displayMessage(caption, x_component.getText(), INFO);
				} else if (p_dueComponents.size() > 1) {
					x_trayIcon.displayMessage("Threads", "You have " + p_dueComponents.size() + " new notifications.", INFO);
				}

				for (final Component x_component : p_dueComponents) {
					if(!x_menuItems.containsKey(x_component)) {
						MenuItem x_menuItem = new MenuItem((getMenuItemText(x_component)));
						x_menuItems.put(x_component, x_menuItem);

						x_menuItem.addActionListener(e -> WindowManager.getInstance().openComponent(x_component));

						x_component.addComponentChangeListener(e -> {
							if(e.getSource() == x_component) {
								if(e.isComponentRemoved()) {
									s_popUpMenu.remove(x_menuItems.get(x_component));
								} else if(e.getField() == TEXT) {
									x_menuItems.get(x_component).setLabel(getMenuItemText(x_component));
								}
							}
						});

						if(s_popUpMenu.getItemCount() == 3) {
							s_popUpMenu.addSeparator();
						}

						s_popUpMenu.add(x_menuItem);
					}
				}
			});
		} catch (AWTException e) {
			// do nothing for now TODO ??
		}
	}

	public static void addConfiguration(Configuration p_configuration) {
        Thread x_topLevelThread = p_configuration.getTopLevelThread();

        MenuItem x_addActionItem = new MenuItem("Add Action");
        x_addActionItem.addActionListener(e -> addAction(null, x_topLevelThread, getDateSuggestion(), null, true));

        MenuItem x_addUpdateItem = new MenuItem("Add Update");
        x_addUpdateItem.addActionListener(e -> addUpdate(null, x_topLevelThread, null));

        MenuItem x_closeItem = new MenuItem("Close File");
        x_closeItem.addActionListener(e -> WindowManager.getInstance().closeConfiguration(p_configuration));

        File x_xmlFile = p_configuration.getXmlFile().getAbsoluteFile();
        storeRecentFile(x_xmlFile);

        Menu x_menu = new Menu(x_xmlFile.getName());
        x_menu.add(x_addActionItem);
        x_menu.add(x_addUpdateItem);
        x_menu.add(x_closeItem);

        s_configurationMenus.put(p_configuration, x_menu);
        s_popUpMenu.add(x_menu);

        if(s_recentMenuItems.containsKey(x_xmlFile)) {
            s_recentMenuItems.get(x_xmlFile).setEnabled(false);
        } else {
            MenuItem x_newMenuItem = new MenuItem(x_xmlFile.getName());
            x_newMenuItem.addActionListener(e -> loadXmlFile(x_xmlFile));
            x_newMenuItem.setEnabled(false);
            x_recentFilesMenu.add(x_newMenuItem);
            s_recentMenuItems.put(x_xmlFile, x_newMenuItem);
        }
    }

    private static void loadXmlFile(File x_xmlFile) {
        try {
            new Threads(loadConfiguration(x_xmlFile));
        } catch (Exception e) {
            x_trayIcon.displayMessage("Threads", "Error loading file: " + e.getMessage(), ERROR);
        }
    }

    public static void removeConfiguration(Configuration p_configuration) {
	    s_popUpMenu.remove(s_configurationMenus.get(p_configuration));
		s_configurationMenus.remove(p_configuration);
        File x_xmlFile = p_configuration.getXmlFile().getAbsoluteFile();

        if(s_recentMenuItems.containsKey(x_xmlFile)) {
		    s_recentMenuItems.get(x_xmlFile).setEnabled(true);
        }
	}

	private static String getMenuItemText(Component x_component) {
		return (x_component instanceof Item ? "Action Overdue" : "Reminder") + ": " + x_component.getText();
	}
}
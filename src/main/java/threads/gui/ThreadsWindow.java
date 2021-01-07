package threads.gui;

import threads.data.Component;
import threads.data.Configuration;
import threads.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static threads.util.ImageUtil.getThreadsImage;
import static threads.util.Settings.Setting.*;

class ThreadsWindow extends JFrame {
    private final Settings o_settings;
    private final NavigationAndComponentPanel o_navigationAndComponentPanel;

    ThreadsWindow(Configuration p_configuration) {
        super("Threads - " + p_configuration.getXmlFile().getName());
        setIconImage(getThreadsImage());

        o_settings = p_configuration.getSettings();

        o_navigationAndComponentPanel = new NavigationAndComponentPanel(p_configuration, this);
        setContentPane(o_navigationAndComponentPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                o_settings.updateSetting(WINX, "" + new Double(getLocation().getX()).intValue());
                o_settings.updateSetting(WINY, "" + new Double(getLocation().getY()).intValue());
                o_settings.updateSetting(WINW, "" + new Double(getSize().getWidth()).intValue());
                o_settings.updateSetting(WINH, "" + new Double(getSize().getHeight()).intValue());
            }
        });

        o_navigationAndComponentPanel.showComponent(p_configuration.getFirstComponent());

        setSize(new Dimension(o_settings.getIntSetting(WINW), o_settings.getIntSetting(WINH)));
        setLocation(new Point(o_settings.getIntSetting(WINX), o_settings.getIntSetting(WINY)));
    }

    public void openComponent(Component p_component) {
        o_navigationAndComponentPanel.showComponent(p_component);
        o_settings.updateSetting(UUID, p_component.getId().toString());
    }
}

package gui;

import data.Reminder;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Date;

public class RemindDateSuggestionPanel extends JPanel implements DocumentListener
{
    private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final Dimension s_dueFieldSize = new Dimension(130, 25);

    private static final DateItem[] s_weekItems;

    private static final DateItem[] s_dayItems;

    private static final DateItem[] s_hourItems;

    private static final DateItem[] s_minItems;


    static
    {
        s_weekItems = new DateItem[5];

        for(int i = 0; i < s_weekItems.length; i++)
        {
            s_weekItems[i] = new DateItem(i + " Ws", i * 7 * 24 * 60 * 60 * 1000);
        }

        s_dayItems = new DateItem[7];

        for(int i = 0; i < s_dayItems.length; i++)
        {
            s_dayItems[i] = new DateItem(i + " Ds", i * 24 * 60 * 60 * 1000);
        }

        s_hourItems = new DateItem[24];

        for(int i = 0; i < s_hourItems.length; i++)
        {
            s_hourItems[i] = new DateItem(i + " Hs", i * 60 * 60 * 1000);
        }

        s_minItems = new DateItem[12];

        for(int i = 0; i < s_minItems.length; i++)
        {
            s_minItems[i] = new DateItem(i * 5 + " Ms", i * 5 * 60 * 1000);
        }
    }

	private final Reminder o_reminder;

	private final ChangeListener o_listener;

    private final JTextField o_dueDateField = new JTextField();

    private final JComboBox o_minBox = new JComboBox(s_minItems);

    private final JComboBox o_hourBox = new JComboBox(s_hourItems);

    private final JComboBox o_dayBox = new JComboBox(s_dayItems);

    private final JComboBox o_weekBox = new JComboBox(s_weekItems);

	private JButton o_setButton = new JButton("Set");
    
    public RemindDateSuggestionPanel(Reminder p_reminder, ChangeListener p_listener)
    {
        super(new BorderLayout());
		o_reminder = p_reminder;
		o_listener = p_listener;

		o_dueDateField.setPreferredSize(s_dueFieldSize);
		o_dueDateField.setText(s_dateFormat.format(o_reminder.getDueDate()));
		o_dueDateField.getDocument().addDocumentListener(this);

		o_dueDateField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setDueDate();
			}
		});

		o_setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggestAndSet();
			}
		});

		JPanel x_labelPanel = new JPanel(new BorderLayout());
		x_labelPanel.add(new JLabel("Due Date"), BorderLayout.CENTER);
		x_labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_labelAndFieldPanel = new JPanel(new BorderLayout());
		x_labelAndFieldPanel.add(x_labelPanel, BorderLayout.WEST);
		x_labelAndFieldPanel.add(o_dueDateField, BorderLayout.CENTER);

		JPanel x_dropdownPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_dropdownPanel.add(o_weekBox);
		x_dropdownPanel.add(o_dayBox);
		x_dropdownPanel.add(o_hourBox);
		x_dropdownPanel.add(o_minBox);

		JPanel x_beforePanel = new JPanel(new BorderLayout());
		x_beforePanel.add(new JLabel("before"), BorderLayout.CENTER);
		x_beforePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_suggestPanel = new JPanel(new BorderLayout());
		x_suggestPanel.add(x_beforePanel, BorderLayout.WEST);
		x_suggestPanel.add(o_setButton, BorderLayout.CENTER);

		add(x_labelAndFieldPanel, BorderLayout.WEST);
		add(x_dropdownPanel, BorderLayout.CENTER);
        add(x_suggestPanel, BorderLayout.EAST);
    }

	private void suggestAndSet()
    {
        long x_timeToSubtract = 0;
        
        x_timeToSubtract += ((DateItem)o_weekBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_dayBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_hourBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_minBox.getSelectedItem()).o_value;
        
        Date x_dueDate = o_reminder.getItem().getDueDate();
        
        o_dueDateField.setText(s_dateFormat.format(new Date(x_dueDate.getTime() - x_timeToSubtract)));
		setDueDate();
    }

	private void setDueDate() {
		if(o_dueDateField.getText() != null && o_dueDateField.getText().length() > 0)
		{
			try {
				Date x_dueDate = s_dateFormat.parse(o_dueDateField.getText());

				if(o_reminder.getDueDate() != null)
				{
					if(!x_dueDate.equals(o_reminder.getDueDate()))
					{
						o_reminder.setDueDate(x_dueDate);
					}
				}
				else
				{
					o_reminder.setDueDate(x_dueDate);
				}
			} catch (ParseException e) {
				o_dueDateField.setText(s_dateFormat.format(o_reminder.getDueDate()));
			}
		}
		else
		{
			o_dueDateField.setText(s_dateFormat.format(o_reminder.getDueDate()));
		}

		o_listener.changed(true);
	}

	@Override
	public void insertUpdate(DocumentEvent documentEvent) {
		o_listener.changed(false);
	}

	@Override
	public void removeUpdate(DocumentEvent documentEvent) {
		o_listener.changed(false);
	}

	@Override
	public void changedUpdate(DocumentEvent documentEvent) {
		o_listener.changed(false);
	}

	private static class DateItem
    {
        public final String o_display;
        
        public final int o_value;
        
        public DateItem(String p_display, int p_value)
        {
            o_display = p_display;
            o_value = p_value;
        }
        
        public String toString()
        {
            return o_display;
        }
    }
}


import model.LevenshteinTableModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.Iterator;

/**
 * Chunk editor
 * User: alberto
 * Date: 7/12/12
 * Time: 16:56
 *
 */
public class LevenshteinGraphFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(1, 2));
        final JTextArea textAreaOrigin = new JTextArea();
        final JTextArea textAreaDestiny = new JTextArea();
        textAreaOrigin.setPreferredSize(new Dimension(1, 200));
        textAreaDestiny.setPreferredSize(new Dimension(1, 200));
        panel.add(new JScrollPane(textAreaOrigin));
        panel.add(new JScrollPane(textAreaDestiny));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(panel);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(0.1);
        final LevenshteinTableModel model = new LevenshteinTableModel(textAreaOrigin.getText(), textAreaDestiny.getText());
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange(e);
            }

            private void onChange(DocumentEvent e) {
                model.setOrigin(textAreaOrigin.getText());
                model.setDestiny(textAreaDestiny.getText());
            }
        };
        textAreaOrigin.getDocument().addDocumentListener(listener);
        textAreaDestiny.getDocument().addDocumentListener(listener);

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(15, 5, Integer.MAX_VALUE, 1));

        final JTable table = new JTable(model) {
            /**
             *
             * @return False If the size of the table is not the size of the scroll
             */
            public boolean getScrollableTracksViewportWidth() {
                boolean ok = false;
                if (autoResizeMode != AUTO_RESIZE_OFF) {
                    if (getParent() instanceof JViewport) {
                        int parentWidth = getParent().getWidth();
                        int tableWidth = getPreferredSize().width;
                        ok = parentWidth > tableWidth;
                    }
                }
                return ok;
            }

            @Override
            public void addColumn(TableColumn aColumn) {
                aColumn.setPreferredWidth((Integer) spinner.getValue());
                super.addColumn(aColumn);
            }

      public boolean isCellEditable(int row, int column)
      {
        return false;
      }
        };

        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TableColumnModel cm = table.getColumnModel();
                Integer preferredWidth = (Integer) spinner.getValue();
                for (int i = 0; i < cm.getColumnCount(); i++) {
                    cm.getColumn(i).setPreferredWidth(preferredWidth);
                }
            }
        });

        table.setTableHeader(null);
        table.setDefaultRenderer(Object.class, model.getCellRenderer());
        JPanel panelGraph = new JPanel(new BorderLayout());
        panelGraph.add(new JScrollPane(table));
        JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        bPanel.add(new JLabel("Min Column Width"));
        bPanel.add(spinner);
        panelGraph.add(bPanel, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelGraph);
        table.setFillsViewportHeight(true);
        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setVisible(true);
    }
}

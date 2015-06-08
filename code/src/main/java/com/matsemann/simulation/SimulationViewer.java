package com.matsemann.simulation;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.matsemann.ea.ipc.Task;
import com.matsemann.ea.reference.ReferenceManager;
import com.matsemann.ea.reference.SolutionReducer;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Should only be used for showing Wheel solutions
 */
public class SimulationViewer extends JFrame {

    NondominatedPopulation population, reducedPopulation, referenceSet, reducedReferenceSet;

    JPanel mainPanel, buttonsPanel;
    JTextField toReduceField;
    JTable objectivesTable;
    JScrollPane tableScroll;
    LwjglCanvas lwjglCanvas;

    int toReduce = 20;
    SolutionReducer reducer = new SolutionReducer();
    ObjectivesModel objectivesModel;
    private final WheelRenderer wheelRenderer;

    public SimulationViewer(NondominatedPopulation population, NondominatedPopulation referenceSet) {
        this.population = population;
        this.referenceSet = referenceSet;



        JButton populationButton = new JButton("Population");
        populationButton.addActionListener(e -> updateTable("pop"));

        JButton reducedPopulationButton = new JButton("Reduced pop");
        reducedPopulationButton.addActionListener(e -> updateTable("reducedpop"));

        JButton referenceButton = new JButton("Reference set");
        referenceButton.addActionListener(e -> updateTable("ref"));

        JButton reducedReferenceButton = new JButton("Reduced ref");
        reducedReferenceButton.addActionListener(e -> updateTable("reducedref"));

        toReduceField = new JTextField(toReduce + "");
        toReduceField.setPreferredSize(new Dimension(50, toReduceField.getPreferredSize().height));

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEtchedBorder(Color.ORANGE, Color.CYAN));
        GridBagConstraints c = new GridBagConstraints();


        buttonsPanel = new JPanel();
        buttonsPanel.add(populationButton);
        buttonsPanel.add(reducedPopulationButton);
        buttonsPanel.add(referenceButton);
        buttonsPanel.add(reducedReferenceButton);
        buttonsPanel.add(toReduceField);


        String[] columnNames = {"Nr", "Dst", "rotZ", "rot", "spokes"};

        objectivesModel = new ObjectivesModel();
        objectivesModel.setColumnNames(columnNames);
        objectivesTable = new JTable(objectivesModel);
        objectivesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objectivesTable.setAutoCreateRowSorter(true);
        objectivesTable.setDefaultRenderer(Double.class, new DoubleCellRenderer());

        objectivesTable.getSelectionModel().addListSelectionListener(x -> {
            int selectedRow = objectivesTable.getSelectedRow();

            if (selectedRow >= 0)  {
                int solutionSelected = objectivesTable.convertRowIndexToModel(selectedRow);
                showSolution(solutionSelected);
            }
        });

        tableScroll = new JScrollPane(objectivesTable);

        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.width = 1200;
        configuration.height = 1000;
        configuration.samples = 4;
//        configuration.forceExit = false;

        wheelRenderer = new WheelRenderer();
        lwjglCanvas = new LwjglCanvas(wheelRenderer, configuration);
        lwjglCanvas.getCanvas().setPreferredSize(new Dimension(configuration.width, configuration.height));



        mainPanel.add(buttonsPanel, c);

        c.gridy = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        mainPanel.add(tableScroll, c);


        c.gridy = 0;
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        mainPanel.add(lwjglCanvas.getCanvas(), c);


        if (this.population != null) {
            updateTable("pop");
        } else {
            updateTable("ref");
        }


        getContentPane().add(mainPanel);
        setVisible(true);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void dispose() {
//        wheelRenderer.dispose();
//        lwjglCanvas.stop();
//        lwjglCanvas.postRunnable(() -> {
//            wheelRenderer.dispose();
//            lwjglCanvas.stop();
//        });
//        Gdx.app.postRunnable(lwjglCanvas::stop);
//        SwingUtilities.invokeLater(lwjglCanvas::stop);

//        lwjglCanvas.postRunnable(() -> {
//            LwjglCanvas.lwjglCanvas.listener.pause();
//            LwjglCanvas.lwjglCanvas.listener.dispose();
//        });
        lwjglCanvas.stop();
        super.dispose();
    }

    private void showSolution(int solutionSelected) {
        Solution solution = objectivesModel.getByIndex(solutionSelected);
        Task task = (Task) solution.getAttribute("task");
        if (task != null) {
            wheelRenderer.setAngles(task.angles);
        }
    }

    private void updateTable(String toShow) {
        int reduced = Integer.parseInt(toReduceField.getText());

        if (toShow.equals("pop")) {
            addDataToTable(population);
        } else if (toShow.equals("reducedpop")) {
            if (reducedPopulation == null || toReduce != reduced) {
                reducedPopulation = reducer.reduce(population, reduced);
            }
            addDataToTable(reducedPopulation);
        } else if (toShow.equals("ref")) {
            addDataToTable(referenceSet);
        } else if (toShow.equals("reducedref")) {
            if (reducedReferenceSet == null || toReduce != reduced) {
                reducedReferenceSet = reducer.reduce(referenceSet, reduced);
            }
            addDataToTable(reducedReferenceSet);
        }

        toReduce = reduced;
    }

    private void addDataToTable(NondominatedPopulation population) {
        objectivesModel.setSolutions(population);
    }


    public class ObjectivesModel extends AbstractTableModel {

        private NondominatedPopulation population;
        private String[] columnNames;

        public void setColumnNames(String[] columnNames) {
            this.columnNames = columnNames;
            fireTableStructureChanged();
        }

        public void setSolutions(NondominatedPopulation population) {
            this.population = population;
            fireTableDataChanged();
        }

        public Solution getByIndex(int i) {
            return population.get(i);
        }

        @Override
        public int getRowCount() {
            return population != null ? population.size() : 0;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return (double) rowIndex;
            }

            int obj = columnIndex - 1;

            if (obj >= population.get(rowIndex).getNumberOfObjectives()) {
                return -1.0;
            }

            return population.get(rowIndex).getObjective(obj);
        }


        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Double.class;
        }


    }

    public class DoubleCellRenderer extends DefaultTableCellRenderer {

        NumberFormat format;

        public DoubleCellRenderer() {
            super();
            format = NumberFormat.getInstance();
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(15);
        }

        @Override
        protected void setValue(Object value) {
            double v = (double) value;
            String format1 = format.format(v);
            setText(format1);
        }
    }

    public static void main(String[] args) {
        NondominatedPopulation dtlz2_2 = new ReferenceManager("test").getReferenceSet();

        SwingUtilities.invokeLater(() -> new SimulationViewer(dtlz2_2, null));
    }
}

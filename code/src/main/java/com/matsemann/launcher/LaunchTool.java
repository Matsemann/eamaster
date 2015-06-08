package com.matsemann.launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.apache.commons.cli.CommandLine;
import org.moeaframework.analysis.diagnostics.DiagnosticTool;
import org.moeaframework.core.Settings;
import org.moeaframework.util.CommandLineUtility;

import javax.swing.*;

public class LaunchTool extends CommandLineUtility {

    /**
     * Constructs the command line utility for launching the diagnostic tool.
     */
    public LaunchTool() {
        super();
    }

    @Override
    public void run(CommandLine commandLine) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    //silently handle
                }

                DiagnosticTool diagnosticTool = new DiagnosticTool();
                diagnosticTool.setIconImages(Settings.getIconImages());
                diagnosticTool.setVisible(true);
            }

        });
    }

    /**
     * Starts the command line utility for launching the diagnostic tool.
     *
     * @param args the command line arguments
     * @throws Exception if an error occurred
     */
    public static void main(String[] args) throws Exception {
        new LaunchTool().start(args);
    }
}
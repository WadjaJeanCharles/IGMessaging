package com.sandrew.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.sandrew.logic.FileUploader;

/**
 * GUI presented to upload a file.
 */
public final class FileUploaderGui extends JFrame {

    private static final long serialVersionUID = 3496122833955078629L;

    private final int textFieldSize = 20;

    private static final int FRAME_WIDTH = 500;

    private static final int FRAME_HEIGHT = 150;

    private final int STATUS_BAR_HEIGHT = 16;

    private File selectedFile;

    private final JLabel labelSelectFile = new JLabel("Select File: ");

    private final JTextField textFilePath = new JTextField(this.textFieldSize);

    private final JButton buttonSelectFile = new JButton("...");

    private final JButton buttonUploadFile = new JButton("Upload");

    private final JMenuBar menuBar;

    private final JPanel panelStatusPanel = new JPanel();

    private final JLabel labelStatusLabel = new JLabel();

    private FileUploader fileUploader;

    public FileUploaderGui() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        // Menu
        this.menuBar = new JMenuBar();
        final JMenu fileMenuItem = this.getFileMenu();
        this.menuBar.add(fileMenuItem);
        this.setJMenuBar(this.menuBar);

        // Button
        this.buttonUploadFile.addActionListener(this.getUploadFileAction());
        this.buttonSelectFile.addActionListener(this.getFileSelectorAction());

        // Layout
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        final JPanel panelFilePanel = new JPanel();
        panelFilePanel.setLayout(new FlowLayout());
        panelFilePanel.add(this.labelSelectFile);
        panelFilePanel.add(this.textFilePath);
        panelFilePanel.add(this.buttonSelectFile);
        mainPanel.add(panelFilePanel);
        final JPanel panelUploadButtoPanel = new JPanel();
        panelUploadButtoPanel.add(this.buttonUploadFile);
        mainPanel.add(panelUploadButtoPanel);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(this.getStatusBar(), BorderLayout.SOUTH);

        this.pack();
        this.setVisible(true);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        this.checkConnection();
    }

    /**
     * Gets the file menu.
     *
     * @return The menu
     */
    private JMenu getFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        // Configure Connection
        final JMenuItem configureConnectionMenuItem = this.getConfigureConnectionMenuItem();
        fileMenu.add(configureConnectionMenuItem);

        return fileMenu;
    }

    /**
     * Creates the menu item to provide connection configuration information.
     *
     * @return The menu item.
     */
    private JMenuItem getConfigureConnectionMenuItem() {
        final JMenuItem openMenuItem = new JMenuItem("Configure Connection");
        openMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final ConnectionConfigurationPanel configureConnectionPanel = ConnectionConfigurationPanel
                                .getInstance();
                final int result = JOptionPane.showConfirmDialog(FileUploaderGui.this, configureConnectionPanel,
                                "Configure Connection", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        FileUploaderGui.this.fileUploader = configureConnectionPanel.getFileUploader();
                    } catch (final URISyntaxException e1) {
                        JOptionPane.showMessageDialog(FileUploaderGui.this,
                                        "The URI to the broker is not well formatted", "Connection Failure",
                                        JOptionPane.ERROR_MESSAGE);
                    } catch (final IllegalArgumentException e2) {
                        JOptionPane.showMessageDialog(FileUploaderGui.this, "The destination cannot be null",
                                        "Connection Failure", JOptionPane.ERROR_MESSAGE);
                    }
                    FileUploaderGui.this.checkConnection();
                }
            }
        });
        return openMenuItem;
    }

    /**
     * Gets the action performed when the upload button is hit.
     *
     * @return The action for the upload button.
     */
    private ActionListener getUploadFileAction() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (FileUploaderGui.this.fileUploader != null) {
                    if (FileUploaderGui.this.selectedFile != null) {
                        try {
                            FileUploaderGui.this.fileUploader.uploadFile(FileUploaderGui.this.selectedFile);
                            // FileUploaderGui.this.cleanUpUserInput();
                        } catch (final JMSSecurityException e2) {
                            JOptionPane.showMessageDialog(FileUploaderGui.this,
                                            "Unable to connect to the broker: wrong credentials", "Upload Failure",
                                            JOptionPane.ERROR_MESSAGE);
                            return;
                        } catch (JMSException | ParserConfigurationException | SAXException | IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(FileUploaderGui.this,
                                            "Failed to upload the file: " + e1.getMessage(), "Upload Failure",
                                            JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(FileUploaderGui.this, "File successfully uploaded",
                                        "Upload Success", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(FileUploaderGui.this, "Please select a file", "Upload Failure",
                                        JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(FileUploaderGui.this,
                                    "Please configure the connection via the File menu", "Upload Failure",
                                    JOptionPane.WARNING_MESSAGE);
                }
            }
        };
    }

    /**
     * Gets the action triggered when the upload button is hit.
     *
     * @return The action listener for the upload button.
     */
    private ActionListener getFileSelectorAction() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser chooserFileToUpload = new JFileChooser();
                final FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
                chooserFileToUpload.setFileFilter(filter);
                chooserFileToUpload.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooserFileToUpload.setDialogTitle("Choose File To Upload");
                if (chooserFileToUpload.showOpenDialog(FileUploaderGui.this) == JFileChooser.APPROVE_OPTION) {
                    FileUploaderGui.this.selectedFile = chooserFileToUpload.getSelectedFile();
                    FileUploaderGui.this.textFilePath.setText(FileUploaderGui.this.selectedFile.getAbsolutePath());
                } else {
                    FileUploaderGui.this.cleanUpUserInput();
                }
            }
        };
    }

    /**
     * Creates the status panel.
     *
     * @return The status panel.
     */
    private JPanel getStatusBar() {

        this.panelStatusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.panelStatusPanel.setPreferredSize(new Dimension(this.getWidth(), this.STATUS_BAR_HEIGHT));
        this.panelStatusPanel.setLayout(new BoxLayout(this.panelStatusPanel, BoxLayout.X_AXIS));

        this.labelStatusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        this.panelStatusPanel.add(this.labelStatusLabel);

        return this.panelStatusPanel;
    }

    /**
     * Checks if the connection has been configured ad gives a visual clue.
     */
    private void checkConnection() {
        if (this.fileUploader == null) {
            this.labelStatusLabel.setText("The connection is NOT configured");
            this.panelStatusPanel.setBackground(Color.RED);
        } else {
            this.labelStatusLabel.setText("The connection is configured");
            this.panelStatusPanel.setBackground(Color.GREEN);
        }
    }

    /**
     * Cleans up the information entered by the user.
     */
    private void cleanUpUserInput() {
        FileUploaderGui.this.selectedFile = null;
        FileUploaderGui.this.textFilePath.setText(StringUtils.EMPTY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FileUploaderGui();
            }
        });
    }
}

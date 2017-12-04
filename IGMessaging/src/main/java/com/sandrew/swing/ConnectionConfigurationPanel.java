package com.sandrew.swing;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.sandrew.logic.FileUploader;

/**
 * Panel gathering connection information.
 */
public final class ConnectionConfigurationPanel extends JPanel {

    private static final long serialVersionUID = -2783615140045145818L;

    private final int textFieldSize = 20;

    private static final int INSET = 10;

    private final JLabel labelBrokerURI = new JLabel("Broker URL: ");

    private final JLabel labelUserName = new JLabel("User Name: ");

    private final JLabel labelUserPassword = new JLabel("User Password: ");

    private final JLabel labelDestinationName = new JLabel("Destination Name: ");

    private final JLabel labelIsTopic = new JLabel("Send to Topic: ");

    private final JTextField textBrokerURI = new JTextField(this.textFieldSize);

    private final JTextField textUserName = new JTextField(this.textFieldSize);

    private final JPasswordField textUserPassword = new JPasswordField(this.textFieldSize);

    private final JTextField textDestinationName = new JTextField(this.textFieldSize);

    private final JCheckBox checkDestinationName = new JCheckBox();

    /**
     * Creates a new connection configuration panel.
     */
    private ConnectionConfigurationPanel() {

        this.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(INSET, INSET, INSET, INSET);

        // Broker URL
        constraints.gridx = 0;
        constraints.gridy += 1;
        this.add(this.labelBrokerURI, constraints);
        constraints.gridx = 1;
        this.add(this.textBrokerURI, constraints);

        // User Name
        constraints.gridx = 0;
        constraints.gridy += 1;
        this.add(this.labelUserName, constraints);
        constraints.gridx = 1;
        this.add(this.textUserName, constraints);

        // User password
        constraints.gridx = 0;
        constraints.gridy += 1;
        this.add(this.labelUserPassword, constraints);
        constraints.gridx = 1;
        this.add(this.textUserPassword, constraints);

        // Destination Name
        constraints.gridx = 0;
        constraints.gridy += 1;
        this.add(this.labelDestinationName, constraints);
        constraints.gridx = 1;
        this.add(this.textDestinationName, constraints);

        // Is Topic
        constraints.gridx = 0;
        constraints.gridy += 1;
        this.add(this.labelIsTopic, constraints);
        constraints.gridx = 1;
        this.add(this.checkDestinationName, constraints);
    }

    /**
     * Creates a file uploader and returns it.
     *
     * @return The file uploader.
     * @throws URISyntaxException
     *             If the URI to the broker is not well formatted.
     * @throws IllegalArgumentException
     *             If the destination or the broker URI is null.
     */
    public FileUploader getFileUploader() throws URISyntaxException {
        checkArgument(StringUtils.isNotBlank(this.textBrokerURI.getText()));
        checkArgument(StringUtils.isNotBlank(this.textDestinationName.getText()));

        if (StringUtils.isBlank(this.textDestinationName.getText())) {
            throw new IllegalArgumentException("The Destination cannot be null");
        }
        final URI brokerUri = new URI(this.textBrokerURI.getText());
        return new FileUploader(brokerUri, this.textUserName.getText(), new String(this.textUserPassword.getPassword()),
                        this.textDestinationName.getText(), this.checkDestinationName.isSelected());
    }

    /**
     * Holder of the configuration panel.
     */
    private static final class ConnectionConfigurationPanelInstanceHolder {

        private static final ConnectionConfigurationPanel instance = new ConnectionConfigurationPanel();
    }

    /**
     * Gets an instance of the connection configuration panel.
     *
     * @return The configuraion panel.
     */
    public static ConnectionConfigurationPanel getInstance() {
        return ConnectionConfigurationPanelInstanceHolder.instance;
    }

}

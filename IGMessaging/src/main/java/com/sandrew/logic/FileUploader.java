package com.sandrew.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Posts a file as a string to the configured ActiveMQ broker. The file is expected to be an XML file. The broker should
 * be up an running before the file is uploaded.
 */
public final class FileUploader extends Communicator {

    /**
     * Configure the file uploader, i.e. the broker information.
     *
     * @param brokerUri
     *            URI to the broker.
     * @param userName
     *            User name to connect to the broker.
     * @param password
     *            Password linked to the user name used to connect to the broker.
     * @param destinationName
     *            Name of the destination of the message.
     * @param isTopic
     *            True if the file should be posted to a topic. It is posted to a queue otherwise, which is the default
     *            behaviour.
     * @throws IllegalArgumentException
     *             If the destination is blank or null.
     * @throws NullPointerException
     *             If the URI to the broker is null;
     */
    public FileUploader(final URI brokerUri, final String userName, final String password, final String destinationName,
                    final boolean isTopic) {
        super(brokerUri, userName, password, destinationName, isTopic);
    }

    /**
     * Uploads an XML file as String.
     *
     * @param fileToUpload
     *            The file to upload.
     * @throws JMSException
     *             If there is a problem while creating either the connection or the session.
     * @throws ParserConfigurationException
     *             If the document builder cannot be created.
     * @throws SAXException
     *             If the file cannot be properly parsed.
     * @throws IOException
     *             If the file cannot be accessed.
     * @throws JMSSecurityException
     *             If the credential to the broker are not correct.
     */
    public void uploadFile(final File fileToUpload)
                    throws JMSException, ParserConfigurationException, SAXException, IOException {
        final Destination destination = this.getDestination();

        final Connection conn = this.getConnection();
        final Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        try {
            // Get the file
            final FileInputStream fis = new FileInputStream(fileToUpload);

            // Serialise it
            final StringWriter stringOut = this.serializeFile(fis);

            // Send the content as String
            final String payload = stringOut.toString();
            final Message msg = session.createTextMessage(payload);
            final MessageProducer producer = session.createProducer(destination);

            producer.send(msg);
            session.close();
        } finally {
            if (session != null) {
                session.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}

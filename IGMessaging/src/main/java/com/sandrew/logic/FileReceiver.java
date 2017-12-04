/**
 *
 */
package com.sandrew.logic;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 *
 */
public class FileReceiver extends Communicator {

    /**
     * Configure the file receiver, i.e. the broker information.
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
    public FileReceiver(final URI brokerUri, final String userName, final String password, final String destinationName,
                    final boolean isTopic) {
        super(brokerUri, userName, password, destinationName, isTopic);
    }

    /**
     * Gets a file from a destination and prints it into the console.
     *
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
    public void receiveFile() throws JMSException, ParserConfigurationException, SAXException, IOException {
        final Destination destination = this.getDestination();
        final Connection conn = this.getConnection();
        final Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        try {
            final MessageConsumer consumer = session.createConsumer(destination);
            conn.start();

            // Get the file as String
            final TextMessage msg = (TextMessage) consumer.receive();
            final String xmlAsString = msg.getText();

            final StringWriter stringOut = this.deserialiseFile(xmlAsString);

            System.out.println("Received File:");
            System.out.println(stringOut.toString());

            session.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}

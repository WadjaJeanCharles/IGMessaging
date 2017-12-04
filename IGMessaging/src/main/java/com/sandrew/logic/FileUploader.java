package com.sandrew.logic;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Posts a file as a string to the configured ActiveMQ broker. The file is expected to be an XML file. The broker should
 * be up an running before the file is uploaded.
 */
public final class FileUploader {

    private final URI brokerUri;

    private final String userName;

    private final String password;

    private final String destinationName;

    private boolean isTopic = false;

    private final ConnectionFactory cFactory;

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
        checkNotNull(brokerUri);
        checkArgument(StringUtils.isNotBlank(destinationName));

        this.brokerUri = brokerUri;
        this.userName = userName;
        this.password = password;
        this.destinationName = destinationName;
        this.isTopic = isTopic;

        if (StringUtils.isBlank(this.userName)) {
            this.cFactory = new ActiveMQConnectionFactory(this.brokerUri);
        } else {
            if (StringUtils.isBlank(this.password)) {
                this.cFactory = new ActiveMQConnectionFactory(this.userName, StringUtils.EMPTY, this.brokerUri);
            } else {
                this.cFactory = new ActiveMQConnectionFactory(this.userName, this.password, this.brokerUri);
            }
        }

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
        Destination destination = null;
        if (this.isTopic) {
            destination = new ActiveMQTopic(this.destinationName);
        } else {
            destination = new ActiveMQQueue(this.destinationName);
        }

        final Connection conn = this.cFactory.createConnection();
        final Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            // Get the file
            dBuilder = dbFactory.newDocumentBuilder();
            final FileInputStream fis = new FileInputStream(fileToUpload);

            // Serialise it
            final Document doc = dBuilder.parse(fis);
            final OutputFormat format = new OutputFormat(doc);
            final StringWriter stringOut = new StringWriter();
            final XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.serialize(doc);

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

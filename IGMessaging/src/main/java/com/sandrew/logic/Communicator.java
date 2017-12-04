package com.sandrew.logic;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public abstract class Communicator {

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
    public Communicator(final URI brokerUri, final String userName, final String password, final String destinationName,
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
     * @return the destinationName
     */
    public String getDestinationName() {
        return this.destinationName;
    }

    /**
     * @return the isTopic
     */
    public boolean isTopic() {
        return this.isTopic;
    }

    /**
     * Creates a new connection.
     *
     * @return A new connection.
     * @throws JMSException
     *             If there is a problem while creating either the connection or the session.
     */
    public Connection getConnection() throws JMSException {
        return this.cFactory.createConnection();
    }

    /**
     * Gets the destination of the message, which can be a topic or a queue.
     *
     * @return The destination of the message.
     */
    public Destination getDestination() {
        if (this.isTopic()) {
            return new ActiveMQTopic(this.getDestinationName());
        } else {
            return new ActiveMQQueue(this.getDestinationName());
        }
    }

    /**
     * Serialise a stream into a string. It is expected that the stream is an XML file.
     *
     * @param stream
     *            The stream to serialise.
     * @return A String writer of the serialised stream.
     *
     * @throws ParserConfigurationException
     *             If the document builder cannot be created.
     * @throws SAXException
     *             If the file cannot be properly parsed.
     * @throws IOException
     *             If the file cannot be accessed.
     */
    public StringWriter serializeFile(final InputStream stream)
                    throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(stream);
        final OutputFormat format = new OutputFormat(doc);
        final StringWriter stringOut = new StringWriter();
        final XMLSerializer serial = new XMLSerializer(stringOut, format);
        serial.serialize(doc);

        return stringOut;
    }

    /**
     * Deserialise a string into an XML document.
     *
     * @param xmlAsString
     *            The XML file as a string.
     * @return A String writer of the XML file.
     * 
     * @throws ParserConfigurationException
     *             If the document builder cannot be created.
     * @throws SAXException
     *             If the file cannot be properly parsed.
     * @throws IOException
     *             If the file cannot be accessed.
     */
    public StringWriter deserialiseFile(final String xmlAsString)
                    throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        final Document doc = dBuilder.parse(new InputSource(new StringReader(xmlAsString)));

        final OutputFormat format = new OutputFormat(doc);
        final StringWriter stringOut = new StringWriter();
        final XMLSerializer serial = new XMLSerializer(stringOut, format);
        serial.serialize(doc);

        return stringOut;
    }
}

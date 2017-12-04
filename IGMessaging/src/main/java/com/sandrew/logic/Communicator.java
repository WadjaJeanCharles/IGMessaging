package com.sandrew.logic;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;

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
}

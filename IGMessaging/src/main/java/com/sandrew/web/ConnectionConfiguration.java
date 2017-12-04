package com.sandrew.web;

public class ConnectionConfiguration {

    private String brokerUri;

    private String userName;

    private String password;

    private String destination;

    private String filePath;

    private boolean isTopic = false;

    private String message = null;

    /**
     * @return the brokerUri
     */
    public String getBrokerUri() {
        return this.brokerUri;
    }

    /**
     * @param brokerUri
     *            the brokerUri to set
     */
    public void setBrokerUri(final String brokerUri) {
        this.brokerUri = brokerUri;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return this.destination;
    }

    /**
     * @param destination
     *            the destination to set
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * @param filePath
     *            the filePath to set
     */
    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the isTopic
     */
    public boolean getIsTopic() {
        return this.isTopic;
    }

    /**
     * @param isTopic
     *            the isTopic to set
     */
    public void setIsTopic(final boolean isTopic) {
        this.isTopic = isTopic;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

}

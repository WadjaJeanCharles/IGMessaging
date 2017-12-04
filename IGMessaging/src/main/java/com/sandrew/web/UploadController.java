package com.sandrew.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.xml.sax.SAXException;

import com.sandrew.logic.FileUploader;

/**
 * Controller of the application.
 */
@Controller
public class UploadController {

    /**
     * Serves the form to get the connection and file information.
     *
     * @param model
     *            The holder of the information.
     * @return the name of the form to serve.
     */
    @GetMapping("/upload")
    public String uploadForm(final Model model) {
        model.addAttribute("uploadConf", new ConnectionConfiguration());
        return "SubmissionForm";
    }

    /**
     * Uploads the file and serves the response.
     * 
     * @param connectionConfiguration
     *            The connection and file information coming from the request.
     * @return The response to serve.
     */
    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute final ConnectionConfiguration connectionConfiguration) {

        URI brokerUri = null;
        try {
            brokerUri = new URI(connectionConfiguration.getBrokerUri());
        } catch (final URISyntaxException e) {
            connectionConfiguration.setMessage("Failed to set up broker: " + e.getMessage());
            e.printStackTrace();
        }
        final FileUploader uploader = new FileUploader(brokerUri, connectionConfiguration.getUserName(),
                        connectionConfiguration.getPassword(), connectionConfiguration.getDestination(),
                        connectionConfiguration.getIsTopic());
        try {
            uploader.uploadFile(new File(connectionConfiguration.getFilePath()));
        } catch (JMSException | ParserConfigurationException | SAXException | IOException e) {
            connectionConfiguration.setMessage("Failed to upload file: " + e.getMessage());
            e.printStackTrace();
        }
        return "Result";
    }
}

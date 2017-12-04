
package com.sandrew.swing;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(FileUploaderGui.class).headless(false).web(false).run(args);
    }

}

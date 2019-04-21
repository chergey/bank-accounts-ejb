package org.elcer.accounts;

import org.apache.openejb.testing.Application;
import org.apache.openejb.testing.ContainerProperties;
import org.apache.tomee.embedded.TomEEEmbeddedApplicationRunner;

@Application
@ContainerProperties(@ContainerProperties.Property(name = "tomee.embedded.application.runner.http", value = "8022"))
public class App {

    public static void main(String[] args) {
        TomEEEmbeddedApplicationRunner.run(new App());
    }
}
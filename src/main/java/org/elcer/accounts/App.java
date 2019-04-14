package org.elcer.accounts;

import org.apache.openejb.testing.*;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.tomee.embedded.TomEEEmbeddedApplicationRunner;
import org.elcer.accounts.app.SampleDataInitializer;
import org.elcer.accounts.services.Synchronizer;
import org.elcer.accounts.services.synchronizers.ReentrantlockSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.net.URL;
import java.util.Properties;

@Application
//@Classes(context = "app")
@ContainerProperties(@ContainerProperties.Property(name = "port", value = "8122"))
//@TomEEEmbeddedApplicationRunner.LifecycleTasks(MyTask.class) // can start a ftp/sftp/elasticsearch/mongo/... server before tomee
//@TomEEEmbeddedApplicationRunner.Configurers(SetMyProperty.class)
public class App {
    @RandomPort("http")
    private int port;

    @RandomPort("http")
    private URL base;

//    @Inject
//    private SampleDataInitializer sampleDataInitializer;

    @Configuration
    public Properties add() {
        return new PropertiesBuilder().p("programmatic", "property").build();
    }

    @PostConstruct
    public void appStarted() {
       // sampleDataInitializer.init();

    }


    public static void main(String[] args) {
        TomEEEmbeddedApplicationRunner.run(new App());
    }
}
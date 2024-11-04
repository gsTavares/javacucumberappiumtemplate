package javacucumberappiumtemplate.singletons;

import java.io.IOException;

import javacucumberappiumtemplate.util.PropertiesReader;

public class PropertiesReaderSingleton {

    private static PropertiesReader propertiesReader;

    public static PropertiesReader getReader() throws IOException {
        if (propertiesReader == null) {
            propertiesReader = new PropertiesReader("application.properties");
        }
        return propertiesReader;
    }

}

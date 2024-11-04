package javacucumberappiumtemplate;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import javacucumberappiumtemplate.singletons.AppiumDriverSingleton;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("javacucumberappiumtemplate")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTest {

    @Before
    public void beforeEach() throws Exception {
        AppiumDriverSingleton.getAndroidDriver();
    }

    @After
    public void afterEach() {
        AppiumDriverSingleton.closeDriver();
    }

}

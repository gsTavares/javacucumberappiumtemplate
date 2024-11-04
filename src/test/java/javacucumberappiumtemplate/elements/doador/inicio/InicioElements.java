package javacucumberappiumtemplate.elements.doador.inicio;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InicioElements {
    
    @FindBy(xpath = "//android.widget.TextView[@text=\"Campanhas recentes\"]")
    private WebElement campanhasRecentes;

    public WebElement getCampanhasRecentes() {
        return campanhasRecentes;
    }

}

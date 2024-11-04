package javacucumberappiumtemplate.elements.login;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginElements {

    @FindBy(xpath = "//android.widget.EditText[@resource-id=\"ion-input-0\"]")
    private WebElement emailInput;

    @FindBy(xpath = "//android.widget.EditText[@resource-id=\"ion-input-1\"]")
    private WebElement passwordInput;

    @FindBy(xpath = "//android.widget.Button[@text=\"Entrar\"]")
    private WebElement loginButton;

    public WebElement getEmailInput() {
        return emailInput;
    }

    public WebElement getPasswordInput() {
        return passwordInput;
    }

    public WebElement getLoginButton() {
        return loginButton;
    }

    

}

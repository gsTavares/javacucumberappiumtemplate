package javacucumberappiumtemplate.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.openqa.selenium.support.PageFactory;

import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javacucumberappiumtemplate.pages.LoginPage;
import javacucumberappiumtemplate.pages.doador.InicioPage;
import javacucumberappiumtemplate.singletons.AppiumDriverSingleton;

public class LoginSteps {

    private LoginPage loginPage;
    private InicioPage inicioPage;
    private AndroidDriver driver;

    public LoginSteps() throws Exception {
        driver = AppiumDriverSingleton.getAndroidDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        inicioPage = PageFactory.initElements(driver, InicioPage.class);
    }

    @Given("que estou na tela de login")
    public void queEstouNaTelaDeLogin() {
        assertTrue(loginPage.getEmailInput().isDisplayed());
    }

    @When("digito os campos de email {string} e senha {string}")
    public void digitoOsCamposDeEmailESenha(String email, String senha) {
        loginPage.getEmailInput().sendKeys(email);
        loginPage.getPasswordInput().sendKeys(senha);
    }

    @And("clico no botao Entrar")
    public void clicoNoBotaoEntrar() {
        loginPage.getLoginButton().click();
    }

    @Then("sou redirecionado para a tela inicio do doador")
    public void souRedirecionadoParaATelaInicioDoador() {
        assertTrue(inicioPage.getCampanhasRecentes().isDisplayed());
    }

}

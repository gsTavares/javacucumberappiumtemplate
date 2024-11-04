# Automação de testes de aplicativos Android utilizando Java (Appium + Cucumber)

## Objetivo

Conseguir automatizar cenários de teste E2E comuns ao longo do desenvolvimento. 

Por exemplo, suponha que na página inicial de um e-commerce foi adicionada uma seção de eletrônicos que, quando o usuário clica nela, ele é redirecionado para a lista de produtos eletrônicos da loja. O QA, além de testar essa nova seção, precisa garantir que o redirecionamento para as outras seções que já existiam continue funcionando corretamente (os chamados "testes regressivos"). 

Esse projeto exemplifica a automação da funcionalidade de login de um aplicativo
## Requisitos

- Java JDK 11 || 17
- Maven versão > 3
- Android Studio
- Appium Server
- Appium Inspector (ou qualquer outra ferramenta que consiga inspecionar o layout do aplicativo Android)

## Glossário

- *Appium: framework de automação multiplataforma*
- *Cucumber: dependência que permite a integração e execução de testes escritos em linguagem Gherkin*

## Etapas

### 1 - Gerar archetype do Cucumber

Com o `maven` previamente configurado, crie uma pasta para o seu projeto de automação, abra o terminal no mesmo diretório e execute:

```shell
mvn archetype:generate                      \
   "-DarchetypeGroupId=io.cucumber"           \
   "-DarchetypeArtifactId=cucumber-archetype" \
   "-DarchetypeVersion=7.20.1"               \
   "-DgroupId=hellocucumber"                  \
   "-DartifactId=hellocucumber"               \
   "-Dpackage=hellocucumber"                  \
   "-Dversion=1.0.0-SNAPSHOT"                 \
   "-DinteractiveMode=false"
```

Esse comando vai gerar um template base, com as dependências do Cucumber e com um teste básico de exemplo

### 2 - Configuração do driver do Android

2.1. Appium uiautomator2

No terminal (tendo o Appium previamente configurado)

```shell
appium driver install uiautomator2
```

```shell
appium driver doctor uiautomator2
```

IMPORTANTE: Se estiver tudo certo com o seu ambiente, o log do segundo comando retornará como abaixo. Caso o contrário, etapas de configuração de ambiente adicionais serão necessárias.

![[Pasted image 20241103184214.png]]

2.2. Configurações para sessão no appium e driver do Android

Aqui não existe muita regra: o importante é que as variáveis essenciais para se criar uma sessão no Appium sejam preenchidas. Nesse projeto, elas foram obtidas a partir da leitura de propriedades do arquivo `pom.xml`:

Classe que faz a leitura das propriedades:
```java
package javacucumberappiumtemplate.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private Properties properties;  
    
    public PropertiesReader(String propertyFileName) throws IOException {
        InputStream is = getClass().getClassLoader()
            .getResourceAsStream(propertyFileName);
        this.properties = new Properties();
        this.properties.load(is);
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }

}
```

Singleton para o leitor de propriedades:
```java
package javacucumberappiumtemplate.singletons;

import java.io.IOException;
import javacucumberappiumtemplate.util.PropertiesReader;

public class PropertiesReaderSingleton {

    private static PropertiesReader propertiesReader;  

	// Garante uma única instância do leitor compartilhada para todo o projeto
    public static PropertiesReader getReader() throws IOException {
        if (propertiesReader == null) {
            propertiesReader = new PropertiesReader("application.properties");
        }
        return propertiesReader;
    }
    
}
```

Classe que inicializa a sessão no appium e o driver para automação no Android
```java
package javacucumberappiumtemplate.singletons;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.options.BaseOptions;

import javacucumberappiumtemplate.util.PropertiesReader;

public class AppiumDriverSingleton {

    private static AppiumDriver driver;
    private static String appiumServerUrl = "http://localhost:4723/";

	// Garante uma única instância do driver compartilhada para todo o projeto
    @SuppressWarnings("rawtypes")
    public static AndroidDriver getAndroidDriver() throws IOException, MalformedURLException {
        if(driver == null) {
            PropertiesReader reader = PropertiesReaderSingleton.getReader();

			// Preenche as variáveis de acordo com as properties do pom.xml
            BaseOptions options = new BaseOptions<>()
                .amend("platformName", reader.getProperty("appium.platformName"))
                .amend("appium:automationName", reader.getProperty("appium.automationName"))
                .amend("appium:platformVersion", reader.getProperty("appium.platformVersion"))
                .amend("appium:app", reader.getProperty("appium.app"));
            
            // Instancia o driver
            driver = new AndroidDriver(new URL(appiumServerUrl), options);
        }
        return (AndroidDriver) driver;
    }

    public static void closeDriver() {
        if(driver != null) {
            driver.quit();
        }
    }
}
```

Tabela com as propriedades principais:

| Propriedade     | Descrição                                                                                                                                                     |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| automationName  | Middleware que o Appium vai utilizar para interagir com a plataforma nativa (no caso do Android é o `uiautomator2` instalado anteriormente via CLI do Appium) |
| platformName    | Nome da plataforma (ex. Android, IOS, Windows etc.)                                                                                                           |
| platformVersion | Versão da plataforma                                                                                                                                          |
| app             | Caminho com o binário da aplicação que será testada (ex. /apps/meu-app.apk)                                                                                   |

### 3 - Gestão do driver para cada caso de teste

É importante que a instância do driver seja controlada corretamente para evitar que várias sessões fiquem abertas no Appium sem necessidade. Para isso, as anotações `@Before` e `@After` podem ser utilizadas

```java
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

	// Ações que são executadas ANTES de cada teste
    @Before
    public void beforeEach() throws Exception {
        AppiumDriverSingleton.getAndroidDriver();
    }

	// Ações que são executadas DEPOIS de cada teste
    @After
    public void afterEach() {
        AppiumDriverSingleton.closeDriver();
    }

}
```

### 4 - Escrita dos cenários

Um dos pacotes da aplicação deve conter os testes escritos em linguagem Gherkin com a extensão `.feature`

Teste da funcionalidade de login utilizado nesse projeto
```gherkin
Feature: Login Conectamor

  Scenario: Efetuar login com sucesso
    Given que estou na tela de login
    When digito os campos de email "teste@email.com" e senha "123"
    And clico no botao Entrar
    Then sou redirecionado para a tela inicio do doador
```

### 5 - Mapeamento dos elementos

Agora é preciso dizer para o seu projeto de automação quais elementos devem ser utilizados no teste. No contexto desse projeto, a apk foi gerada a partir do Ionic. Ou seja, é uma aplicação híbrida

Aqui vai depender muito de como a plataforma organiza a árvore de elementos. E para encontrá-los com maior facilidade sugiro utilizar o Appium Inspector para obter seus respectivos seletores.

Exemplo dos elementos da página de login mapeados
```java
package javacucumberappiumtemplate.elements.login;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginElements {

	// o xpath de cada elemento pode ser obtido por meio do Appium Inspector

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
```

Página de login, que "herda" todos os seus elementos
```java
package javacucumberappiumtemplate.pages;

import javacucumberappiumtemplate.elements.login.LoginElements;

public class LoginPage extends LoginElements {

// aqui você pode implementar métodos customizados que envolvem a manipulação dos elementos em tela

}
```
### 6 - Implementar as etapas dos testes

Por fim, basta implementar quais ações o middleware de automação deve fazer para que o teste seja concluído com sucesso.

Exemplo dos passos para efetuar o login com um usuário válido no aplicativo

```java
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

    public LoginSteps() throws Exception {]
	    // Recebe a instância compartilhada do driver
        driver = AppiumDriverSingleton.getAndroidDriver();
        // Espera um tempo até a aplicação abrir totalmente no celular
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        // Preenche os elementos em memória pelo xpath informado
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        inicioPage = PageFactory.initElements(driver, InicioPage.class);
    }

    @Given("que estou na tela de login")
    public void queEstouNaTelaDeLogin() {
		// Se o input de email está sendo mostrado em tela, significa que o usuário está no login
        assertTrue(loginPage.getEmailInput().isDisplayed());
    }

    @When("digito os campos de email {string} e senha {string}")
    public void digitoOsCamposDeEmailESenha(String email, String senha) {
	    // Preenche os dados do usuário no formulário
        loginPage.getEmailInput().sendKeys(email);
        loginPage.getPasswordInput().sendKeys(senha);
    }

    @And("clico no botao Entrar")
    public void clicoNoBotaoEntrar() {
	    // Faz a ação de click no botão "Entrar"
        loginPage.getLoginButton().click();
    }

    @Then("sou redirecionado para a tela inicio do doador")
    public void souRedirecionadoParaATelaInicioDoador() {
	    // Se o título da tela de início do doador está sendo mostrado, significa que o usuário foi redirecionado corretamente 
        assertTrue(inicioPage.getCampanhasRecentes().isDisplayed());
    }
    
}
```

### Possíveis melhorias

- Integrar com uma biblioteca de relatórios para visualizar melhor os resultados dos testes
- Adaptar o projeto para testes em mais de uma plataforma (talvez executar o mesmo teste com mais de um driver)
- Utilização de containers (a configuração de ambiente feita é toda local na máquina)

## Links úteis

https://appium.io/docs/en/latest/quickstart/uiauto2-driver/
https://github.com/appium/java-client/blob/master/docs/Page-objects.md
https://cucumber.io/docs/guides/10-minute-tutorial/?lang=java
https://github.com/appium/appium-inspector/releases
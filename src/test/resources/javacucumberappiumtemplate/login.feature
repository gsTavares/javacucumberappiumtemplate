Feature: Login Conectamor

  Scenario: Efetuar login com sucesso
    Given que estou na tela de login
    When digito os campos de email "teste@email.com" e senha "123"
    And clico no botao Entrar
    Then sou redirecionado para a tela inicio do doador

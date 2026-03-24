
## 🛡️ Qualidade e Segurança

### Cobertura de Testes (JaCoCo)
Conforme os requisitos da Fase 1, os testes automatizados focaram nos domínios críticos (**Domain** e **Application**), atingindo coberturas superiores a **80%**.
Abaixo um exemplo da cobertura de testes obtida pelo JaCoCo, por meio de plugin do IntelliJ:

![img.png](doc/img/jacoco_plugin_intellij.png)

Foi adicionado também a dependência do Jacoco no projeto podendo gerar o relatório de cobertura via Maven com o comando:
```bash
mvn clean verify
```
Arquivo ficará disponível em:
`target/site/jacoco/index.html`

![jacoco_dependence_index.png](doc/img/jacoco_dependence_index.png)

No relatório gerado, temos a cobertura detalhada por pacote e classe, destacando a cobertura acima de 80% para os pacotes `core`.

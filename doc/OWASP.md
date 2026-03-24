
### Análise de Vulnerabilidades (OWASP Dependency-Check)

Utilizamos a ferramenta **OWASP Dependency-Check** integrada ao Maven para análise estática de dependências (SCA).
Para executar o scan, é necessário obter uma **API KEY**, pelo site: https://nvd.nist.gov/developers/request-an-api-key
```bash
mvn org.owasp:dependency-check-maven:check -Dnvd.api.key=<API_KEY>
```
#### Durante a primeira execução foi observados alguns pontos de atenão:

**Pontos identificados e mitigados:**
1.  **Spring Boot DevTools (CRITICAL)**: Vulnerabilidade relacionada ao `SnakeYAML`.
    * **Mitigação**: A dependência foi configurada com `<optional>true</optional>` e escopo `test`. Além disso, utilizamos **Multi-stage Build** no Dockerfile para garantir que o JAR final de produção contenha apenas o JRE e o código necessário, removendo o DevTools completamente da imagem final.
2.  **Swagger UI (MEDIUM)**: Relacionada à biblioteca `DOMPurify` nos assets estáticos.
    * **Mitigação**: Atualização do starter `springdoc-openapi` para a versão mais recente e recomendação de desativação do endpoint em ambientes produtivos críticos.

![first_owasp_execution.png](doc/img/first_owasp_execution.png)

Após a mitigação dos pontos acima, uma nova análise foi realizada, não sendo mais identificadas vulnerabilidades. <br>
![second_owasp_execution.png](doc/img/second_owasp_execution.png)

---

## 🗺️ Entregas de Design (DDD)

A documentação completa contendo o **Event Storming** (Criação de OS e Gestão de Peças), o **Dicionário de Linguagem Ubíqua** e os diagramas de contexto podem ser acessados no [Miro](https://miro.com/app/board/uXjVID97lew=/?share_link_id=974727696482).


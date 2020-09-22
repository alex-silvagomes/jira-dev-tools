### Tecnologias utilizadas/requeridas

* Atlassian/SDK for Jira
* Atlassian/Jira Product
* Atlassian/Jira API _Restfull 

* Índice
  * [1. Configure o ambiente de desenvolvimento: Atlassian SDK, ScriptRunner, Eclipse e Maven/Atlassian SDK](#1-configure-o-ambiente-de-desenvolvimento-atlassian-sdk-scriptrunner-eclipse-e-mavenatlassian-sdk)
  * [2. Criar o projeto do Jira plugin com o Atlassian SDK](#2-criar-o-projeto-do-jira-plugin-com-o-atlassian-sdk)
  * [3. Configurar arquivo pom.xml do projeto criado para integrar com ScriptRunner](#3-configurar-arquivo-pomxml-do-projeto-criado-para-integrar-com-scriptrunner)
  * [4. Gerar o pacote executável (.obr) do Plugin](#4-gerar-o-pacote-executável-obr-do-plugin)
  * [5. Gerar o pacote executável (.obr) do Plugin](#8-gerar-o-pacote-executável-obr-do-plugin)
  * [6. Instalar o pacote (.obr) do Plugin no Jira](#9-instalar-o-pacote-obr-do-plugin-no-jira)
  * [7. Execute o plugin no Console do ScriptRunner](#10-execute-o-plugin-no-console-do-scriptrunner)
  * [Referencias](#referencias)

---

### 1. Configure o ambiente de desenvolvimento: Atlassian SDK, ScriptRunner, Eclipse e Maven/Atlassian SDK

* [1.1. Configurar o Maven do Eclipse apontando para a instalação customizada pelo Atlassian SDK]
  * __dica__: Recomendo que você separe um Workspace do Eclipse para esta configuração

1.1.1. Navegar para o menu do Eclipse Windows >> Preference 

1.1.2. Na caixa de diálogo Preference clicar no list-box menu lateral esquerdo Maven e expandir a árvore 

1.1.3. No list-box menu com a árvore de Maven expandida clicar no sub-menu User settings 

1.1.4. Na caixa de diálogo User Settings da hierarquia de menu Prefrences >> Maven >> User Settings

Alterar o campo User Settings para a configuração que está no diretório do Atlas

```eclipse
C:\Apps\Atlassian\atlassian-plugin-sdk-8.0.16\apache-maven-3.5.4\conf\settings.xml
```

1.1.5. Na caixa de diálogo User Settings da hierarquia clicar no botão `reindex`

* [1.2. Configurar o Maven customizado pela Atlassian SDK para incluir as bibliotecas do ScriptRunner](setup-scriptrunner-dev-environment-for-eclipse-step-by-step.md#2-configurar-o-maven-customizado-pela-atlassian-sdk-para-incluir-as-bibliotecas-do-scriptrunner)

```cmd
C:\> notepad C:\Apps\Atlassian\atlassian-plugin-sdk-8.0.16\apache-maven-3.5.4\conf\settings.xml

C:\> notepad C:\Apps\Atlassian\atlassian-plugin-sdk-8.0.16\apache-maven-3.5.4\conf\settings.xml
        :
    <repositories>
        :
    <!-- Atlassian repositories will be listed above this line -->
    <!-- Adaptavist repositories are below -->
    <repository>
      <id>adaptavist-external</id>
      <url>https://nexus.adaptavist.com/content/repositories/external</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>never</updatePolicy>
        <checksumPolicy>warn</checksumPolicy>
      </snapshots>
      <releases>
        <enabled>true</enabled>
        <checksumPolicy>warn</checksumPolicy>
      </releases>
    </repository>

    <repository>
      <id>adaptavist-external-snapshots</id>
      <url>https://nexus.adaptavist.com/content/repositories/external-snapshots</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>never</updatePolicy>
        <checksumPolicy>warn</checksumPolicy>
      </snapshots>
      <releases>
        <enabled>true</enabled>
        <checksumPolicy>warn</checksumPolicy>
      </releases>
    </repository>
    <!-- End of Adaptavist maven repositories -->

    :
    </repositories>
```

### 2. Criar o projeto do Jira plugin com o Atlassian SDK

* 2.1. Criar o esqueleto do projeto com o Atlassian SDK

```cmd
C:\..\ws-github-10> atlas-create-jira-plugin
Define value for groupId: : br.com.inmetrics.jira
Define value for artifactId: : service-manager-plugin
Define value for version: 1.0.0-SNAPSHOT: : 2020.06.23.1020
Define value for package: br.com.inmetrics.jira: : br.com.inmetrics.jira.<plugin-name>
Y : : Y
  :
```

### 3. Configurar arquivo `pom.xml` do projeto criado para integrar com ScriptRunner

3.1. Configurar `pom.xml` do projeto para incluir as dependências do ScriptRunner. Você deverá incluir trechos nos seguintes pontos:
* `<dependencies> .. </dependencies>`
* `<properties> .. </properties>`

```cmd
C:\> notepad C:\..\<plugin-name>\pom.xml
```

```xml
        :
    <version>2020.06.23.0950</version>
        :
    <organization>
        <name>Inmetrics</name>
        <url>http://wwww.inmetrics.com.br/</url>
    </organization>
        :
    <dependencies>
        :
        <!-- Add dependency on ScriptRunner Adaptavist -->
        <dependency>
          <groupId>com.onresolve.jira.groovy</groupId>
          <artifactId>groovyrunner</artifactId>
          <version>4.1.3.26</version>
          <scope>provided</scope>
        </dependency>

        <dependency>
          <groupId>org.codehaus.groovy</groupId>
          <artifactId>groovy-all</artifactId>
          <version>${groovy.version}</version>
          <scope>provided</scope>
        </dependency>
        :
    <dependencies>
        :
    <properties>
            :
        <groovy.version>2.2.1</groovy.version>
        <scriptrunner.version>4.1.3.26</scriptrunner.version>
            :
    </properties>
        :
```


### 4. Gerar o pacote executável (.obr) do Plugin

4.1. Executar o Maven/Atlassian-SDK CLEAN do projeto

```cmd
C:\..\service-manager-plugin> atlas-clean
```

4.2. Executar o Maven/Atlassian-SDK PACKAGE do projeto

```cmd
C:\..\service-manager-plugin> atlas-package
```

4.3. Conferir o pacote gerado

```cmd
C:\..\plugin-name> dir .\target\plugin-name-*
```

```console
  :
23/06/2020  10:29         3.342.136 plugin-name-2020.06.23.0950-tests.jar
23/06/2020  10:29         2.295.749 plugin-name-2020.06.23.0950.jar
23/06/2020  10:29         2.117.927 plugin-name-2020.06.23.0950.obr
  :
```



### 4. Importar o projeto Atlassian-SDK no Eclipse

4.1. No Eclipse, navegar para opção de menu: `Eclipse :: File >> Import ...`

4.2. Na caixa de diálogo `Import`, escolher o seguinte item da lista de opções hierárquica `Maven >> Existing Maven Project`

4.3. Na caixa de diálogo `Import Maven Projects`, informar no campo `Root Directory` o caminho do projeto `C:\..\plugin-name`. Em seguida marcar a seleção do `pom.xml` e clicar no botão `Finish`

4.4. Aguardar a importação do projeto no Eclipse

4.5. Pode ignorar eventuais erros com a mensagem `Setup Maven Plugins Connector` e `Resolve Later`

4.6. Pode ignorar eventuais erros no arquivo `pom.xml` na tag `<plugin>` e `<execution>`
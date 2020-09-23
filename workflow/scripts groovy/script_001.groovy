// Import commons libraries
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

// Import log libraries
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger
import org.apache.log4j.Level
log.setLevel(Level.DEBUG)

// Instance Issue Manager, Component Accessor, Custom Fields, etc
log.info("Instance Issue Manager, Component Accessor, Custom Fields, etc");
def issueManager = ComponentAccessor.getIssueManager()
def customFieldManager = ComponentAccessor.getCustomFieldManager()

// Teste para Jira Groovy Console
def issueKey = "TICKET-44"
def issue = issueManager.getIssueObject(issueKey)

// Get current issue fields
def TICKET_ID = String.format("%012d", Integer.parseInt(issue.getId().toString()));
def TICKET_NUMBER = issue.getKey();  
def TICKET_STATUS = issue.getStatus().name;
def TICKET_PIPELINE = issue.getCustomFieldValue(customFieldManager.getCustomFieldObject("customfield_11801")) as String
def TICKET_TIPO_MUDANCA = issue.getCustomFieldValue(customFieldManager.getCustomFieldObject("customfield_11803")) as String
def TICKET_NUM_MUDANCA = issue.getCustomFieldValue(customFieldManager.getCustomFieldObject("customfield_11800"))  as String
def TICKET_RDM_NUMBER_INT = Integer.parseInt("${TICKET_NUM_MUDANCA}");

// Concatena Type "RDM" e Number "450879" = "RDM450879" - Retira os Zeros a esquerda ao converter para Integer. Obs. Assim a api identifica a RDM.
def RDM_NUMBER = new String("${TICKET_TIPO_MUDANCA}${TICKET_RDM_NUMBER_INT}");

// Check Validator - TIPO_MUDANCA = 'RDM'
if (!TICKET_TIPO_MUDANCA || TICKET_TIPO_MUDANCA != "RDM") {
    throw new InvalidInputException("ERRO: Falha na validacao do campo 'Tipo de Mudança'. Valor(es) esperados: ['RDM']. Valor informado: '" + TICKET_TIPO_MUDANCA + "' !")
}

// Specify that classes from this plugin should be available to this script
log.info("@WithPlugin('br.com.inmetrics.jira.service-manager-plugin')");
import br.com.inmetrics.jira.servicemanagerplugin.api.MyPluginComponent
@WithPlugin("br.com.inmetrics.jira.service-manager-plugin")

// Inject plugin module
@PluginModule
MyPluginComponent myPluginComponent
log.info("@PluginModule");

// Service Manager - Url, Username, Password, StatusList
// * PROD: "http://10.196.93.69:13095/sc62server/PWS/RequestForChangePadrao.wsdl" 
// * TH: "http://10.194.52.116:13095/sc62server/PWS/RequestForChangePadrao.wsdl"
// * PROD/NAT: "http://192.168.84.23:13095/sc62server/PWS/RequestForChangePadrao.wsdl" 
// * TH/NAT: "http://192.168.248.59:13095/sc62server/PWS/RequestForChangePadrao.wsdl"
def SERVICEMANAGER_WSDL = new String("http://10.194.52.116:13095/sc62server/PWS/RequestForChangePadrao.wsdl")
def SERVICEMANAGER_USERNAME = new String("OPER_ESTEIRADEVSECOPS")
def SERVICEMANAGER_PASSWORD = new String("D3v53C0p5@2o2o")

// Set Environment with JIRA_BASE_URL
def JIRA_BASE_URL = ComponentAccessor.getApplicationProperties().getString("jira.baseurl")
def ENVIRONMENT = "DEV"

switch(JIRA_BASE_URL) { 
    case "https://jira.bradesco.com.br:8443": 
        // Environment (PROD) - Producao. 
        SERVICEMANAGER_WSDL = new String("http://10.196.93.69:13095/sc62server/PWS/RequestForChangePadrao.wsdl")
        ENVIRONMENT = "PRODUCAO"
        log.info("Set 'PRODUCAO' environment");
    break
    default:
        // Environment (TH) - Como padrao.
        SERVICEMANAGER_WSDL = new String("http://10.194.52.116:13095/sc62server/PWS/RequestForChangePadrao.wsdl")
        ENVIRONMENT = "TH"
        log.info("Set 'HOMOLOGACAO' environment");
    break
} 


def SERVICEMANAGER_STATUS_LIST = []
switch(TICKET_STATUS) { 
    case "APROVADO DS": 
        // transition - Aprovar GMUD
        SERVICEMANAGER_STATUS_LIST = ["SOLICITADA", "EM APROVACAO", "EM AUTORIZACAO", "EM EXECUCAO"]
        log.info("Set requireds status (Aprovar GMUD)");
    break
    case "APROVADO GMUD": 
        // transition - Aprovar PROD
        SERVICEMANAGER_STATUS_LIST = ["EM EXECUCAO"]
        log.info("Set requireds status (Aprovar PROD)");
    break
    default:
        // transition - Aprovar DS e Enviar para TI (TU > TI)
        SERVICEMANAGER_STATUS_LIST = ["EM ELABORACAO", "ABERTA"] 
        log.info("Set requireds status (default)");
    break
} 

// Invoke method getRetrieveRequestForChangePadrao() from component
log.info("")
log.info("Invoke method getRetrieveRequestForChangePadrao(SERVICEMANAGER_WSDL, SERVICEMANAGER_USERNAME, SERVICEMANAGER_PASSWORD, TARGET_RDM_NUMBER)");
log.info("")
log.info("  - JIRA_BASE_URL.............: '${JIRA_BASE_URL}'");
log.info("  - ENVIRONMENT...............: '${ENVIRONMENT}'");
log.info("  - SERVICEMANAGER_WSDL.......: '${SERVICEMANAGER_WSDL}'");
log.info("  - SERVICEMANAGER_USERNAME...: '${SERVICEMANAGER_USERNAME}'");
log.info("  - SERVICEMANAGER_STATUS_LIST: '${SERVICEMANAGER_STATUS_LIST}'");
log.info("  - RDM_NUMBER................: '${RDM_NUMBER}'");
log.info("")
log.info("  - TICKET_PIPELINE:       '${TICKET_PIPELINE}'")
log.info("  - TICKET_NUMBER:         '${TICKET_NUMBER}'")
log.info("  - TICKET_ID:             '${TICKET_ID}'")
log.info("  - TICKET_STATUS:         '${TICKET_STATUS}'")
log.info("")
def hashMap = myPluginComponent.getRetrieveRequestForChangePadrao(SERVICEMANAGER_WSDL, SERVICEMANAGER_USERNAME, SERVICEMANAGER_PASSWORD, RDM_NUMBER) 
log.info("  - hashMap: " + hashMap);
def RDM_STATUS = hashMap.status as String
log.info("  - RDM_STATUS................: " + RDM_STATUS );
    
// Check Validator - RDM_STATUS IN SERVICEMANAGER_STATUS_LIST
if (!RDM_STATUS || RDM_STATUS == "null") {
    throw new InvalidInputException("ERRO: Falha na validacao do campo 'Status da RDM'. Não foi possível recuperar o campo do Service Manager (SIGS) para a '" + RDM_NUMBER + " !")
} else if (!SERVICEMANAGER_STATUS_LIST.contains(RDM_STATUS)) {
    if (!TICKET_PIPELINE == "CONTINGENCIA"){
        throw new InvalidInputException("ERRO: Falha na validacao do campo 'Status da RDM'. Valor(es) esperados: "+ SERVICEMANAGER_STATUS_LIST + " -  Valor recuperado do Service Manager (SIGS) para a '" + RDM_NUMBER + "': '" + RDM_STATUS + "' !")
    }
}


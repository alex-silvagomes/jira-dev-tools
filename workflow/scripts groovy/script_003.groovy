// *********************************** //
// POST BAMBOO API - APPROVE > APRCHGM
// *********************************** //

// Jira Lib
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.component.ComponentAccessor;
import org.apache.commons.codec.binary.Base64;
import com.opensymphony.workflow.*
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.customfields.option.Option
    
// Logs Lib
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger
import org.apache.log4j.Level
log.setLevel(Level.DEBUG)


// Teste para Jira Groovy Console
def issueKey = "TICKET-49"
def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueObject(issueKey)

// Busca campos do Ticket
def cFieldManager = ComponentAccessor.getCustomFieldManager()
def optionsManager = ComponentAccessor.getOptionsManager();
    
// Numero do Pacote Changeman
def TICKET_PACKAGE_NUMBER = issue.getCustomFieldValue(cFieldManager.getCustomFieldObject("customfield_11805"))
def CCUSTO_FROM_PACKAGE = "${TICKET_PACKAGE_NUMBER}".substring(0,4) 
def cf_CCUSTO = cFieldManager.getCustomFieldObject("customfield_11807")

def fieldConfig = cf_CCUSTO.getRelevantConfig(issue);
def CCUSTO_Options = optionsManager.getOptions(fieldConfig).toListString()

log.info("")
log.info("Default Fields")
log.info("")
log.info("TICKET_PACKAGE_NUMBER:  '${TICKET_PACKAGE_NUMBER}'")
log.info("CCUSTO_FROM_PACKAGE:    '${CCUSTO_FROM_PACKAGE}'")
log.info("CCUSTO_Options:         '${CCUSTO_Options}'")
log.info("")


// Check Validator - TICKET CENTRO DE CUSTO
if (!CCUSTO_Options.contains("${CCUSTO_FROM_PACKAGE}")) {
    throw new InvalidInputException("ERRO: O centro de custo '${CCUSTO_FROM_PACKAGE}' nao esta cadastrado, favor solicitar o cadastro e tentar novamente.")
}





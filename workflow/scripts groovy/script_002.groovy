// *********************************** //
// POST BAMBOO API - APPROVE > APRCHGM
// *********************************** //

// Jira Lib
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.component.ComponentAccessor;
import org.apache.commons.codec.binary.Base64;
import com.opensymphony.workflow.*
// Groovy Lib
import groovyx.net.http.RESTClient.*
// Logs Lib
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger
import org.apache.log4j.Level
log.setLevel(Level.DEBUG)

// Teste para Jira Groovy Console
def issueKey = "TICKET-53"
def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueObject(issueKey)

// Busca campos do Ticket
def cFieldManager = ComponentAccessor.getCustomFieldManager()
def TICKET_SUMMARY = issue.getSummary()

log.info("")
log.info("Default Fields")
log.info("")
log.info("TICKET_SUMMARY:  '${TICKET_SUMMARY}'")
log.info("")


// Check Validator - TICKET_SUMMARY
if (!TICKET_SUMMARY || TICKET_SUMMARY == "null") {
    throw new InvalidInputException("ERRO: Falha na validacao do campo 'Titulo do Pacote - Resumo', deve conter um valor.")
} else if (TICKET_SUMMARY.contains("'") || TICKET_SUMMARY.contains("/")) {
    throw new InvalidInputException("ERRO: Falha na validacao do campo 'Titulo do Pacote - Resumo', nao pode conter os caracteres ( ' ) ou ( / ).")
}





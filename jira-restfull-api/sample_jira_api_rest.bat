REM https://docs.atlassian.com/software/jira/docs/api/REST/8.12.2/#api/2/myself-getUser

REM "# ----------------------------------------------------- #"
REM "# SCRIPT RUN - Call JIRA REST API to get myself         #"
REM "# ----------------------------------------------------- #"
REM ""

SET JIRA_BASEURL="https://192.168.84.64:8085"
SET JIRA_USER="husrpsap"
SET JIRA_PASS="husrpsap@123"

curl -k -X POST --user %JIRA_USER%:%JIRA_PASS% -H "Content-Type: application/json" "https://192.168.84.64:8085/rest/api/2/myself"

# JiraDataDemo

The purpose of application is to obtain data from JIRA and persist it in both XML and JSON files.

Please import the project in Eclipse as Maven project, build it and run it as application.

1. Enter in the console number the represents chosen format - 1 for XML, 2 for JSON.
2. Application logs information for every bulk of 50 issues that are retrieved from REST API.For example:
**VIII 05, 2017 9:19:06 PM com.retriever.jira.JiraIssueRetriever getBasicInformation**
**INFO: 50 issues were retrieved!**
3. After all of the information is retrieved the following message is displayed.
**INFO: File was successfully created! - IssuesXML.xml**

Created file is located under current directory of the application. The file name depends on file format - 
IssuesXML.xml for XML format or IssuesJSON.json for JSON format. 

The file is replaced everytime the application is run for chosen format.

Some examples of the test logs are located in test results folder.

Cheers!

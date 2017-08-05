package com.retriever.jira;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.retriever.jira.model.Issue;
import com.retriever.jira.model.Issues;
import com.retriever.jira.utils.FormatEnum;

public class IssueRetrieverMain {

	private static final Logger LOGGER = Logger
			.getLogger(IssueRetrieverMain.class.getName());

	public static void main(String[] args) {

		System.setProperty("javax.xml.bind.context.factory",
				"org.eclipse.persistence.jaxb.JAXBContextFactory");
		System.out.print("Please choose data format - 1 for XML, 2 for JSON:");
		Scanner scanner = new Scanner(System.in);
		int num = scanner.nextInt();
		scanner.close();

		FormatEnum formatType = null;
		boolean isFormatValid = true;
		switch (num) {
		case 1:
			formatType = FormatEnum.XML;
			break;
		case 2:
			formatType = FormatEnum.JSON;
			break;
		default:
			isFormatValid = false;
			break;
		}

		if (!isFormatValid) {
			LOGGER.log(Level.SEVERE, "Not valid format chosen!");
		} else {
			int startAt = 0;
			int maxResults = 50;
			int total = 0;
			
			//Get the first bulk of info from the API
			JiraIssueRetriever retriever = new JiraIssueRetriever();
			List<Issue> issuesList = retriever.getJiraIssueInformation(startAt,
					maxResults);
			total = retriever.getTotal();

			//Proceed until all information is set to object issueList
			while (startAt + maxResults < total) {
				startAt += maxResults;
				List<Issue> issuesListTemp = retriever.getJiraIssueInformation(
						startAt, maxResults);
				issuesList.addAll(issuesListTemp);
			}
			Issues resultEntityFinal = new Issues();
			resultEntityFinal.setIssues(issuesList);
			resultEntityFinal.setTotal(total);
			LOGGER.log(Level.INFO, "All issues count: " + issuesList.size());

			//Save issue data in file
			JiraIssueFileSaver fileSaver = new JiraIssueFileSaver();
			fileSaver.saveIssueDataInFile("", formatType.getName(),
					resultEntityFinal);
		}
	}

}

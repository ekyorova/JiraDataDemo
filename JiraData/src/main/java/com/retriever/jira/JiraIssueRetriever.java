package com.retriever.jira;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.retriever.jira.model.Comments;
import com.retriever.jira.model.CommentsEntity;
import com.retriever.jira.model.Issue;
import com.retriever.jira.model.Issues;

public class JiraIssueRetriever {

	private static final Logger LOGGER = Logger
			.getLogger(IssueRetrieverMain.class.getName());

	private final static String JIRA_URL = "https://jira.atlassian.com/browse/";
	private final static String REST_API_URL = "https://jira.atlassian.com/rest/api/2/search/";
	private final static String REST_API_URL_COMMENT = "https://jira.atlassian.com/rest/api/2/issue/";
	private final static String QUERY = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";
	private final static String ENDPOINT_COMMENT = "/comment";
	private final static String JQL = "jql";

	private int total;
	private final Client client;
	private LinkedList<Issue> issuesList;
	private Issues resultEntity;

	public JiraIssueRetriever() {
		client = ClientConnection.getInstance();
		total = 0;
		resultEntity = new Issues();

	}

	/**
	 * Get JIRA Issue Information from API
	 * 
	 * @param startAt
	 *            - starting issue at the result
	 * @param maxResults
	 *            - number of returned issues after startAt
	 * @return List of all issues per bulk
	 **/
	public List<Issue> getJiraIssueInformation(int startAt, int maxResults) {
		issuesList = new LinkedList<Issue>();
		resultEntity = getBasicInformation(startAt, maxResults);
		total = resultEntity.getTotal();
		ExecutorService EXEC = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		List<Callable<Issue>> tasks = new ArrayList<Callable<Issue>>();
		for (final Issue issue : resultEntity.getIssues()) {
			Callable<Issue> c = new Callable<Issue>() {
				@Override
				public Issue call() throws Exception {
					Issue issueTemp = issue;
					List<Comments> comments = getCommentsInformation(issue
							.getKey());
					if (!comments.isEmpty()) {
						issueTemp.setComments(comments);
						issueTemp.setUrl(JIRA_URL + issue.getKey());
					}
					return issueTemp;
				}
			};
			tasks.add(c);
		}

		try {
			List<Future<Issue>> results = EXEC.invokeAll(tasks);
			for (Future<Issue> fr : results) {
				issuesList.add(fr.get());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			EXEC.shutdown();
		}
		return issuesList;
	}

	/**
	 * Get total count of all the issues
	 * 
	 * @return Total count number
	 **/
	public int getTotal() {
		return total;
	}

	private Issues getBasicInformation(int startAt, int maxResults) {
		WebTarget webTarget = client.target(REST_API_URL)
				.queryParam(JQL, QUERY).queryParam("startAt", startAt)
				.queryParam("maxResults", maxResults);
		Invocation.Builder invocationBuilder = webTarget
				.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		Issues resultEntity = null;
		if (response != null) {
			if (response.getStatus() == 200) {
				resultEntity = response.readEntity(Issues.class);
				if (resultEntity.getIssues() != null) {
					LOGGER.log(Level.INFO, resultEntity.getIssues().size()
							+ " issues were retrieved!");
				} else {
					LOGGER.log(Level.WARNING, "No issues were retrieved! ");
				}
			} else {
				LOGGER.log(
						Level.SEVERE,
						"Not successfull call! Status code: "
								+ response.getStatus() + ", status info: "
								+ response.getStatusInfo());
			}
		}
		return resultEntity;
	}

	private List<Comments> getCommentsInformation(String issueKey) {
		WebTarget webTarget = client.target(REST_API_URL_COMMENT + issueKey
				+ ENDPOINT_COMMENT);
		Invocation.Builder invocationBuilder = webTarget
				.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		List<Comments> listOfComments = new ArrayList<Comments>();
		if (response != null) {
			if (response.getStatus() == 200) {
				CommentsEntity resultEntity = response
						.readEntity(CommentsEntity.class);
				listOfComments = resultEntity.getComments();
				// LOGGER.log(Level.INFO, listOfComments.size() +
				// "comments were retrieved for ticket number" + issueKey);
			} else {
				LOGGER.log(
						Level.SEVERE,
						"Not successfull call! Status code: "
								+ response.getStatus() + ", status info: "
								+ response.getStatusInfo() + " for issue " + issueKey);
			}
		}
		return listOfComments;
	}

}

package com.retriever.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junitx.framework.FileAssert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.retriever.jira.JiraIssueFileSaver;
import com.retriever.jira.model.Author;
import com.retriever.jira.model.Comments;
import com.retriever.jira.model.Fields;
import com.retriever.jira.model.Issue;
import com.retriever.jira.model.IssueType;
import com.retriever.jira.model.Issues;
import com.retriever.jira.model.Priority;
import com.retriever.jira.model.Reporter;

public class JiraIssueFileSaverTest {

	private String jsonFile;
	private String xmlFile;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private Issues issues;

	@Before
	public void setup() {
		System.setProperty("javax.xml.bind.context.factory",
				"org.eclipse.persistence.jaxb.JAXBContextFactory");

		issues = initIssueInfo();
		jsonFile = buildJSON();
		xmlFile = buildXML();
	}

	@Test
	public void calculateFileNameTest() {
		String fileNameExpected = "IssuesXML.xml";
		JiraIssueFileSaver saver = new JiraIssueFileSaver();
		String fileNameActual = saver.calculateFileName("XML");
		assertEquals(fileNameExpected, fileNameActual);
	}

	@Test
	public void saveIssueDataInFileJSONTest() {
		String fileNameExpected = "IssuesJSONExp.json";
		createTempFileByName(fileNameExpected, "JSON");
		JiraIssueFileSaver saver = new JiraIssueFileSaver();
		String fileNameOriginal = saver.calculateFileName("JSON");
		saver.saveIssueDataInFile(folder.getRoot().getPath(), "JSON", issues);
		assertTrue(new File(folder.getRoot().getPath() + "\\"
				+ fileNameOriginal).exists());
		FileAssert.assertEquals(new File(folder.getRoot().getPath() + "\\"
				+ fileNameExpected), new File(folder.getRoot().getPath() + "\\"
				+ fileNameOriginal));
	}

	@Test
	public void saveIssueDataInFileXMLTest() {
		String fileNameExpected = "IssuesXMLExp.xml";
		createTempFileByName(fileNameExpected, "XML");

		JiraIssueFileSaver saver = new JiraIssueFileSaver();
		String fileNameOriginal = saver.calculateFileName("XML");
		saver.saveIssueDataInFile(folder.getRoot().getAbsolutePath(), "XML",
				issues);
		assertTrue(new File(folder.getRoot().getPath() + "\\"
				+ fileNameOriginal).exists());
		FileAssert.assertEquals(new File(folder.getRoot().getPath() + "\\"
				+ fileNameOriginal), new File(folder.getRoot().getPath() + "\\"
				+ fileNameOriginal));
	}

	private void createTempFileByName(String fileName, String type) {
		File tempFileXml;
		FileWriter writer = null;
		try {
			tempFileXml = folder.newFile(fileName);
			writer = new FileWriter(tempFileXml);
			if (type.equals("XML")) {
				writer.write(xmlFile);
			} else {
				writer.write(jsonFile);
			}
			writer.close();
		} catch (IOException e) {
			assertFalse(
					"Test setup fail! Temp test file do not created properly !"
							+ e.getMessage(), true);
		} finally {
			try {
				writer.close();
			} catch (IOException e1) {
				assertFalse(
						"Test setup fail! Temp test file do not created properly !"
								+ e1.getMessage(), true);
			}
		}
	}

	private String buildXML() {
		StringBuilder builder = new StringBuilder();
		builder.append("<issuesRoot><issues><comments><author><displayName>Yuliya Kozarevska</displayName></author>");
		builder.append("<body>Test body comment 1</body></comments>");
		builder.append("<comments><author><displayName>emad shanab</displayName></author><body>Test body comment 2</body>");
		builder.append("</comments><comments><author><displayName>dflkja</displayName></author>");
		builder.append("<body>Test body comment 3</body></comments>");
		builder.append("<fields><created>2017-07-06T19:13:31.000+0000</created>");
		builder.append("<description>Test description</description>");
		builder.append("<issuetype><name>Bug</name></issuetype><priority><name>High</name></priority>");
		builder.append("<reporter><name>afigas</name></reporter>");
		builder.append("<summary>Test summary</summary></fields>");
		builder.append("<key>TRANS-2433</key><url>https://jira.atlassian.com/browse/TRANS-2433</url>");
		builder.append("</issues><total>1</total></issuesRoot>");
		return builder.toString();
	}

	private String buildJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"issuesRoot\":{\"issues\":[{\"comments\":[{\"author\":{");
		builder.append("\"displayName\":\"Yuliya Kozarevska\"},\"body\":\"Test comment 1\"},{");
		builder.append("\"author\":{\"displayName\":\"emad shanab\"},\"body\":\"Test comment 2\"},{");
		builder.append("\"author\":{\"displayName\":\"dflkja\"},\"body\":\"Test comment 3\"}],");
		builder.append("\"fields\":{\"created\":\"2017-07-06T19:13:31.000+0000\",\"description\":\"Test description\",");
		builder.append("\"issuetype\":{\"name\":\"Bug\"},\"priority\":{\"name\":\"High\"},");
		builder.append("\"reporter\":{\"name\":\"afigas\"},\"summary\":\"Test summary\"},");
		builder.append("\"key\":\"TRANS-2433\",\"url\":\"https://jira.atlassian.com/browse/TRANS-2433\"");
		builder.append("}],\"total\":1}}");
		return builder.toString();
	}

	private Issues initIssueInfo() {
		Issues issuesTest = new Issues();
		issuesTest.setTotal(1);
		Issue issueObj = new Issue();
		issueObj.setKey("TRANS-2433");
		issueObj.setUrl("https://jira.atlassian.com/browse/TRANS-2433");
		Fields fields = new Fields();
		fields.setCreated("2017-07-06T19:13:31.000+0000");
		fields.setDescription("Test description");
		IssueType issueType = new IssueType();
		issueType.setName("Bug");
		fields.setIssuetype(issueType);
		Priority priority = new Priority();
		priority.setName("High");
		fields.setPriority(priority);
		Reporter reporter = new Reporter();
		reporter.setName("afigas");
		fields.setReporter(reporter);
		fields.setSummary("Test summary");
		issueObj.setFields(fields);
		Comments comment = new Comments();
		Author author = new Author();
		author.setDisplayName("Yuliya Kozarevska");
		comment.setAuthor(author);
		comment.setBody("Test comment 1");

		Comments comment2 = new Comments();
		Author author2 = new Author();
		author2.setDisplayName("emad shanab");
		comment2.setAuthor(author2);
		comment2.setBody("Test comment 2");

		Comments comment3 = new Comments();
		Author author3 = new Author();
		author3.setDisplayName("dflkja");
		comment3.setAuthor(author3);
		comment3.setBody("Test comment 3");

		List<Comments> comments = new ArrayList<>();
		comments.add(comment);
		comments.add(comment2);
		comments.add(comment3);
		issueObj.setComments(comments);
		List<Issue> issueCollection = new ArrayList<>();
		issueCollection.add(issueObj);
		issuesTest.setIssues(issueCollection);
		return issuesTest;
	}

}

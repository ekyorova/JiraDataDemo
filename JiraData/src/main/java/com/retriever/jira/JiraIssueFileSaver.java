package com.retriever.jira;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;

import com.retriever.jira.model.Issues;
import com.retriever.jira.utils.FormatEnum;

public class JiraIssueFileSaver {

	private static final Logger LOGGER = Logger
			.getLogger(JiraIssueFileSaver.class.getName());

	/**
	 * Save list of issues in file. File can be in XML or JSON Format.
	 * 
	 * @param path
	 *            - File path for the file
	 * @param formatType
	 *            - XML or JSON format type
	 * @param issuesList
	 *            - List of all issues' information
	 **/
	public void saveIssueDataInFile(String path, String formatType,
			Issues issuesList) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Issues.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			if (formatType.equals(FormatEnum.XML.getName())) {
				jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE,
						MediaType.APPLICATION_XML);
			} else {
				jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE,
						MediaType.APPLICATION_JSON);
			}

			String fileName = calculateFileName(formatType);
			jaxbMarshaller
					.marshal(issuesList, new File(path.isEmpty() ?  fileName : path + "\\" + fileName));
			LOGGER.log(Level.INFO, "File was successfully created! - "
					+ fileName);
		} catch (JAXBException e) {
			LOGGER.log(Level.SEVERE,
					"Data was not successfully written in file in format "
							+ formatType + ":", e.getCause());
		}
	}

	/**
	 * Calculate file name using the format typee
	 * 
	 * @param formatType
	 *            - XML or JSON format type
	 * @return Generated file name
	 **/
	public String calculateFileName(String formatType) {
		return "Issues" + formatType + "." + formatType.toLowerCase();
	}
}

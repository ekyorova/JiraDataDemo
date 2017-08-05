package com.retriever.jira.model;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement()
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentsEntity {
	
	private LinkedList<Comments> comments;

	public LinkedList<Comments> getComments() {
		return comments;
	}

	public void setComments(LinkedList<Comments> comments) {
		this.comments = comments;
	}

}

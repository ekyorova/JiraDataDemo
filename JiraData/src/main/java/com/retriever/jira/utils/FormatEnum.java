package com.retriever.jira.utils;

public enum FormatEnum {
    XML("XML"),
    JSON("JSON");

    private String name;

    FormatEnum(String name) {
        this.name = name;
    }

	public String getName() {
		return name;
	}

}

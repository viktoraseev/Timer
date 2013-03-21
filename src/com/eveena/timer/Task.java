package com.eveena.timer;

public class Task {
	private char key;
	private String name;
	public Task(char key, String name) {
		this.key = key;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char getKey() {
		return key;
	}
	public void setKey(char key) {
		this.key = key;
	}
}
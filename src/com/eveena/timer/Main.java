package com.eveena.timer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Main {
	private static final long TIME_PERIOD = 5 * 1000; // 5 minutes
	private static final long TIME_TRESHHOLD = 1000; // 1 second
	private static final Task IDLE = new Task(' ', "Idle");
	private List<Task> taskList = new ArrayList<Task>();
	private long lastTime = System.currentTimeMillis();
	private Task currentTask = IDLE;
	private int lines = 100;
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException {
		System.err.println("Timer 1.0");
		if (args.length == 0) {
			System.err.println("Usage: timer tasks.properties > timesheet.csv");
			System.err.println("Timer will output instructions to strerr and time periods to stdout every 5 minutes");
			System.err.println("");
			System.err.println(
					"Properties file format: \n" + 
					"task.name.1=My Task\n" +
					"task.key.1=a\n");
			return;
		}
		Main main = new Main();
		main.load(args);
		main.run();
	}
	
	public void load(String[] args) throws IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(args[0]));
		for(int i=1;i<1000;i++) {//hack ;)
			String name = prop.getProperty("task.name." + i);
			String key = prop.getProperty("task.key." + i);
			if (name == null || key == null)
				break;
			taskList.add(new Task(key.charAt(0), name));
		}
		taskList.add(IDLE);
	}
	
	/**
	 * Counts time, writing timestamp if user was in task mode at least TIME_PERIOD
	 * @throws IOException
	 */
	public void run() throws IOException {
		printInfo();
		while(true) {
			char key = readInput();
			if (key != 0) {
				Task newTask = null;
				for(Task t : taskList) {
					if (t.getKey() == key) {
						newTask = t;
						break;
					}
				}
				if (newTask == null) {
					System.err.println("Task not found for key '" + key + "'");
					continue;
				}
				checkTime();
				currentTask = newTask;
				System.err.println("Switched to task '" + currentTask.getName() + "'");
			}
			
			checkTime();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignore) {
			}
		}
	}
	
	private void checkTime() {
		long time = System.currentTimeMillis();
		long period = time - lastTime;
		if (period < 0) {
			lastTime = time;
			currentTask = IDLE;
			System.err.println("Back to the future detected. Time Reset, task changed to IDLE.");
			printInfo();
			return;
		}
		if (period < TIME_PERIOD)
			return;
		if (period > TIME_PERIOD + TIME_TRESHHOLD) {
			lastTime = time;
			currentTask = IDLE;
			System.err.println("Future comes faster then expected. Time reset, task changed to IDLE.");
			printInfo();
			return;
		}
		lastTime = time;
		if (currentTask == IDLE)
			return;
		System.out.println(currentTask.getName() + "," + TIME_PERIOD / 1000 + "," + new Date(lastTime));
		System.err.println("STDERR:" + currentTask.getName() + "," + TIME_PERIOD / 1000 + "," + new Date(lastTime));
		lines++;
		if (lines > 15) {
			lines = 0;
			printInfo();
		}
	}

	/**
	 * Reads key from keyboard and return it. Return 0 if key was not pressed. if several keys was pressed, returns last one.
	 * @return
	 * @throws IOException
	 */
	private char readInput() throws IOException {
		if (System.in.available() == 0)
			return 0;
		int c = 0;
		
		// read only last char
		while(System.in.available() > 0) {
			int newC = System.in.read();
			if (newC == 10 || newC == 13)
				continue;
			c = newC;
		}
		if (c == 'x' || c == 'X')
			System.exit(0);
		return (char) c;
	}

	private void printInfo() {
		System.err.println("Current task is '" + currentTask.getName() + "'");
		for(Task t : taskList) {
			System.err.println("Press '" + t.getKey() + "' to '" + t.getName() + "'");
		}
		System.err.println("Press 'x' to exit");
	}
}

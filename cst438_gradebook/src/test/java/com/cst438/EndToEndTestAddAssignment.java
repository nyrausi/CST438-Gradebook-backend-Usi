package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@SpringBootTest
public class EndToEndTestAddAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 2500; // 2.5 seconds
	
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	//set assignment due date to 24 hours ago
	public static final String TEST_DUE_DATE = (new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)).toString();
	public static final int TEST_COURSE_ID = 123456;
	
	public static final String TEST_INVALID_DUE_DATE = "2022-";
	public static final int TEST_INVALID_COURSE_ID = -1;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {
		
		// deletes any previously existing test assignments in the database
		Assignment x = null;
        do {
        	x = null;
        	for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)) {
        			x = assignment;
        			break;
        		}
        	}
            if (x != null)
            	assignmentRepository.delete(x);
        } while (x != null);
					
		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			
			driver.findElement(By.id("Add")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// use xPath to find elements, input values, and add new assignment
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_ASSIGNMENT_NAME);
			driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys(TEST_DUE_DATE);
			driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys(Integer.toString(TEST_COURSE_ID));
			driver.findElement(By.id("Submit")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// verify that the added test assignment is now in the database
			boolean foundTestAssignment = false;
			for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)) {
        			foundTestAssignment = true;
        			break;
        		}
        	}
			assertTrue(foundTestAssignment, " Could not find assignment with name, " + TEST_ASSIGNMENT_NAME + ", in database. Please try again. ");
			
			// verify that the successful toast message pops up
			String toastText = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals("Assignment successfully added", toastText);

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			Assignment testAssignment = null;

        	for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)) {
        			testAssignment = assignment;
        			break;
        		}
        	}
        	
	        if (testAssignment != null) {
	        	assignmentRepository.delete(testAssignment);
	        }

			driver.quit();
		}

	}
	
	@Test
	public void invalidAddAssignmentTest() throws Exception {
		
		// deletes any previously existing test assignments in the database
		Assignment x = null;
        do {
        	x = null;
        	for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME) || assignment.getName().equals("")) {
        			x = assignment;
        			break;
        		}
        	}
            if (x != null)
            	assignmentRepository.delete(x);
        } while (x != null);
					
		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			
			driver.findElement(By.id("Add")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// test for invalid assignment name (blank name)
			driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys(TEST_DUE_DATE);
			driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys(Integer.toString(TEST_COURSE_ID));
			driver.findElement(By.id("Submit")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// verify that the test assignment is not in the database
			boolean notFoundTestAssignment = true;
			for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals("")) {
        			notFoundTestAssignment = false;
        			break;
        		}
        	}
			assertTrue(notFoundTestAssignment, " Found invalid assignment with blank name in database. Please try again. ");
			
			// verify that the failed toast message pops up
			String toastText = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals("Adding assignment failed", toastText);
			
			// verify that message prompting user to enter a name pops up
			String msgText = driver.findElement(By.id("Msg")).getText();
			assertEquals("Please enter a name for the assignment.", msgText);
			
			// test for invalid assignment due date
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_ASSIGNMENT_NAME);
			driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys(Keys.chord(Keys.CONTROL, "a"), TEST_INVALID_DUE_DATE);
			driver.findElement(By.id("Submit")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// verify that the test assignment is not in the database
			notFoundTestAssignment = true;
			for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)) {
        			notFoundTestAssignment = false;
        			break;
        		}
        	}
			assertTrue(notFoundTestAssignment, " Found invalid assignment with invalid due date in database. Please try again. ");
			
			// verify that the failed toast message pops up
			toastText = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals("Adding assignment failed", toastText);
			
			// test for invalid assignment course id
			driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys(Keys.chord(Keys.CONTROL, "a"), TEST_DUE_DATE);
			driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys(Keys.chord(Keys.CONTROL, "a"),Integer.toString(TEST_INVALID_COURSE_ID));
			driver.findElement(By.id("Submit")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// verify that the test assignment is not in the database
			notFoundTestAssignment = true;
			for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)) {
        			notFoundTestAssignment = false;
        			break;
        		}
        	}
			assertTrue(notFoundTestAssignment, " Found invalid assignment with invalid course ID in database. Please try again. ");
			
			// verify that the failed toast message pops up
			toastText = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals("Adding assignment failed", toastText);			

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  in case, clean up database so the test is repeatable.
			 */
			Assignment testAssignment = null;

        	for (Assignment assignment : assignmentRepository.findAll()) {
        		if (assignment.getName().equals(TEST_ASSIGNMENT_NAME) || assignment.getName().equals("")) {
        			testAssignment = assignment;
        			break;
        		}
        	}
        	
	        if (testAssignment != null) {
	        	assignmentRepository.delete(testAssignment);
	        }

			driver.quit();
		}

	}
}
package ru.greg3d.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

	public static WebElement waitElementIsClickable(WebDriver driver, WebElement element, Integer waitTimeInSeconds) {
	    if (waitTimeInSeconds == null) {
	    	waitTimeInSeconds = 10;
	    }
	    WebDriverWait wait = new WebDriverWait(driver, waitTimeInSeconds);
	    return wait.until(ExpectedConditions.elementToBeClickable(element));
	}
		//
		// Дожидаемся состояния страницы $.active = 0
		//
	public static void WaitPageIsActive(WebDriver driver){
    	WebDriverWait wait = new WebDriverWait(driver, 5000);
    	try{
    	wait.until(new ExpectedCondition<Boolean>(){
            public Boolean apply(WebDriver d) {
        		if(((JavascriptExecutor)d).executeScript("return $.active").toString().equals("0"))
        			return true;
        		return false;
            }
         });
    	}catch(Exception e){}
    }
	
		//
		// Дожидаемся состояния страницы $.active = 1
		//
	public static void WaitPageIsNotActive(WebDriver driver){
    	WebDriverWait wait = new WebDriverWait(driver, 5/100, 100);

    	try{
    	wait.until(new ExpectedCondition<Boolean>(){
            public Boolean apply(WebDriver d) {
        		if(((JavascriptExecutor)d).executeScript("return $.active").toString().equals("1"))
        			return true;
        		return false;
            }
         });
    	}catch(Exception e){}
    }		
}

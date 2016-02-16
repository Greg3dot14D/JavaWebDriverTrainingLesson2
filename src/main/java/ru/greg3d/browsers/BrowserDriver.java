/**
 * 
 */
/**
 * @author greg3d
 *
 */
package ru.greg3d.browsers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.events.EventFiringWebDriver;


import ru.greg3d.events.WebEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserDriver{
	private static WebDriver _driver;
	private static Logger LOG = LoggerFactory.getLogger(BrowserDriver.class);
	
	public synchronized static WebDriver newDriver() {
		_driver = new EventFiringWebDriver(BrowserFactory.getBrowser());
		WebEventListener eventListener = new WebEventListener();
		((EventFiringWebDriver)_driver).register(eventListener);
//		_driver = BrowserFactory.getBrowser();
		return _driver;
    }	
	
	public static WebDriver getCurrentDriver() {
        return _driver;
    }	
	
    public static String getCurrentUrl(){
    	return _driver.getCurrentUrl();
    }
	
	public static void close(){
        if(_driver != null)
		try {
        	_driver.quit();
            _driver = null;
        } catch (UnreachableBrowserException e) {
        	LOG.error("cannot close browser: unreachable browser + " + e.getMessage());
        }
        catch (NullPointerException e){
        	LOG.error("cannot close browser: NullPointer" + e.getMessage());
        }
    }
	
	public static void loadPage(String url){
        LOG.debug("Directing browser to:" + url);
        getCurrentDriver().get(url);
	}
}
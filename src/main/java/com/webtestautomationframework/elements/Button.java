package com.webtestautomationframework.elements;

import com.webtestautomationframework.steps.BasePage;
import cucumber.api.java.en.Given;
import org.openqa.selenium.By;

public class Button extends BasePage{



    @Given("^user clicks on \"([^\"]*)\" button$")
    public void userClicksOnButtonWithLabel(String buttonLabel){
        String buttonLocatorByLabel="//*[normalize-space()='"+buttonLabel+"']";
        actions.click(actions.findVisibleElementWithoutScrolling(By.xpath(buttonLocatorByLabel)));
    }


}

package com.webtestautomationframework.elements;

import com.webtestautomationframework.steps.BasePage;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class InputTextField extends BasePage {

    private static final String inputFieldFollowingLabel = "//following-sibling::*/input";
    private static final String inputTextAreaFollowingLabel = "//following-sibling::*/textarea";

    @When("^user types \"([^\"]*)\" in \"([^\"]*)\" input field on \"([^\"]*)\" page$")
    public void user_types_in_inputField_following_label(String value, String inputLabel, String formName) {
        sendKeysToFiledAfterClearing(value, inputLabel, inputFieldFollowingLabel);
    }


    @When("^user types \"([^\"]*)\" in \"([^\"]*)\" input text area on \"([^\"]*)\" page$")
    public void user_types_in_textArea_following_label(String value, String inputLabel, String formName) {
        sendKeysToFiledAfterClearing(value, inputLabel, inputTextAreaFollowingLabel);
    }

    private void sendKeysToFiledAfterClearing(String value, String inputLabel, String inputFieldFollowingLabel) {
        String inputFieldLocator= "//*[@class='input-label' and contains(text(),'"+inputLabel+"')]"+ inputFieldFollowingLabel;
        WebElement inputElement = actions.waitUntilVisible(By.xpath(inputFieldLocator));
        inputElement.clear();
        inputElement.sendKeys(value);
    }


}

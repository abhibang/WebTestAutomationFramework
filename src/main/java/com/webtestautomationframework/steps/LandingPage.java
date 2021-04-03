package com.webtestautomationframework.steps;

import com.webtestautomationframework.utils.Timeout;
import com.webtestautomationframework.utils.core.DriverFactory;
import cucumber.api.java.en.Given;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LandingPage extends BasePage {


    private static final Logger logger = LoggerFactory.getLogger(LandingPage.class);

    private static final String XPATH_AD_BOX = "//div[@class='ad-box']";

    @FindBy(xpath = XPATH_AD_BOX +"//a[contains(text(),'SIGN UP  →')]")
    @CacheLookup
    private WebElement signUp;

    @FindBy(xpath = "//div[@class='main-content']//a[normalize-space()='Coco']")
    @CacheLookup
    private WebElement cocoStoreHomePage;

    public LandingPage() {
        PageFactory.initElements(DriverFactory.getDriver(), this);
    }

    @Given("^user is on Coco store Home page$")
    public void userIsCocoStoreHomePage() {
        actions.waitUntilPageLoad(Timeout.THREE_SECONDS);
        Assert.assertTrue("Coco store Home Page should be displayed",actions.isVisible(cocoStoreHomePage));
    }

    @Given("^advertisement box is displayed with text \"([^\"]*)\" and price \"([^\"]*)\" with \"([^\"]*)\" button$")
    public void adBoxIsDisplayed(String textInAdBox, String priceInAdBox, String buttonLabel) {
        String locator = XPATH_AD_BOX + "//div[@class='title' and contains(normalize-space(),'"+textInAdBox+"')]";
        Assert.assertTrue(textInAdBox +" should be displayed in adBox",actions.isVisible(By.xpath(locator),Timeout.VERY_SHORT));

        String[] price = priceInAdBox.split(" ");

        String currencySymbol = XPATH_AD_BOX + "//div[@class='price']/child::*[contains(.,'"+price[0]+"')]";
        Assert.assertTrue(price[0] +" currency should should be displayed",actions.isVisible(By.xpath(currencySymbol),Timeout.VERY_SHORT));

        String amount = XPATH_AD_BOX + "//div[@class='price']/child::*[contains(.,'"+price[1]+"')]";
        Assert.assertTrue(price[1] +" amount should should be displayed",actions.isVisible(By.xpath(amount),Timeout.VERY_SHORT));


        String buttonLabelLocator= XPATH_AD_BOX + "//a[normalize-space()='"+buttonLabel+"']";
        Assert.assertTrue(buttonLabel +" button should be displayed ",actions.isVisible(By.xpath(buttonLabelLocator),Timeout.VERY_SHORT));
    }

    @Given("^\"([^\"]*)\" button is displayed on Home page in advertisement box$")
    public void signUpLinkIsDisplayed(String signUpButtonLabel) {
        Assert.assertTrue(signUpButtonLabel +" should be displayed below ad box ",actions.isVisible(signUp));
    }


}


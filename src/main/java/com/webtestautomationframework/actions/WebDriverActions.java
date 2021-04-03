package com.webtestautomationframework.actions;

import com.webtestautomationframework.utils.Timeout;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebDriverActions {
    WebDriver driver;
    private Logger log = Logger.getLogger(WebDriverActions.class);
    public WebDriverActions(WebDriver driver) {
        this.driver = driver;
    }

    public void clearText(WebElement locator) {
        WebElement element = findVisibleElement(locator);
        element.clear();
    }

    public void clearTextAfterWaiting(By locator, Timeout timeout) {
        WebElement element = waitUntilVisible(locator, timeout);
        element.clear();
    }



    public void nativeSeleniumClick(WebElement button) {
        button.click();
    }

    public void nativeClick(WebElement button) {
            click(button);
    }

    public void click(WebElement locator) {
        WebElement element = waitUntilClickable(locator);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        try {
            executor.executeScript("arguments[0].click();", element);
        } catch (StaleElementReferenceException e) {
            log.warn("Element was stale immediately after waiting to be clickable. Waiting for element to be clickable again.");
            element = waitUntilClickable(locator);
            executor.executeScript("arguments[0].click();", element);
        }
    }
    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    public WebElement findElement(By locator, WebElement parent) {
        return parent.findElement(locator);
    }

    public WebElement findElement(By locator) {
        return driver.findElement(locator);
    }

    public List<WebElement> findElements(By locator, WebElement parent) {
        return parent.findElements(locator);
    }

    public WebElement findVisibleElement(WebElement webElement) {
        return waitUntilVisible(webElement, Timeout.SHORT);
    }

    public WebElement findVisibleElement(By locator) {
        return waitUntilVisible(locator, Timeout.LONG);
    }
    public WebElement findVisibleElementWithoutScrolling(By locator) {
        return waitUntilVisibleWithoutScrolling(locator, Timeout.VERY_SHORT);
    }

    public WebElement findVisibleElementWithoutScrolling(By locator, WebElement parent) {
        return waitUntilVisible(parent, locator);
    }

    public WebElement findVisibleElement(By locator, WebElement parent) {
        return waitUntilVisible(parent, locator);
    }

    public List<WebElement> findVisibleElements(WebElement locator) {
        WebDriverWait wait = new WebDriverWait(driver, Timeout.AVERAGE.inSeconds());
        return wait.until(ExpectedConditions.visibilityOfAllElements(locator));

    }

    public List<WebElement> findVisibleElements(By locator, WebElement parent) {
        List<WebElement> elements = findElements(locator, parent);
        WebDriverWait wait = new WebDriverWait(driver, Timeout.AVERAGE.inSeconds());
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public WebElement getElement(By locator) {
        WebElement element;
        List<WebElement> elements = findElements(locator);
        try {
            element = elements.isEmpty() ? null : elements.get(0);
            if (element != null)
                element.isDisplayed();
        } catch (WebDriverException invalid) {
            return null;
        }
        return element;
    }

    public WebElement getElement(By locator, WebElement parent) {
        List<WebElement> elements = findElements(locator, parent);
        WebElement element;
        try {
            element = elements.isEmpty() ? null : elements.get(0);
            if (element != null)
                element.isDisplayed();
        } catch (WebDriverException invalid) {
            return null;
        }
        return element;
    }

    public String[] getOptionsFromDropdown(By dropdownLocator) {
        Select dropdown = new Select(driver.findElement(dropdownLocator));
        return getTextValuesFromElements(dropdown.getOptions());
    }

    public String getSelectedOptionInDropdown(WebElement dropdownLocator) {
        Select dropdown = new Select(findVisibleElement(dropdownLocator));
        return dropdown.getFirstSelectedOption().getText();
    }



    public String[] getTextValuesFromElements(List<WebElement> elements) {
        return getAttributeFromElements(elements, "innerText");
    }

    public List<String> getTextFromElements(List<WebElement> elements) {
        return elements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getTextContentFromElements(List<WebElement> elements) {
        return elements.stream()
                .map(this::getTextContentFromElement)
                .collect(Collectors.toList());
    }

    public String getTextValueFromEditElement(WebElement element) {
        return element.getAttribute("value");
    }

    public String getTextValueFromEditElement(By editLocator) {
        WebElement editElement = driver.findElement(editLocator);
        return getTextValueFromEditElement(editElement);
    }

    public String[] getAttributeFromElements(List<WebElement> elements, String attribute) {
        return elements.stream()
                .map(element -> element.getAttribute(attribute))
                .map(String::trim)
                .toArray(String[]::new);
    }

    Boolean listOfElementIsNotEmpty(WebElement locator) {
        try {
            List<WebElement> elements = findVisibleElements((locator));
            return !elements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isVisible(WebElement element) {
        if (element == null) {
            return false;
        }
        try {
            return element.isDisplayed() && element.getSize().getHeight() > 0 && element.getSize().getWidth() > 0;
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isVisible(By locator, Timeout timeout) {
        boolean isVisible = false;
        for (long stop = System.currentTimeMillis() + timeout.inMillis(); !isVisible && stop > System.currentTimeMillis(); ) {
            isVisible = driver.findElements(locator).size() > 0 && driver.findElement(locator).isDisplayed();
        }
        return isVisible;
    }



    public void selectOptionFromDropdown(String option, WebElement dropdown) {
        dropdown.findElement(By.xpath(String.format("./option[text()='%s']", option))).click();
        dispatchInputEvent(dropdown);
    }

    public void dispatchInputEvent(WebElement element) {
        String script = "const element = arguments[0];" +
                "element.dispatchEvent(new Event('input', { bubbles: true}));";
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(script, element);
    }

    public WebElement waitUntilClickable(By locator, Timeout timeout) {
        final String errorMessage = String.format("Element '%s' never became clickable after '%d' seconds", locator, timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        return wait.withMessage(errorMessage)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class)
                .until(webDriver -> {
                    WebElement element = getElement(locator);
                    if (element != null) {
                        scrollIntoView(element);
                        WebElement visibleElement = ExpectedConditions.visibilityOf(element).apply(driver);
                        try {
                            if (visibleElement != null && visibleElement.isEnabled()) {
                                return visibleElement;
                            }
                            return null;
                        } catch (StaleElementReferenceException e) {
                            return null;
                        }
                    } else return null;
                });
    }

    public WebElement waitUntilClickable(WebElement element) {
        return waitUntilClickable(element, Timeout.AVERAGE);
    }

    public WebElement waitUntilClickable(WebElement element, Timeout timeout) {
        final String errorMessage = String.format("Element never became clickable after '%d' seconds", timeout.inSeconds());
        scrollIntoView(element);
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        return wait.withMessage(errorMessage)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.elementToBeClickable(element));
    }



    public WebElement waitUntilClickable(By locator) {
        return waitUntilClickable(locator, Timeout.AVERAGE);
    }

    public WebElement waitUntilVisible(By locator, Timeout timeout) {
        WebElement element = waitUntilVisibleWithoutScrolling(locator, timeout);
        return element;
    }

    public WebElement waitUntilVisibleWithoutScrolling(By locator, Timeout timeout) {
        final String errorMessage = String.format("Element '%s' never became visible after '%d' seconds", locator, timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        return wait.withMessage(errorMessage)
                .ignoring(WebDriverException.class)
                .until(getVisibleElement(locator));
    }

    private Function<? super WebDriver, WebElement> getVisibleElement(By locator) {
        return webDriver -> {
            List<WebElement> elements = webDriver.findElements(locator);
            for (WebElement element : elements)
                if (element.isDisplayed())
                    return element;
            return null;
        };
    }

    public WebElement waitUntilVisible(By locator) {
        return waitUntilVisible(locator, Timeout.AVERAGE);
    }

    public void waitForTextInElementNotToBeEmpty(WebElement element) {
        FluentWait<WebDriver> wait = new WebDriverWait(driver, Timeout.SHORT.inSeconds());
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(element, "text", "")));
    }

    public WebElement waitUntilVisible(WebElement element, Timeout timeout) {
        final String errorMessage = String.format("Element never became visible after '%d' seconds", timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        wait.withMessage(errorMessage)
                .until(ExpectedConditions.visibilityOf(element));
        scrollIntoView(element);
        return element;
    }

    public WebElement waitUntilVisible(WebElement element) {
        return waitUntilVisible(element, Timeout.AVERAGE);
    }

    public void waitUntilNotVisible(By locator, Timeout timeout) {
        final String errorMessage = String.format("Element '%s' still visible after '%d' seconds", locator, timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        try {
            wait.withMessage(errorMessage)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw e;
        } catch (WebDriverException ignored) {
        }
    }

    public void waitUntilNotVisible(WebElement element) {
        waitUntilNotVisible(element, Timeout.AVERAGE);
    }

    public void waitUntilNotVisible(WebElement element, Timeout timeout) {
        final String errorMessage = String.format("Element still visible after '%d' seconds", timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        try {
            wait.withMessage(errorMessage)
                    .until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            throw e;
        } catch (WebDriverException ignored) {
        }
    }

    public void waitUntilNotPresent(WebElement element, Timeout timeout) {
        final String errorMessage = String.format("Element still visible after '%d' seconds", timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        try {
            wait.withMessage(errorMessage)
                    .until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException e) {
            throw e;
        } catch (WebDriverException ignored) {
        }
    }

    public WebElement waitUntilPresent(By locator, Timeout timeout) {
        final String errorMessage = String.format("Element never became present after '%d' seconds", timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        return wait.withMessage(errorMessage)
                .until(ExpectedConditions.presenceOfElementLocated(locator));

    }

    public void waitUntilNotVisible(By locator) {
        waitUntilNotVisible(locator, Timeout.AVERAGE);
    }

    public void waitUntilNotPresent(WebElement element) {
        waitUntilNotPresent(element, Timeout.AVERAGE);
    }

    public WebElement waitUntilVisible(WebElement parent, By locator) {
        return waitUntilVisible(parent, locator, Timeout.AVERAGE);
    }

    public WebElement waitUntilVisible(WebElement parent, By locator, Timeout timeout) {
        final String errorMessage = String.format("Element located by %s never became visible after '%d' seconds", locator, timeout.inSeconds());
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        return wait.withMessage(errorMessage)
                .until(visibilityOfNestedElementLocatedBy(parent, locator));
    }

    private ExpectedCondition<WebElement> visibilityOfNestedElementLocatedBy(WebElement parent, By locator) {
        return webDriver -> {
            Boolean displayed = false;
            Boolean exists = false;
            try {
                exists = parent.findElements(locator).size() > 0;
                displayed = parent.findElement(locator).isDisplayed();
            } catch (Exception e) {
                int i = 0;
            }
            return (exists && displayed) ?
                    parent.findElement(locator) :
                    null;
        };
    }

    /*  Methods used for executing JS scripts
     */
    public Object executeScriptReturningObject(WebElement element, String script) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return executor.executeScript(script, element);
    }

    public Object executeScriptReturningObject(String script) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return executor.executeScript(script);
    }

    public String getTextContentFromElement(WebElement element) {
        String script = "return arguments[0].textContent";
        return executeScriptReturningObject(element, script).toString();
    }

    public WebElement executeJavaScriptReturningVisibleElement(String script) {
        FluentWait<WebDriver> wait = new WebDriverWait(driver, Timeout.SHORT.inSeconds());
        wait.withMessage("Element was not found visible.");
        return wait.until((WebDriver webDriver) -> {
            WebElement element = (WebElement) executeScriptReturningObject(script);
            return (element != null && element.isDisplayed()) ? element : null;
        });
    }

    public WebElement executeJavaScriptReturningElement(String script) {
        FluentWait<WebDriver> wait = new WebDriverWait(driver, Timeout.SHORT.inSeconds());
        wait.withMessage("Element was not found visible.");
        return wait.until((WebDriver webDriver) -> {
            WebElement element = (WebElement) executeScriptReturningObject(script);
            return (element != null) ? element : null;
        });
    }

    public WebElement executeJavaScriptReturningElement(WebElement parent, String script) {
        FluentWait<WebDriver> wait = new WebDriverWait(driver, Timeout.SHORT.inSeconds());
        wait.withMessage("Element was not found visible.");
        return wait.until((WebDriver webDriver) -> {
            WebElement element = (WebElement) executeScriptReturningObject(parent, script);
            return (element != null) ? element : null;
        });
    }

    public void blur(WebElement element) {
        String script = "arguments[0].blur();";
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(script, element);
    }

    @SuppressWarnings("unchecked")
    public List<Object> executeScriptReturningObjects(String script) {
        return (List<Object>) executeScriptReturningObject(script);
    }

    public void executeScript(String script) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(script);
    }

    public void executeScript(WebElement element, String script) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(script, element);
    }





    public void switchToIframe(WebElement frame) {
        driver.switchTo().frame(frame);
    }

    public void switchToIframeBasedOnId(String id) {
        driver.switchTo().frame(id);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }



    public void scrollIntoView(WebElement element) {
        String script = "arguments[0].scrollIntoView(false)";
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(script, element);
    }

    public void scrollToTopOfView(WebElement element) {
        String script = "arguments[0].scrollIntoView(true)";
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(script, element);
    }



    public WebElement findVisibleElementAcrossContexts(WebElement locator) {
        if (listOfElementIsNotEmpty(locator))
            return findVisibleElement(locator);
        return findElementAcrossContexts(locator);
    }

    public WebElement findElementAcrossContexts(WebElement locator) {
        return null;
    }



    public void waitUntilLoadingIconDisappears(Timeout timeout) {
        final String errorMessage = "Loader Icon is still going on";
        WebDriverWait wait = new WebDriverWait(driver, timeout.inSeconds());
        wait.withMessage(errorMessage).until(untilLoaderIconDisappears());
    }

    private ExpectedCondition<Boolean> untilLoaderIconDisappears() {
        return WebDriver -> {
            WebElement loader = WebDriver.findElement(By.xpath("//section[@class='rhp ui-will-load']//div[contains(@class,'left-pane-items')]"));
            String loadAttribute = loader.getAttribute("class");
            return !loadAttribute.contains("loading");
        };
    }


    public Boolean clickElementIfPresent(WebElement locator) {
        try {
            List<WebElement> elements = findVisibleElements(locator);
            if (elements.size() > 0) {
                elements.get(0).click();
                return true;
            }
        } catch (WebDriverException w) {
            //do nothing.
        }
        return false;
    }

    public void waitUntilPageLoad(int timeout) {
        new WebDriverWait(driver, timeout).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public WebElement getBrowserAddressBar() {
        return null;
    }

    public int getStatusBarHeight() {
        return 0;
    }

    public void forCondition(Function<WebDriver, Boolean> condition, Timeout timeout) {
        new WebDriverWait(driver, timeout.inSeconds())
                .withMessage("Wait condition not met")
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class)
                .until(condition);
    }


    public void forExpectedCondition(ExpectedCondition expectedCondition) {
        new WebDriverWait(driver, Timeout.AVERAGE.inSeconds())
                .until(expectedCondition);
    }

    public void forCondition(Function<WebDriver, Boolean> condition) {
        forCondition(condition, Timeout.VERY_LONG);
    }


    public void explicitlyFor(int secondsToWait) {
        try {
            Thread.sleep(secondsToWait * 1000);
        } catch (InterruptedException ignored) {}
    }

    public void explicitlyForMillisec(long milliSecondsToWait) {
        try {
            Thread.sleep(milliSecondsToWait);
        } catch (InterruptedException ignored) {}
    }


    public WebElement forElementToBePresent(Function<WebDriver, WebElement> condition) throws TimeoutException {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(Timeout.AVERAGE.inSeconds()))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class)
                .until(condition);
    }
}

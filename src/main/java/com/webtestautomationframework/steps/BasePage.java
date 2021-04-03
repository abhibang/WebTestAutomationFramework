package com.webtestautomationframework.steps;

import com.webtestautomationframework.actions.WebDriverActions;
import com.webtestautomationframework.utils.core.DriverFactory;

public class BasePage {
    protected WebDriverActions actions;

    public BasePage() {
        actions = DriverFactory.getActions();
    }
}

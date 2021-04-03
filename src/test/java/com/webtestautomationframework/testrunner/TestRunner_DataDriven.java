package com.webtestautomationframework.testrunner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/scenarios/Assignment1.feature"},
		format = {"pretty", "html:target/Reports/Assignment1", "json:target/Assignment1.json"},
		monochrome = true,
		glue = {"com/webtestautomationframework"}
)

public class TestRunner_DataDriven {
}

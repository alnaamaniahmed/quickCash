package com.example.quickcash;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
@RunWith(AndroidJUnit4.class)
public class UIAutomatorTests {
    private static final int LAUNCH_TIMEOUT = 5000;
    final String launcherPackage = "com.example.quickcash";
    private UiDevice device = null;

    @Before
    public void setup() {
        device = UiDevice.getInstance(getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        final Intent appIntent = context.getPackageManager().getLaunchIntentForPackage(launcherPackage);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(appIntent);
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }
    @After
    public void tearDown() {
        device = null;
    }

    @Test
    public void checkIfLandingPageIsVisible() throws UiObjectNotFoundException {
        UiObject roleSpinner = device.findObject(new UiSelector().textContains("Select your role"));
        assertTrue("Spinner is not visible.",roleSpinner.exists());
        UiObject emailInputBox = device.findObject(new UiSelector().textContains("Email"));
        assertTrue("Email box is not visible.",emailInputBox.exists());
        UiObject passwordInputBox = device.findObject(new UiSelector().textContains("Password"));
        assertTrue("Password box is not visible.",passwordInputBox.exists());
        UiObject signInButton = device.findObject(new UiSelector().text("SIGN IN"));
        assertTrue("Sign In button is not visible.",signInButton.exists());
        UiObject forgotPasswordLink = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/forgotPassword"));
        assertTrue("Forgot Password link not visible.", forgotPasswordLink.exists());
        assertTrue("Forgot Password link is not clickable.", forgotPasswordLink.isClickable());
        UiObject signUpLink = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/signUp"));
        assertTrue("Sign Up link not visible.", signUpLink.exists());
        assertTrue("Sign Up link is not clickable.", signUpLink.isClickable());
    }
    @Test
    public void checkIfMoved2RegistrationPage() throws UiObjectNotFoundException {
        UiObject signUpLink = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/signUp"));
        signUpLink.clickAndWaitForNewWindow();
        UiObject registrationTitle = device.findObject(new UiSelector().textContains("Register"));
        assertTrue("Register title is not visible on Registration Page", registrationTitle.exists());
    }

    /**
     * URGENT: This line was used to redirect to the password Reset page so change the UiObject to an actual UI element in the PasswordReset Page
     * UiObject resetInstructionText = device.findObject(new UiSelector().textContains("Reset your password"));
     */
    @Test
    public void checkIfMoved2PasswordResetPage() throws UiObjectNotFoundException {
        UiObject forgotPasswordLink = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/forgotPassword"));
        forgotPasswordLink.clickAndWaitForNewWindow();
        UiObject resetInstructionText = device.findObject(new UiSelector().textContains("Reset your password"));
        assertTrue("Password reset instruction text is not visible", resetInstructionText.exists());
    }
    @Test
    public void signInAsEmployee() throws UiObjectNotFoundException {
        UiObject roleSpinner = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/roleSpinner"));
        roleSpinner.click();
        UiObject employeeOption = device.findObject(new UiSelector().text("Employee"));
        employeeOption.click();
        UiObject emailInput = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/emailInput"));
        emailInput.setText("employee@dal.ca");
        UiObject passwordInput = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/passwordInput"));
        passwordInput.setText("employee123");
        UiObject signInButton = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/signInButton"));
        signInButton.click();

        UiObject employeeDashboardGreeting = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/imageButton"));
        assertTrue("Employee page is not visible", employeeDashboardGreeting.waitForExists(LAUNCH_TIMEOUT));

        // Open the toolbar menu
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.pressMenu(); // Attempt to open the overflow menu directly

        // Wait a bit for the menu to appear
        device.waitForIdle(); // You might need to adjust waiting strategy based on the app's behavior

        // Click the logout item by text or ID
        UiObject logoutItem = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/menuButton"));
        if (!logoutItem.exists()) {
            // If not found by ID, try finding by text
            logoutItem = device.findObject(new UiSelector().text("Logout"));
        }

        if(logoutItem.exists()) {
            logoutItem.click();
        } else {
            throw new UiObjectNotFoundException("Logout item not found.");
        }
    }

    @Test
    public void signInAsEmployer() throws UiObjectNotFoundException {
        UiObject roleSpinner = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/roleSpinner"));
        roleSpinner.click();
        UiObject employerOption = device.findObject(new UiSelector().text("Employer"));
        employerOption.click();
        UiObject emailInput = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/emailInput"));
        emailInput.setText("employer@dal.ca");

        UiObject passwordInput = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/passwordInput"));
        passwordInput.setText("employer123");

        UiObject signInButton = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/signInButton"));
        signInButton.click();

        UiObject employerDashboardGreeting = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/welcomeTextView"));
        assertTrue("Employer page is not visible", employerDashboardGreeting.waitForExists(LAUNCH_TIMEOUT));
        // Open the toolbar menu
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.pressMenu(); // Attempt to open the overflow menu directly

        // Wait a bit for the menu to appear
        device.waitForIdle(); // You might need to adjust waiting strategy based on the app's behavior

        // Click the logout item by text or ID
        UiObject logoutItem = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/menuButton"));
        if (!logoutItem.exists()) {
            // If not found by ID, try finding by text
            logoutItem = device.findObject(new UiSelector().text("Logout"));
        }

        if(logoutItem.exists()) {
            logoutItem.click();
        } else {
            throw new UiObjectNotFoundException("Logout item not found.");
        }
    }

    @Test
    public void checkLogOut() throws UiObjectNotFoundException{
        UiObject roleSpinner = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/roleSpinner"));
        roleSpinner.click();
        UiObject employerOption = device.findObject(new UiSelector().text("Employer"));
        employerOption.click();
        UiObject emailInput = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/emailInput"));
        emailInput.setText("employer@test.com");

        UiObject passwordInput = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/passwordInput"));
        passwordInput.setText("employer123");

        UiObject signInButton = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/signInButton"));
        signInButton.click();

        UiObject welcome = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/welcomeTextView"));
        assertTrue("Login page is not visible", welcome.waitForExists(LAUNCH_TIMEOUT));

        // Open the toolbar menu
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.pressMenu(); // Attempt to open the overflow menu directly

        // Wait a bit for the menu to appear
        device.waitForIdle(); // You might need to adjust waiting strategy based on the app's behavior

        // Click the logout item by text or ID
        UiObject logoutItem = device.findObject(new UiSelector().resourceId(launcherPackage + ":id/menuButton"));
        if (!logoutItem.exists()) {
            // If not found by ID, try finding by text
            logoutItem = device.findObject(new UiSelector().text("Logout"));
        }

        if(logoutItem.exists()) {
            logoutItem.click();
        } else {
            throw new UiObjectNotFoundException("Logout item not found.");
        }
    }
}
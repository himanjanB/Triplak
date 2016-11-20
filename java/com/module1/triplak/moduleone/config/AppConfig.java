package com.module1.triplak.moduleone.config;

/**
 * Created by Himanjan on 28-10-2015.
 */
public class AppConfig {

    //Server URL Request SMS and Verify SMS configuration
    public static final String URL_REQUEST_SMS = "http://192.168.0.50/triplak/request_sms.php";
    public static final String URL_VERIFY_OTP = "http://192.168.0.50/triplak/verify_otp.php";
    public static final String URL_REQUEST_SMS_TEST = "http://192.168.0.50/triplak/testRequest_sms.php";
    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "TRIPLK";
    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
    //Authentication URL
    public static String AUTHENTICATION_URL = "http://192.168.0.50/triplak/authenticateCode.php";
    //Server user login url
    public static String URL_LOGIN = "http://192.168.0.50/triplak/login.php";
    //Server user register url
    public static String URL_REGISTER = "http://192.168.0.50/triplak/register.php";
    //Login Verification URL
    public static String LOGIN_OTP_VERIFY_URL = "http://192.168.0.50/triplak/loginOTPVerify.php";
    //Get updated money URL
    public static String UPDATE_MONEY_URL = "http://192.168.0.50/triplak/updateMoney.php";
    //Privacy policy URL
    public static String PRIVACY_POLICY_URL = "http://192.168.0.50/triplak/privacyPolicy.html";
    //Country names and phone code URL
    public static String COUNTRY_PHONE_CODES = "http://192.168.0.50/triplak/countryCodes.txt";
    //FAQ Details URL
    public static String FAQ_DETAILS = "http://borahimanjan.wix.com/triplak";
    //Friends Details Update URL
    public static String URL_UPDATE_FRIENDS_DETAILS = "http://192.168.0.50/triplak/updateFriendsDetails.php";
}

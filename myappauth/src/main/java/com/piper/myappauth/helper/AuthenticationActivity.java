package com.piper.myappauth.helper;

/**
 * Created by developers on 16/01/18.
 */

public interface AuthenticationActivity {

    /**
     * Method to response for successful authentication
     */
    void success();

    /**
     * Method to response for failure authentication
     */
    void failed();

    /**
     * Method to response for error
     */
    void error();

    /**
     * Method to know if user is singned in
     */
    void isUserAuthenticated(boolean isAuthenticated);
}

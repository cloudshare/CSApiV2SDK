package com.cloudshare.apitests;

import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cloudshare.api.CSAPIHighLevel;
import com.cloudshare.api.CSAPILowLevel;
import com.cloudshare.api.CSAPILowLevel.ApiException;
import com.cloudshare.api.DTOs.EnvStatus;

public class SanityTests extends TestCase {
    public static final String GOOD_CREDENTIALS_FILENAME = "credentials.txt";
    public static final String BAD_CREDENTIALS_FILENAME = "bad_credentials.txt";

    public void setUp() {
    }

    public void tearDown() {
    }

    public void testCheckKeys() throws IOException {
        // good credentials
        Credentials goodCredentials = new Credentials(GOOD_CREDENTIALS_FILENAME);

        CSAPILowLevel api = new CSAPILowLevel(goodCredentials.getId(),
                                              goodCredentials.getKey());

        try {
            assertTrue(api.CheckKeys());
        } catch (ApiException e) {
            fail("We shouldn't get ApiException: " + e.getMessage());
        }

        // bad credentials
        Credentials badCredentials = new Credentials(BAD_CREDENTIALS_FILENAME);

        api = new CSAPILowLevel(badCredentials.getId(), badCredentials.getKey());
        try {
            api.CheckKeys();
            fail("CheckKeys should throw ApiException");
        } catch (ApiException e) {
            // we should get here
        }
    }

    public void testHighLevel() throws IOException {
        Credentials credentials = new Credentials(GOOD_CREDENTIALS_FILENAME);
        CSAPIHighLevel api = new CSAPIHighLevel(credentials.getId(),
                                                credentials.getKey());

        try {
            List<EnvStatus> r = api.getEnvironmentStatusList();
            for (EnvStatus es : r) {
                System.out.println(es.toPrettyString());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(SanityTests.class);

        return suite;
    }
}

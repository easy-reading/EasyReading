package nu.info.zeeshan.rnf;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(ApplicationTestSuite.class).includeAllPackagesUnderHere()
                .build();
    }

    public ApplicationTestSuite() {
        super();
    }
}
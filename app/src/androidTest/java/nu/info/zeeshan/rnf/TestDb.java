package nu.info.zeeshan.rnf;

import android.test.AndroidTestCase;

import nu.info.zeeshan.rnf.data.FeedsDbHelper;

/**
 * Created by Zeeshan Khan on 5/22/2016.
 */
public class TestDb extends AndroidTestCase {
    void deleteTheDatabase() {
        mContext.deleteDatabase(FeedsDbHelper.DATABASE_NAME);
    }

    @Override
    public void setUp() {
        deleteTheDatabase();
    }
}

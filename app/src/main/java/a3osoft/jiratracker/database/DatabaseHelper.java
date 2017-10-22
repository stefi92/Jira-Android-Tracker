package a3osoft.jiratracker.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.List;

import a3osoft.jiratracker.BuildConfig;
import a3osoft.jiratracker.jira.Issue;
import a3osoft.jiratracker.jira.JiraAuthToken;
import a3osoft.jiratracker.jira.Worklog;
import a3osoft.jiratracker.validations.Validation;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "jira_tracking_app.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 12;

    private static DatabaseHelper mInstance = null;

    // the DAO object we use to access the SimpleData table
    private Dao<Issue, String> issueDao = null;
    private Dao<Worklog, Integer> worklogDao = null;
    private Dao<Validation, Integer> validationDao = null;

    private Context mCxt;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCxt = context;
    }

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            if(BuildConfig.DEBUG) Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, Issue.class);
            TableUtils.createTableIfNotExists(connectionSource, Worklog.class);
            TableUtils.createTableIfNotExists(connectionSource, JiraAuthToken.class);
            TableUtils.createTableIfNotExists(connectionSource, Validation.class);
        } catch (SQLException e) {
            if(BuildConfig.DEBUG) Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            if(BuildConfig.DEBUG) Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Issue.class, true);
            TableUtils.dropTable(connectionSource, Worklog.class, true);
            TableUtils.dropTable(connectionSource, JiraAuthToken.class, true);
            TableUtils.dropTable(connectionSource, Validation.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            if(BuildConfig.DEBUG) Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearAllData() throws java.sql.SQLException {
        TableUtils.clearTable(getConnectionSource(), Issue.class);
        TableUtils.clearTable(getConnectionSource(), Worklog.class);
        TableUtils.clearTable(getConnectionSource(), JiraAuthToken.class);
        TableUtils.clearTable(getConnectionSource(), Validation.class);
    }

    public Dao<Issue, String> getIssueDao() throws SQLException, java.sql.SQLException {
        if (issueDao == null) {
            issueDao = getDao(Issue.class);
        }
        return issueDao;
    }

    public Dao<Worklog, Integer> getWorklogDao() throws SQLException, java.sql.SQLException {
        if (worklogDao == null) {
            worklogDao = getDao(Worklog.class);
        }
        return worklogDao;
    }

    public Dao<Validation, Integer> getValidationDao() throws SQLException, java.sql.SQLException {
        if (validationDao == null) {
            validationDao = getDao(Validation.class);
        }
        return validationDao;
    }

    public void setAuthToken(String token){
        try {
            getDao(JiraAuthToken.class).create(new JiraAuthToken(token));
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAuthToken(){
        try {
            List<JiraAuthToken> tokens = getDao(JiraAuthToken.class).queryForAll();
            if(tokens.isEmpty()){
                return "";
            }
            return tokens.get(0).getToken();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        issueDao = null;
        worklogDao = null;
        validationDao = null;
    }
}

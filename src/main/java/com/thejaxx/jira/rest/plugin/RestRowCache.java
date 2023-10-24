package com.thejaxx.jira.rest.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RestRowCache {

    private static final Logger logger = LoggerFactory.getLogger(RestRowCache.class);
    /**
     * If the configuration does not depend on project keys, then we use this
     * key in the cache
     */
    private static final String DEFAULT_PROJECT_KEY = "InternalDefaultProjectKey";
    private static final int KEEP_ALL_PROJECTS_IN_CACHE = -1;
    private Map<String, Map<String, RestRow>> m_cache = new HashMap<String, Map<String, RestRow>>();
    private Map<String, Long> m_lastCacheFillTimes = new HashMap<String, Long>();

    public RestRowCache() {
    }

    public Map<String, RestRow> getProjectSpecificCacheMap(String projectKey) {
        if (!isDependentOnProjectKey()) {
            projectKey = DEFAULT_PROJECT_KEY;
        }
        return m_cache.get(projectKey);
    }

    public void addCacheEntry(String projectKey, String id, RestRow row) {
        if (!isDependentOnProjectKey()) {
            projectKey = DEFAULT_PROJECT_KEY;
        }
        Map<String, RestRow> projectCache = getProjectSpecificCacheMap(projectKey);
        if (projectCache == null) {
            projectCache = new HashMap<String, RestRow>();
            m_cache.put(projectKey, projectCache);
        }
        projectCache.put(id, row);
    }

//	public Collection<RestRow> getDatabaseRows(String projectKey, DatabaseRowCachePurpose purpose)
//	{
//
//		Collection<RestRow> collection = 			collection = getDatabaseRowsFromDb(projectKey, m_parameters.getSqlQuerySearch());
//
//		if (purpose == DatabaseRowCachePurpose.EDIT
//				|| (purpose == DatabaseRowCachePurpose.SEARCH ))
//		{
//			refreshCacheIfNeeded(projectKey);
//
//			collection = getProjectSpecificCacheMap(projectKey).values();
//		}
//		else if (purpose == DatabaseRowCachePurpose.SEARCH
//				&& m_parameters.getSqlQuerySearch() != null)
//		{
//			collection = getDatabaseRowsFromDb(projectKey, m_parameters.getSqlQuerySearch());
//		}
//		else
//		{
//			throw new IllegalArgumentException("Unsupported purpose!");
//		}
//		// Make sure the returned list of database rows is ordered like the user wanted (specified by using ORDER BY)
//		List<RestRow> list = new ArrayList<RestRow>(collection.size());
//		list.addAll(collection);
//		Collections.sort(list);
//
//		return list;
//	}

    /**
     * @param projectKey the JIRA project key. Can be null.

    public void refreshCacheIfNeeded(String projectKey) {
    //        if (!isDependentOnProjectKey()) {
    projectKey = DEFAULT_PROJECT_KEY;
    //        }

    if (System.currentTimeMillis() - getLastCacheFillTime(projectKey) > m_parameters.getCacheTimeout()) {
    Map<String, RestRow> projectCache = getProjectSpecificCacheMap(projectKey);
    if (projectCache == null) {
    logger.debug("No project cache for " + projectKey);
    // First check if some project must be removed from the cache
    if (m_parameters.getNumberOfProjectsInCache() != KEEP_ALL_PROJECTS_IN_CACHE) {
    if (m_cache.size() == m_parameters.getNumberOfProjectsInCache()) {
    String oldCacheProjectKey = getOldestCacheProjectKey();
    if (oldCacheProjectKey != null) {
    logger.debug("Removing " + oldCacheProjectKey + " project cache");
    m_cache.remove(oldCacheProjectKey);
    m_lastCacheFillTimes.remove(oldCacheProjectKey);
    } else {
    logger.error("Could not remove a project map from the cache, although there are already " + m_cache.size() + " projects in the cache (max projects=" + m_parameters.getNumberOfProjectsInCache() + ")");
    }
    }
    }

    projectCache = new HashMap<String, RestRow>();

    // Add the new projects map to the cache
    m_cache.put(projectKey, projectCache);
    } else {
    projectCache.clear();
    }

    List<RestRow> restRows = restQuery(projectKey);
    for (RestRow restRow : restRows) {
    Object key = restRow.getValue();
    if (key != null) {
    projectCache.put(key.toString(), restRow);
    } else {
    logger.error("refreshCacheIfNeeded :: databaseRow = " + restRow);
    }
    }

    m_lastCacheFillTimes.put(projectKey, System.currentTimeMillis());
    }
    }*/

    /**
     * Method for getting a certain record from the database directly (without using the cache).
     *
     * @param id primary key of the record
     * @return the record
     */
    public RestRow getDatabaseRowFromDatabase(String id) {
        return new RestRow(id, id);
//		Connection c = null;
//		Statement s = null;
//		try
//		{
//			c = createConnection();
//
//			s = c.createStatement();
//
//			String sqlQuery = m_parameters.doSqlSubstitutions();
//			if (isDependentOnProjectKey())
//			{
//				if (sqlQuery.indexOf("like") == -1)
//				{
//					logger.error("You need to use like in your WHERE clause to use ${jira.project.key}");
//				}
//				sqlQuery = sqlQuery.replaceAll("\\$\\{jira.project.key\\}", "%");
//				sqlQuery += " AND " + m_parameters.getPrimaryKeyColumnName() + "=" + id;
//				logger.error(sqlQuery);
//			}
//			else
//			{
//				throw new IllegalArgumentException("This function should only be used when depending on a project key!");
//			}
//
//			sqlQuery = replaceJiraUserIfPresent(sqlQuery);
//
//			ResultSet rs = s.executeQuery(sqlQuery);
//			ResultSetMetaData data = rs.getMetaData();
//
//			RestRow result;
//			if (rs.next())
//			{
//				result = new RestRow();
//				for (int i = 1; i < data.getColumnCount() + 1; i++)
//				{
//					// Work 0-based in the cache
//					result.addDatabaseColumn(i - 1, rs.getObject(i));
//				}
//			}
//			else
//			{
//				throw new IllegalArgumentException("Could not find entry with id " + id + " in the database. Query: " + sqlQuery);
//			}
//
//			return result;
//		}
//		catch (SQLException e)
//		{
//			logger.error(e.getMessage(), e);
//			return null;
//		}
//		catch (GenericEntityException e)
//		{
//			logger.error(e.getMessage(), e);
//			return null;
//		}
//		finally
//		{
//			closeStatement(s);
//			closeConnection(c);
//		}
    }

//    public Collection<RestRow> getAllDatabaseRows(DatabaseRowCachePurpose purpose) {
//        Collection<RestRow> result;
//        if (!isDependentOnProjectKey()) {
//            result = getDatabaseRows(DEFAULT_PROJECT_KEY, purpose);
//        } else {
//            result = getAllDatabaseRowsForProjectDependConfig();
//        }
//        return result;
//    }
//
//    private Collection<RestRow> getAllDatabaseRowsForProjectDependConfig() {
//        // Go directly to the database, we don't want to fill up our cache with the complete database!
//        return getDatabaseRowsFromDb(null);
//    }

    /**
     * Returns the database rows that match the project key. If it is null, then
     * all rows are returned.
     *
     * @return the matching rows
     */
//    private List<RestRow> getDatabaseRowsFromDb(String projectKey) {
//        return getDatabaseRowsFromDb(projectKey, m_parameters.doSqlSubstitutions());
//    }
//
//    private List<RestRow> getDatabaseRowsFromDb(String projectKey, String sqlQuery) {
//        return restQuery();
//    }
//    public List<RestRow> restQuery(String projectKey) {
//        LoggerUtils.main(LogLevel.INFO, "restQuery for project  " + projectKey);
//
//
//        List<RestRow> result = new LinkedList<RestRow>();
//
//        String url = m_parameters.getUrl();
//
//        try {
//
//            JSONArray array = new JSONArray(HttpRequest.get(url).body());
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject jsonObject = (JSONObject) array.get(i);
//                result.add(new RestRow(jsonObject.getString("code"), jsonObject.getString("name")));
//            }
//
//        } catch (Throwable e) {
//            LoggerUtils.main(LogLevel.ERROR, "URL string " + url + ", exception :: " + e.getMessage());
//        }
//
//
////        for (int i = 0; i < 10; i++) {
////            result.add(new RestRow(i + "", i + " what a row!"));
////        }
////        Connection c = null;
////        try {
////            c = createConnection();
////
////            Statement s = c.createStatement();
////
////
////            logger.debug("SQL query: " + sqlQuery);
////            ResultSet rs = s.executeQuery(sqlQuery);
////            ResultSetMetaData data = rs.getMetaData();
////
////            long rowNumber = 0;
////            while (rs.next()) {
////                RestRow row = new RestRow();
////                row.setRowNumber(rowNumber);
////                for (int i = 1; i < data.getColumnCount() + 1; i++) {
////                    // Work 0-based in the cache
////                    row.addDatabaseColumn(i - 1, rs.getObject(i));
////                }
////
////                result.add(row);
////                rowNumber++;
////            }
//        LoggerUtils.main(LogLevel.INFO, String.format("Rest returned %s rows", result.size()));
//        List<RestRow> firstFiveMax = result.subList(0, Math.min(result.size(), 5));
//        LoggerUtils.main(LogLevel.INFO, "Showing first " + firstFiveMax.size() + " records for debugging:");
//        for (RestRow restRow : firstFiveMax) {
//            logger.debug( restRow.toString());
//        }
//        return result;
//    }

//    private String replaceProjectKeyIfPresent(String projectKey, String sqlQuery) {
//        if (sqlQuery.indexOf("${jira.project.key}") != -1) {
//            if (projectKey != null) {
//                sqlQuery = sqlQuery.replaceAll("\\$\\{jira.project.key\\}", projectKey);
//            } else {
//                if (sqlQuery.indexOf("like") == -1) {
//                    logger.error("You need to use like in your WHERE clause to use ${jira.project.key}");
//                }
//                sqlQuery = sqlQuery.replaceAll("\\$\\{jira.project.key\\}", "%");
//            }
//        }
//        return sqlQuery;
//    }
    private String replaceJiraUserIfPresent(String sqlQuery) {
        if (sqlQuery.indexOf("${jira.user}") != -1) {
            ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            String userName = user.getName();
            logger.debug("Replacing ${jira.user} with " + userName);
            sqlQuery = sqlQuery.replaceAll("\\$\\{jira.user\\}", userName);
        }
        return sqlQuery;
    }

    private String getOldestCacheProjectKey() {
        long oldestTime = Long.MAX_VALUE;
        String result = null;
        for (String projectKey : m_lastCacheFillTimes.keySet()) {
            Long fillTime = m_lastCacheFillTimes.get(projectKey);
            if (fillTime.longValue() < oldestTime) {
                result = projectKey;
                oldestTime = fillTime.longValue();
            }
        }
        return result;
    }

    private long getLastCacheFillTime(String projectKey) {
        long result;
        Long fillTime = m_lastCacheFillTimes.get(projectKey);
        if (fillTime != null) {
            result = fillTime.longValue();
        } else {
            result = 0;
        }
        return result;
    }

	private boolean isDependentOnProjectKey()
	{
		return false;
	}

//    private Connection createConnection()
//            throws SQLException, GenericEntityException {
//        if (m_parameters.isUseInternalJiraDatabase()) {
//            DelegatorInterface delegator = ComponentAccessor.getComponent(DelegatorInterface.class);
//            String helperName = delegator.getGroupHelperName("default");
//            logger.debug("Connecting with internal jira db registered as " + helperName);
//
//            return ConnectionFactory.getConnection(helperName);
//        }
//
//        return DriverManager.getConnection(m_parameters.getDatabaseConnectionUrl(),
//                m_parameters.getDatabaseUser(),
//                m_parameters.getDatabasePassword());
//    }

//    void addDemoData() {
//        Connection c = null;
//        try {
//            c = createConnection();
//
//            Statement s = c.createStatement();
//
//            s.executeUpdate("DROP TABLE customer IF EXISTS");
//            s.executeUpdate("CREATE TABLE customer ( id INTEGER IDENTITY, firstname VARCHAR(256), lastname VARCHAR(256), city VARCHAR(256), country VARCHAR(256), projectkey VARCHAR(256) )");
//            s.executeUpdate("INSERT INTO customer VALUES( 1, 'Wim', 'Deblauwe', 'Heule', 'Belgium', 'TST' )");
//            s.executeUpdate("INSERT INTO customer VALUES( 2, 'Victor', 'Deblauwe', 'Heule', 'Belgium', 'TST' )");
//            s.executeUpdate("INSERT INTO customer VALUES( 3, 'Jules', 'Deblauwe', 'Heule', 'Belgium', 'PJT' )");
//            s.executeUpdate("INSERT INTO customer VALUES( 4, 'John', 'Atlassian', 'Sidney', 'Australia', 'PJT')");
//            s.executeUpdate("INSERT INTO customer VALUES( 5, 'Steve', 'Urkel', 'Copenhagen', 'Denmark', 'TST')");
//            s.executeUpdate("INSERT INTO customer VALUES( 6, 'Tim', 'O''hara', 'Brussel', 'Belgium', 'PJT')");
//            s.executeUpdate("INSERT INTO customer VALUES( 7, 'Ervin', '\"Magic\" Johnson', 'Sidney', 'Australia', 'PJT')");
//            s.executeUpdate("INSERT INTO customer VALUES( 8, 'admin', 'Last', 'Brisbane', 'Australia', 'PJT')");
//        } catch (SQLException e) {
//            logger.error(e.getMessage(), e);
//        } catch (GenericEntityException e) {
//            logger.error(e.getMessage(), e);
//        } finally {
//            closeConnection(c);
//        }
//    }


//    private void closeConnection(Connection c) {
//        if (c != null) {
//            try {
//                c.close();
//            } catch (SQLException e) {
//                logger.error(e.getMessage(), e);
//            }
//        }
//    }
//
//    private void closeStatement(Statement s) {
//        if (s != null) {
//            try {
//                s.close();
//            } catch (SQLException e) {
//                logger.error(e.getMessage(), e);
//            }
//        }
//    }
}

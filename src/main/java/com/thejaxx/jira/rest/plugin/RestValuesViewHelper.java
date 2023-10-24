package com.thejaxx.jira.rest.plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thejaxx.jira.rest.plugin.ajax.AjaxViewHelper;

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.util.I18nHelper;
import com.thejaxx.jira.rest.plugin.config.ConfigUtils;
import org.apache.log4j.Logger;

public class RestValuesViewHelper {
    // ------------------------------ FIELDS ------------------------------

    private static final String DATABASE_VALUES_VIEW_HELPER = "databaseValuesViewHelper";
    private static final String EOL = System.getProperty("line.separator");
    private static final int KEEP_ALL_PROJECTS_IN_CACHE = -1;

    private static final Logger logger = Logger.getLogger(RestValuesViewHelper.class);
    /**
     * If the configuration does not depend on project keys, then we use this
     * key in the cache
     */
    private static final String DEFAULT_PROJECT_KEY = "InternalDefaultProjectKey";

    private RestRowCache rowCache = new RestRowCache();
    private AjaxViewHelper viewHelper;
//    private ActiveObjects ao;

    public RestValuesViewHelper() {
//        this.ao = ao;
        viewHelper = new AjaxViewHelper();
    }

    public void reload() {

    }
// --------------------------- CONSTRUCTORS ---------------------------


// -------------------------- PUBLIC METHODS --------------------------

    public void updateCache(RestRowCache cache){
        rowCache = cache;
    }

    //	/**
//	 * Returns the HTML that should be used for viewing the value of the custom field.
//	 *
//	 * @param id		 the primary key of the data (as determined by {@link DatabaseValuesCFParameters#getPrimaryKeyColumnNumber()})
//	 * @param projectKey the JIRA key of the current project. Can be null.
//	 * @param i18nHelper helper for internationalization
//	 * @return a HTML snippet
//	 */
    public String getHtmlForView(String id, String projectKey, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(projectKey);
        System.out.println("id = " + id);
        logger.debug("getHtmlForView . ID " + id + ", Project Key " + projectKey);
        RestRow restRow = rowCache.getProjectSpecificCacheMap(projectKey).get(id);
        String result;
        if (restRow != null) {
            result = RestRowFormatter.formatRestRow(restRow, ConfigUtils.get().getRenderingEditPattern(), i18nHelper);
        } else {
            result = "Unknown id: " + id;
        }
        return result;
    }

    //
//    /**
//     * Returns the text to be used in the 'history' tab and the activity stream.
//     *
//     * @param id         the value in the database (primary key of the row most of the time)
//     * @param i18nHelper the jira i18n helper class
//     * @return the text to show to the user
//     */
//    public String getTextForChangeLog(String id, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(null);
//
//        RestRow restRow = rowCache.getProjectSpecificCacheMap(null).get(id);
//        String result;
//        if (restRow != null) {
//            result = RestRowFormatter.formatRestRow(restRow, entity.get("changeLogViewPattern"), i18nHelper);
//        } else {
//            result = "Unknown id: " + id;
//        }
//        return result;
//    }
//
//    /**
//     * Returns the text to be used in the 'statistics (pie chart, 2d statistics, single level group by report)
//     *
//     * @param id         the value in the database (primary key of the row most of the time)
//     * @param i18nHelper the jira i18n helper class
//     * @return the text to show to the user
//     */
    public String getTextForStatistics(String id, I18nHelper i18nHelper) {

        RestRow restRow = rowCache.getProjectSpecificCacheMap(null).get(id);
        String result;
        if (restRow != null) {
            result = RestRowFormatter.formatRestRow(restRow, ConfigUtils.get().getStatisticsViewPattern(), i18nHelper);
        } else {
            result = "Unknown id: " + id;
        }
        return result;
    }

    //
//    /**
//     * Returns if the user wants to edit the values using AJAX style or using a combobox
//     *
//     * @return true if AJAX style editing should be used
//     */
//    public boolean useAjaxForEdit() {
//        return true;//m_parameters.getEditType() == DatabaseValuesCFParameters.EDIT_TYPE_AJAX_INPUT;
//    }
//
    public boolean useAjaxForSearch() {
        return true;//m_parameters.getSearchType() == DatabaseValuesCFParameters.SEARCH_TYPE_AJAX_INPUT;
    }

    //
//    public boolean useCascadingSelectForEdit() {
//        return m_parameters.getEditType() == RestValuesCFParameters.EDIT_TYPE_CASCADING_SELECT;
//    }
//
//
//    public String getAjaxHtmlForEdit(String customFieldId, String id, String baseurl, String projectKey, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(projectKey);
//
//        return m_ajaxViewHelper.getAjaxHtmlForEdit(customFieldId, id, baseurl, projectKey, i18nHelper);
//    }
//
//    private void refreshCacheIfNeeded(String projectKey) {
//        rowCache.refreshCacheIfNeeded(projectKey);
//    }
//
//    /**
//     * Returns the html that will be used by the ajax auto-completer from scriptacolous.
//     *
//     * @param query      the string the user already typed. Can be null.
//     * @param projectKey the JIRA key of the current project. Can be null.
//     * @param i18nHelper helper for internationalization
//     * @param purpose    one of {@link AjaxViewResultsPurpose}
//     * @return HTML snippet
//     */
//    public String getHtmlForAjaxResults(String query, String projectKey, I18nHelper i18nHelper, AjaxViewResultsPurpose purpose) {
//        return m_ajaxViewHelper.getHtmlForAjaxResults(query, projectKey, i18nHelper, purpose);
//    }
//
//    /**
//     * Returns the HTML that should be used for editing the value of the custom field.
//     *
//     * @param customFieldId the id of the custom field
//     * @param projectKey    the JIRA key of the current project. Can be null.
//     * @param i18nHelper    helper for internationalization
//     * @return a HTML snippet
//     */
//    public String getHtmlForEdit(String customFieldId, String id, String projectKey, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(projectKey);
//
//        StringBuffer builder = new StringBuffer("<select name=\"" +
//                customFieldId +
//                "\">");
//        builder.append(EOL);
//        builder.append("<option value=\"\">")
//                .append(i18nHelper.getText("common.words.none"))
//                .append("</option>")
//                .append(EOL);
//        for (RestRow restRow : rowCache.restQuery(projectKey)) {
//            String databaseRowId = restRow.getValue().toString();
//            if (id != null && id.equals(databaseRowId)) {
//                builder.append("<option value=\"")
//                        .append(databaseRowId)
//                        .append("\" selected=\"selected\">")
//                        .append(RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingEditPattern(), i18nHelper))
//                        .append("</option>")
//                        .append(EOL);
//            } else {
//                builder.append("<option value=\"")
//                        .append(databaseRowId)
//                        .append("\">")
//                        .append(RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingEditPattern(), i18nHelper))
//                        .append("</option>")
//                        .append(EOL);
//            }
//        }
//
//        builder.append("</select>");
//
//        return builder.toString();
//    }
//
//    /**
//     * Returns the HTML that should be used for editing the value of the custom field
//     * with 2 combo boxes.
//     *
//     * @param customFieldId the id of the custom field
//     * @param required      indicates if the custom field is required to be filled in
//     * @param projectKey    the JIRA key of the current project. Can be null.
//     * @param i18nHelper    helper for internationalization
//     * @return a HTML snippet
//     */
//    public String getCascadingSelectHtmlForEdit(String customFieldId, String id, boolean required, String projectKey, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(projectKey);
//
//        Map<OrderedString, List<RestRow>> triageMap = createTriageMapForCascadingSelect(projectKey);
//
//        // Child selection combo box
//
//        // Since the child combo box contains the actual value (primary key),
//        // We need to give that one the real custom field id
//        StringBuilder childBuilder = new StringBuilder();
//        childBuilder.append("<select class=\"select cascadingselect-child\" name=\"").append(customFieldId).append("\" id=\"")
//                .append(customFieldId).append("\">");
//        childBuilder.append(EOL);
//        childBuilder.append("<option value=\"\">")
//                .append(i18nHelper.getText("common.words.pleaseselect"))
//                .append("</option>").append(EOL);
//        if (!required) {
//            childBuilder.append("<option class=\"default-option\" value=\"-1\">")
//                    .append(i18nHelper.getText("common.words.none"))
//                    .append("</option>")
//                    .append(EOL);
//        }
//
//        Set<OrderedString> parentValues = triageMap.keySet();
//        int parentCounter = 0;
//        int parentSelection = KEEP_ALL_PROJECTS_IN_CACHE;
//        for (OrderedString parentValue : parentValues) {
//            childBuilder.append("<option class=\"option-group-").append(parentCounter)
//                    .append("\" value=\"\">")
//                    .append(i18nHelper.getText("common.words.pleaseselect"))
//                    .append("</option>").append(EOL);
//
//            List<RestRow> restRows = triageMap.get(parentValue);
//            for (RestRow restRow : restRows) {
//                String databaseRowId = restRow.getValue();
//                if (id != null && id.equals(databaseRowId)) {
//                    parentSelection = parentCounter;
//                    childBuilder.append("<option class=\"option-group-")
//                            .append(parentCounter)
//                            .append("\" ")
//                            .append(" value=\"")
//                            .append(databaseRowId)
//                            .append("\" selected=\"selected\">")
//                            .append(RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingEditPattern(), i18nHelper))
//                            .append("</option>")
//                            .append(EOL);
//                } else {
//                    childBuilder.append("<option class=\"option-group-")
//                            .append(parentCounter)
//                            .append("\" ")
//                            .append(" value=\"")
//                            .append(databaseRowId)
//                            .append("\">")
//                            .append(RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingEditPattern(), i18nHelper))
//                            .append("</option>")
//                            .append(EOL);
//                }
//            }
//
//            parentCounter++;
//        }
//
//        childBuilder.append("</select>");
//
//        // Parent Select Combo Box
//        StringBuilder parentBuilder = getHtmlForParentComboBox(customFieldId, triageMap, parentSelection, required, i18nHelper);
//
//        return parentBuilder.append(childBuilder.toString()).toString();
//    }
//
//    /**
//     * Returns the HTML that should be used for searching on the value of the custom field.
//     *
//     * @param customFieldId  the id of the custom field
//     * @param selectedValues the values that the user already selected for searching
//     * @param projectKey     the JIRA key of the current project. Can be null.
//     * @param i18nHelper     helper r internationalization
//     * @return a HTML snippet
//     */
//    public String getHtmlForSearch(String customFieldId, List selectedValues, String projectKey, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(projectKey);
//
//        StringBuilder builder = new StringBuilder("<select name=\"")
//                .append(customFieldId)
//                .append("\" id=\"")
//                .append(customFieldId)
//                .append("\"  class=\"standardInputField\" multiple=\"multiple\">");
//        builder.append(EOL);
//        builder.append("<option value=\"-1\"");
//        if (selectedValues == null || selectedValues.isEmpty() || selectedValues.contains("-1")) {
//            builder.append(" selected=\"selected\"");
//        }
//        builder.append(">")
//                .append(i18nHelper.getText("common.filters.any"))
//                .append("</option>").append(EOL);
//        for (RestRow restRow : rowCache.restQuery(projectKey)) {
//            String databaseRowId = restRow.getKey();
//            if (selectedValues != null && selectedValues.contains(databaseRowId)) {
//                builder.append("<option value=\"")
//                        .append(databaseRowId)
//                        .append("\" selected=\"selected\">")
//                        .append(RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingSearchPattern(), i18nHelper))
//                        .append("</option>")
//                        .append(EOL);
//            } else {
//                builder.append("<option value=\"")
//                        .append(databaseRowId)
//                        .append("\">")
//                        .append(RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingSearchPattern(), i18nHelper))
//                        .append("</option>")
//                        .append(EOL);
//            }
//        }
//        builder.append("</select>");
//        return builder.toString();
//    }
//
//    /**
//     * Returns the HTML that should be used for AJAX-style editing the value of the custom field.
//     *
//     * @param customFieldId the id of the custom field
//     * @param baseurl       the base url of the JIRA installation
//     * @param projectKey    the JIRA key of the current project. Can be null.
//     * @param i18nHelper    helper for internationalization
//     * @return a HTML snippet
//     */
    public String getAjaxHtmlForSearch(String customFieldId, String id, String baseurl, String projectKey, I18nHelper i18nHelper) {
        return viewHelper.getAjaxHtmlForSearch(customFieldId, id, baseurl, projectKey, i18nHelper);
    }
//
//    /**
//     * Returns the actual string that should be used for sorting the values. This
//     * should match closely with {@link #getHtmlForView(String, String, I18nHelper)}
//     * or your user will not understand what is going on.
//     *
//     * @param id the primary key
//     * @return a simple String
//     */
//    public String getStringForSorting(String id) {
//        String result;
//        if (!isDependentOnProjectKey()) {
//            refreshCacheIfNeeded(DEFAULT_PROJECT_KEY); // do not pass project key for sorting
//        }
//
//        if (id != null) {
//            RestRow restRow;
//            if (isDependentOnProjectKey()) {
//                // If we are dependent on the project key, we will go directly to the database
//                // Otherwise, we would need to have all the projects in our cache (since a search
//                // result can include multiple projects)
//                restRow = rowCache.getDatabaseRowFromDatabase(id);
//            } else {
//                restRow = rowCache.getProjectSpecificCacheMap(DEFAULT_PROJECT_KEY).get(id);
//            }
//            result = RestRowFormatter.formatRestRow(restRow, m_parameters.getSortingViewPattern(), null);
//        } else {
//            result = null;
//        }
//        return result;
//    }
//
//    public String getStringForSearch(String id) {
//        String result;
//        if (!isDependentOnProjectKey()) {
//            refreshCacheIfNeeded(DEFAULT_PROJECT_KEY); // do not pass project key for sorting
//        }
//
//        if (id != null) {
//            RestRow restRow;
//            if (isDependentOnProjectKey()) {
//                // If we are dependent on the project key, we will go directly to the database
//                // Otherwise, we would need to have all the projects in our cache (since a search
//                // result can include multiple projects)
//                restRow = rowCache.getDatabaseRowFromDatabase(id);
//            } else {
//                restRow = rowCache.getProjectSpecificCacheMap(DEFAULT_PROJECT_KEY).get(id);
//            }
//            result = RestRowFormatter.formatRestRow(restRow, m_parameters.getRenderingSearchPattern(), null);
//        } else {
//            result = null;
//        }
//        return result;
//    }
//
//    public void setSqlSubstitutions(Map substitutions) {
//        if (m_parameters != null) {
//            m_parameters.setSqlSubstitutions(substitutions);
//        }
//    }
//
//    /**
//     * Returns true if the given query reference exists for this custom field
//     *
//     * @param queryReference the query reference name
//     * @return true if the reference is configured, false otherwise
//     */
//    public boolean isExistingQueryReference(String queryReference) {
//        return m_parameters.isExistingQueryReference(queryReference);
//    }
//
//    /**
//     * Returns all query references that exist for this custom field
//     *
//     * @return the set of query reference names
//     */
//    public Set<String> getQueryReferences() {
//        return m_parameters.getQueryReferences();
//    }
//
//    public Set<Object> getQueryValues(String queryReference, String queryValue) {
//        Set<Object> result = new HashSet<Object>();
//        String query = m_parameters.getQuery(queryReference, queryValue);
//        if (query != null) {
//            List<RestRow> queryValues = rowCache.getQueryValues(query);
//            for (RestRow value : queryValues) {
//                result.add(value.getValue());
//            }
//        }
//        return result;
//    }
//
//    public Set<Object> getQueryValues(String queryValue) {
//        Set<Object> result = new HashSet<Object>();
//        String query = m_parameters.getQuery("queryReference", queryValue);
//        if (query != null) {
//            List<RestRow> queryValues = rowCache.getQueryValues(query);
//            for (RestRow value : queryValues) {
//                result.add(value.getValue());
//            }
//        }
//        return result;
//    }

    public static RestValuesViewHelper getViewHelper(CustomField customField) {
        Map map = customField.getCustomFieldType().getVelocityParameters(null, customField, null);
        //return (RestValuesViewHelper) map.get(DATABASE_VALUES_VIEW_HELPER);
        return (RestValuesViewHelper) map.get("valuesViewHelper");
    }

// -------------------------- PRIVATE METHODS --------------------------

    private StringBuilder getHtmlForParentComboBox(String customFieldId, Map<OrderedString, List<RestRow>> triageMap, int parentSelection, boolean required, I18nHelper i18nHelper) {
        Set<OrderedString> parentValues;
        int parentCounter;
        StringBuilder parentBuilder = new StringBuilder("<select class=\"select cascadingselect-parent\" name=\"" +
                customFieldId +
                ":1\" id=\"" +
                customFieldId +
                ":1\">");
        parentBuilder.append(EOL);
        parentBuilder.append("<option class=\"default-option\" value=\"\">")
                .append(i18nHelper.getText("common.words.pleaseselect"))
                .append("</option>").append(EOL);
        if (!required) {
            parentBuilder.append("<option class=\"default-option\" value=\"-1\">")
                    .append(i18nHelper.getText("common.words.none"))
                    .append("</option>")
                    .append(EOL);
        }

        parentValues = triageMap.keySet();
        parentCounter = 0;
        for (OrderedString parentValue : parentValues) {
            if (parentCounter == parentSelection) {
                parentBuilder.append("<option class=\"option-group-").append(parentCounter)
                        .append("\" value=\"")
                        .append(parentCounter)
                        .append("\" selected=\"selected\">")
                        .append(parentValue.getString())
                        .append("</option>")
                        .append(EOL);
            } else {
                parentBuilder.append("<option class=\"option-group-").append(parentCounter)
                        .append("\" value=\"").append(parentCounter)
                        .append("\">")
                        .append(parentValue.getString())
                        .append("</option>")
                        .append(EOL);
            }
            parentCounter++;
        }
        parentBuilder.append("</select>").append(EOL);
        return parentBuilder;
    }

//    private Map<OrderedString, List<RestRow>> createTriageMapForCascadingSelect(String projectKey) {
//        Map<OrderedString, List<RestRow>> triageMap = new TreeMap<OrderedString, List<RestRow>>();
//        int groupingValueOrderNumber = 0;
//        Map<String, Integer> orderMap = new HashMap<String, Integer>();
//        for (RestRow restRow : rowCache.restQuery(projectKey)) {
//            String groupingValue = restRow.getValue();
//            Integer orderNumber = orderMap.get(groupingValue);
//            if (orderNumber == null) {
//                orderMap.put(groupingValue, groupingValueOrderNumber);
//                orderNumber = groupingValueOrderNumber;
//                groupingValueOrderNumber++;
//            }
//            OrderedString orderedString = new OrderedString(groupingValue, orderNumber);
//            List<RestRow> mapForGroupingValue = triageMap.get(orderedString);
//            if (mapForGroupingValue == null) {
//                mapForGroupingValue = new ArrayList<RestRow>();
//                triageMap.put(orderedString, mapForGroupingValue);
//            }
//            mapForGroupingValue.add(restRow);
//        }
//        return triageMap;
//    }

    private boolean isDependentOnProjectKey() {
        return false;
    }


    /**
     * Helper class to make sure the grouping values for cascading select
     * remain the in the sort order that is requested in the SQL query
     */
    private static class OrderedString implements Comparable<OrderedString> {
        private final String m_string;
        private final int m_orderNumber;

        private OrderedString(String string, int orderNumber) {
            m_string = string;
            m_orderNumber = orderNumber;
        }

        public String getString() {
            return m_string;
        }

        public int getOrderNumber() {
            return m_orderNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrderedString that = (OrderedString) o;

            if (m_orderNumber != that.m_orderNumber) {
                return false;
            }
            if (m_string != null ? !m_string.equals(that.m_string) : that.m_string != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = m_string != null ? m_string.hashCode() : 0;
            result = 31 * result + m_orderNumber;
            return result;
        }

        @Override
        public int compareTo(OrderedString orderedString) {
            return Integer.valueOf(m_orderNumber).compareTo(orderedString.m_orderNumber);
        }
    }
}

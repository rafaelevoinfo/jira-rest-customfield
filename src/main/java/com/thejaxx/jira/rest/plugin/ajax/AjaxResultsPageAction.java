package com.thejaxx.jira.rest.plugin.ajax;

import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.log4j.Logger;

import javax.servlet.ServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yoni Moses
 */
@SupportedMethods({RequestMethod.GET})   
public class AjaxResultsPageAction extends JiraWebActionSupport {
    private static final Logger logger = Logger.getLogger(AjaxResultsPageAction.class);

    private static final AjaxViewHelper viewHelper = new AjaxViewHelper();

    /**
     *
     */
    private static final long serialVersionUID = 7990817576683683217L;

    /**
     * Returns the HTML for the AJAX div with the choices for user,
     * depending on what already has been typed.
     *
     * @return a HTML snippet
     */

    public static Map<String, String> decodeQueryString(String query) {
        try {
            Map<String, String> params = new LinkedHashMap<>();
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=", 2);
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                if (!key.isEmpty()) {
                    params.put(key, value);
                }
            }
            return params;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e); // Cannot happen with UTF-8 encoding.
        }
    }

     
    public String getHtml(I18nHelper i18nBean) {

        logger.debug( "Query string " + getHttpRequest().getQueryString());
//		getHttpRequest().getP
//		getResponse().setContentType( "application/json" );

        ServletRequest request = ((ServletRequest) getHttpRequest());
        getHttpResponse().addHeader("Content-Type", "application/json");

        String customfieldId = request.getParameter("customfieldId");
        if (customfieldId != null) {
//            RestValuesViewHelper valuesViewHelper = getDatabaseValuesViewHelper(customfieldId);
            String query = request.getParameter("q");
            String projectKey = request.getParameter("projectKey");
            String purpose = request.getParameter("purpose");

            logger.debug( "Finding parameter for field: " + customfieldId + "=>" + request.getParameterNames().toString());
            Enumeration paramNames = request.getParameterNames();
            Map<String, String> substitutions = new HashMap<String, String>();

            while (paramNames.hasMoreElements()) {
                String paramName = String.valueOf(paramNames.nextElement());
                String[] paramValues = request.getParameterValues(paramName);
                // save all parameters in a map for later sql substitution
                if (paramName != null && paramValues != null) {
                    substitutions.put(paramName, paramValues[0]);
                }
            }

//            valuesViewHelper.setSqlSubstitutions(substitutions);

            return viewHelper.getHtmlForAjaxResults(query, projectKey, i18nBean, AjaxViewResultsPurpose.valueOf(purpose));
        } else {
            return "Please enter a customfieldId!";
        }
    }

//    private RestValuesViewHelper getDatabaseValuesViewHelper(String customfieldId) {
//        CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(customfieldId);
//        Map map = customField.getCustomFieldType().getVelocityParameters(null, customField, null);
//        return (RestValuesViewHelper) map.get("databaseValuesViewHelper");
//    }
}

package com.thejaxx.jira.rest.plugin;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.thejaxx.jira.rest.plugin.ajax.AjaxViewHelper;
import com.thejaxx.jira.rest.plugin.config.ConfigUtils;
import com.thejaxx.jira.rest.plugin.utils.RestFetcher;
import org.apache.log4j.Logger;
import java.util.List;

import javax.annotation.PostConstruct;
import java.util.Map;

@Scanned
public class RestCustomField extends GenericTextCFType implements SortableCustomField<String>/*implements SortableCustomField*//*implements MultipleSettableCustomFieldType,
		MultipleCustomFieldType,
		SortableCustomField*/ {

    // ------------------------------ FIELDS ------------------------------
    private static final Logger logger = Logger.getLogger(RestCustomField.class);

    // --------------------------- CONSTRUCTORS ---------------------------
    @ComponentImport
    private CustomFieldValuePersister customFieldValuePersister;

    @ComponentImport
    private GenericConfigManager genericConfigManager;

    @ComponentImport
    private TextFieldCharacterLengthValidator textFieldCharacterLengthValidator;

    @ComponentImport
    private JiraAuthenticationContext jiraAuthenticationContext;


    @ComponentImport
    private PluginSettingsFactory pluginSettingsFactory = null;

    private AjaxViewHelper ajaxViewHelper;
    private RestValuesViewHelper valuesViewHelper;

    protected RestCustomField(PluginSettingsFactory pluginSettingsFactory, CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager, TextFieldCharacterLengthValidator textFieldCharacterLengthValidator, JiraAuthenticationContext jiraAuthenticationContext) {
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, jiraAuthenticationContext);
        this.pluginSettingsFactory = pluginSettingsFactory;
    }


    @PostConstruct
    public void postConstruct() {
        logger.info("Running postConstruct ");
        ConfigUtils.init(pluginSettingsFactory);
        valuesViewHelper = new RestValuesViewHelper();
        ajaxViewHelper = new AjaxViewHelper();
    }

    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField customField, FieldLayoutItem fieldLayoutItem) {
//        System.out.println("valuesViewHelper = " + valuesViewHelper);
//        System.out.println("ajaxViewHelper = " + ajaxViewHelper);
        String parent = "null";
        if (issue != null && issue.getParentObject() != null) {
            parent = issue.getParentObject().getKey();
        }
        Map<String, Object> result = super.getVelocityParameters(issue, customField, fieldLayoutItem);
        result.put("viewHelper", ajaxViewHelper);
        result.put("valuesViewHelper", valuesViewHelper);
        
        List<RestRow> values = RestFetcher.doQuery(parent);
        //loop for values
        RestRowCache rowCache = new RestRowCache();
        for (RestRow row:values){
            rowCache.addCacheEntry(parent, row.getKey(), row);            
        }
        this.valuesViewHelper.updateCache(rowCache);
                
        if (issue != null && issue.getCustomFieldValue(customField) != null) {
            result.put("restValue", issue.getCustomFieldValue(customField));
        }
        result.put("array", values);
        return result;
    }

    // TODO: Enable this again when configuring via webinterface works
//
//	public List getConfigurationItemTypes()
//	{
//		final List configurationItemTypes = EasyList.build( JiraUtils.loadComponent( DefaultValueConfigItem.class ) );
//		configurationItemTypes.add( new DatabaseValuesCFConfigItemType( m_optionsManager ) );
//		return configurationItemTypes;
//	}

// --------------------- Interface MultipleCustomFieldType ---------------------

//	public Options getOptions( FieldConfig fieldConfig, JiraContextNode jiraContextNode )
//	{
//		return m_optionsManager.getOptions( fieldConfig );
//	}

// --------------------- Interface MultipleSettableCustomFieldType ---------------------

//	public Set getIssueIdsWithValue( CustomField customField, Option option )
//	{
//		if (option != null)
//		{
//			return customFieldValuePersister.getIssueIdsWithValue( customField, PersistenceFieldType.TYPE_LIMITED_TEXT, option.getValue() );
//		}
//		else
//		{
//			return Collections.EMPTY_SET;
//		}
//	}

//	public void removeValue( CustomField customField, Issue issue, Option option )
//	{
//		updateValue( customField, issue, null );
//	}


//    @Override
//    public String getChangelogValue(CustomField field, Object value) {
//        if (restValueHelper != null) {
//            return restValueHelper.getTextForChangeLog((String) value, getI18nBean());
//        } else {
//            return super.getChangelogValue(field, value);
//        }
//    }
//
//    @Override
//    public int compare(String customFieldObjectValue1, String customFieldObjectValue2, FieldConfig fieldConfig) {
//        // This compare function is only used when no searcher has been configured with the custom field
//        // If there is a searcher, the sorting is done via DatabaseValuesSearcher
//
//        return super.compare(restValueHelper.getStringForSorting(customFieldObjectValue1),
//                restValueHelper.getStringForSorting(customFieldObjectValue2),
//                fieldConfig);    //To change body of overridden methods use File | Settings | File Templates.
//    }
}





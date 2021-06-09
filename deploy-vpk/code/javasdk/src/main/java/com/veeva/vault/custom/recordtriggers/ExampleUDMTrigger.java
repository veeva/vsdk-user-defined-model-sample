/*
 * --------------------------------------------------------------------
 * RecordTrigger:	ExampleUDMTrigger
 * Object:			vsdk_udm_example__c
 * Author:			Veeva Vault Developer Support
 *---------------------------------------------------------------------
 * Description:	This code provides examples of a common use case
 * 				for a User-Defined Model (UDM): Custom SDK Settings.
 * 				When records are inserted or updated in a custom
 * 				object, vsdk_udm_example__c, this code loads
 * 				local and remote settings from a custom object,
 * 				vsdk_setting__c, where settings are stored as JSON.
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.recordtriggers;

import com.veeva.vault.custom.settings.*;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.*;
import com.veeva.vault.sdk.api.query.QueryResponse;
import com.veeva.vault.sdk.api.query.QueryService;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class annotation (@RecordTriggerInfo) indicates that this class is a record trigger.
 * It specifies the object that this trigger will run on(vsdk_udm_example__c), the events it will run on(BEFORE_INSERT, BEFORE_UPDATE).
 */
@RecordTriggerInfo(object = "vsdk_udm_example__c", events = {RecordEvent.BEFORE_INSERT, RecordEvent.BEFORE_UPDATE})
public class ExampleUDMTrigger implements RecordTrigger {

	private static final String OBJECT_FIELD_RESULTS = "results__c";
	private static final int DEFAULT_BATCH_SIZE = 500;
	private static final String DEFAULT_STATUS_TYPE = "pending__c";

	/**
	 * Example record trigger to load/save settings from local and remote vaults.
	 * The settings values are displayed in a RichText field on this object
	 */
	public void execute(RecordTriggerContext context) {
		// Usually you would not get just the single record, this is for demo purposes only
		Record firstRecord = context.getRecordChanges().get(0).getNew();

		// Uses the getLocalSettingsExample to get the local settings example record
		ExampleSettings exampleLocalSettings = getLocalSettingExample();
		StringBuilder results = new StringBuilder();
		results.append("<B>Local Batch Size<B>: ");
		results.append(exampleLocalSettings.getBatchSize().toPlainString());

		// Remote settings example (if connection is provided)
		String remoteConnectionId = firstRecord.getValue("remote_connection__c", ValueType.STRING);
		if (remoteConnectionId != null) {
			String remoteConnectionName = getConnectionName(remoteConnectionId);

			ExampleSettings exampleRemoteSettings = getRemoteSettingExample(remoteConnectionName);
			results.append("<BR> ");
			results.append("<B>Remote Batch Size<B>: ");
			results.append(exampleRemoteSettings.getBatchSize().toPlainString());
		}

		// For this example we are only setting the first record
		firstRecord.setValue(OBJECT_FIELD_RESULTS, results.toString());
	}

	/**
	 * Gets the connection name for the provided connectionId
	 *
	 * @return String (null if not found)
	 */
	String getConnectionName(String connectionId) {
		List<String> results = VaultCollections.newList();
		StringBuilder query = new StringBuilder();
		query.append("SELECT api_name__sys");
		query.append(" FROM connection__sys");
		query.append(" WHERE id = '" + connectionId + "'");
		QueryService queryService = ServiceLocator.locate(QueryService.class);
		QueryResponse queryResponse = queryService.query(query.toString());
		queryResponse.streamResults().forEach(queryResult -> {
			results.add(queryResult.getValue("api_name__sys", ValueType.STRING));
		});

		if (!results.isEmpty()) {
			return results.get(0);
		}

		return null;
	}

	/**
	 * Gets an example settings from the local vault. Creates/Saves new values if not found
	 *
	 * @return ExampleSettings
	 */
	ExampleSettings getLocalSettingExample() {
		// Load settings from the local vault
		// Get an instance of the CustomSetting service
		CustomSettingService settingService = ServiceLocator.locate(CustomSettingService.class);
		// The getLocalSettings method gets the example custom sdk job settings from the local vault
		// It return the record as a ExampleSettings User defined model
		ExampleSettings jobSetting = settingService.getLocalSettings(ExampleSettings.class);

		// If the model is not null, log the local model's batch size
		if (jobSetting != null) {
			LogService logger = ServiceLocator.locate(LogService.class);
			logger.debug("Current Local BatchSize = {}",jobSetting.getBatchSize().toString());
		} else {
			// If the record is not found in the local vault, then create an empty ExampleSettings UDM
			// Set values on the newly created UDM and save it

			// Get an instance of the UserDefinedModelService
			UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);
			// Create a new empty UserDefinedModel instance of the ExampleSettings model
			jobSetting = modelService.newUserDefinedModel(ExampleSettings.class);

			// Set default values
			jobSetting.setBatchSize(new BigDecimal(DEFAULT_BATCH_SIZE));
			jobSetting.setStatusTypes(VaultCollections.asList(DEFAULT_STATUS_TYPE));

			// Save new ExampleSettings UDM to local vault
			settingService.saveLocalSettings(jobSetting, ExampleSettings.class);
		}

		return jobSetting;
	}

	/**
	 * Gets an example settings from the remote vault. Creates/Saves new values if not found
	 *
	 * @return ExampleSettings
	 */
	ExampleSettings getRemoteSettingExample(String connectionName) {
		// Get an instance of the CustomSetting Service
		CustomSettingService settingService = ServiceLocator.locate(CustomSettingService.class);
		// Load settings from the remote vault
		ExampleSettings jobSetting = settingService.getRemoteSettings(ExampleSettings.class, connectionName);

		// If the model is not null, log the remote model's batch size
		// Otherwise create an empty UDM, set values, and then save the settings in the remote vault
		if (jobSetting != null) {
			LogService logger = ServiceLocator.locate(LogService.class);
			logger.debug("Current Remote BatchSize = {}",jobSetting.getBatchSize().toString());
		}
		else {
			// Get an instance of the UserDefinedModelService
			UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);
			// Create a new empty UserDefinedModel instance of the ExampleSettings model
			jobSetting = modelService.newUserDefinedModel(ExampleSettings.class);

			// Set default values
			jobSetting.setBatchSize(new BigDecimal(DEFAULT_BATCH_SIZE));
			jobSetting.setStatusTypes(VaultCollections.asList(DEFAULT_STATUS_TYPE));

			settingService.saveLocalSettings(jobSetting, ExampleSettings.class);

			// Save new ExampleSettings UDM to the remote vault
			settingService.saveRemoteSettings(jobSetting, ExampleSettings.class, connectionName);
		}

		return jobSetting;
	}
}

/*
 * --------------------------------------------------------------------
 * UserDefinedService:	CustomSettingService
 * Author:				Veeva Vault Developer Support
 *---------------------------------------------------------------------
 * Description:	This code provides examples of a common use case
 * 				for a User-Defined Model (UDM): Custom SDK Settings.
 * 				The custom User-Defined Service (UDS) provides
 * 				an extendable custom service to convert data between
 * 				UDM and JSON data is stored in vsdk_setting__c.
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.settings;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.http.HttpMethod;
import com.veeva.vault.sdk.api.http.HttpRequest;
import com.veeva.vault.sdk.api.http.HttpResponseBodyValueType;
import com.veeva.vault.sdk.api.http.HttpService;
import com.veeva.vault.sdk.api.json.JsonService;
import com.veeva.vault.sdk.api.query.QueryResponse;
import com.veeva.vault.sdk.api.query.QueryService;

import java.util.List;

@UserDefinedServiceInfo
public class CustomSettingServiceImpl implements CustomSettingService {

    private static final String API_VERSION = "v21.2";
    private static final String OBJECT_SDK_SETTING = "vsdk_setting__c";
    private static final String OBJECT_FIELD_ID = "id";
    private static final String OBJECT_FIELD_JSON = "json__c";
    private static final String OBJECT_FIELD_NAME = "name__v";
    private static final String QUERY_ID_PARAM = "idParam";

    /**
     * getLocalSettingsResponse queries the local vault using the QueryService
     * Returns only the id or the id, name__v, and JSON as plaintext
     *
     *
     * @param settingsClass the interface reference
     * @param includeJson boolean indicates to include the json field in the response or not
     *
     * @return the user-defined model representation of the settings
     */
    private <U extends UserDefinedModel> QueryResponse getLocalSettingResponse(Class<U> settingsClass, boolean includeJson) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT " + OBJECT_FIELD_ID);
        if (includeJson) {
            query.append(", LONGTEXT(" + OBJECT_FIELD_JSON + ")");
        }
        query.append(" FROM " + OBJECT_SDK_SETTING);
        query.append(" WHERE " + OBJECT_FIELD_NAME + " = '");
        query.append(getSettingName(settingsClass));
        query.append("'");

        QueryService queryService = ServiceLocator.locate(QueryService.class);
        return queryService.query(query.toString());
    }

    /**
     * Retrieves local settings based on the interface name
     *
     * @param settingsClass the interface reference
     *
     * @return the user-defined model representation of the settings
     */
    public <U extends UserDefinedModel> U getLocalSettings(Class<U> settingsClass) {
        JsonService jsonService = ServiceLocator.locate(JsonService.class);

        List<U> results = VaultCollections.asList();

        // Use the query response from the getLocalSettingsResponse method to get the json from the records
        // The json is then converted to the ExampleSettings User-Defined Model
        // Then the ExampleSettings UDM is added to the results list
        QueryResponse queryResponse = getLocalSettingResponse(settingsClass, true);
        queryResponse.streamResults().forEach(queryResult -> {
            String json = queryResult.getValue(OBJECT_FIELD_JSON, ValueType.STRING);
            if (json != null) {
                U settingModel = jsonService.convertToUserDefinedModel(json, settingsClass);
                results.add(settingModel);
            }
        });

        // This source code project only expects one example settings record in the local vault
        // Therefore only the first ExampleSettings UDM from the results list is returned
        if (!results.isEmpty()) {
            return results.get(0);
        }

        // Settings were not found
        return null;
    }


    /**
     * Query the remote vault and return only the JSON as plaintext
     *
     * @param settingsClass the interface reference
     * @param connectionName name of the remote connection
     *
     * @return the user-defined model representation of the settings
     */
    private <U extends UserDefinedModel> SettingRecordModel getRemoteSettingResponse(Class<U> settingsClass, String connectionName) {
        // Get an instance of the HTTPService which is used to make HTTP calls
        HttpService httpService = ServiceLocator.locate(HttpService.class);
        LogService logService = ServiceLocator.locate(LogService.class);

        List<SettingRecordModel> results = VaultCollections.newList();

        StringBuilder query = new StringBuilder();
        query.append("SELECT LONGTEXT(" + OBJECT_FIELD_JSON + ")");
        query.append(" FROM " + OBJECT_SDK_SETTING);
        query.append(" WHERE " + OBJECT_FIELD_NAME + " = '");
        query.append(getSettingName(settingsClass));
        query.append("'");

        // Make a VQL query against the remote vault
        HttpRequest queryRequest = httpService.newHttpRequest(connectionName);
        queryRequest.appendPath("/api/" + API_VERSION + "/query");
        queryRequest.setBodyParam("q", query.toString());
        // Send the HTTP request and convert the response to SettingQueryResponseModel User-defined Model
        httpService.send(queryRequest, SettingQueryResponseModel.class)
                .onError(response -> {
                    // If an error occurs, log the error using the LogService
                    logService.error(response.getMessage());
                })
                .onSuccess(response -> {
                    // Get the response UDM
                    SettingQueryResponseModel responseModel = response.getResponseBody();
                    if (responseModel != null) {
                        if (responseModel.getData() != null && !responseModel.getData().isEmpty()) {
                            // Get the Settings model data and add it to the results list
                            SettingRecordModel remoteSettingModel = responseModel.getData().get(0);
                            results.add(remoteSettingModel);
                        }
                    }
                })
                .execute();

        // This source code project only expects one example settings record in the remote vault
        // Therefore only the first ExampleSettings UDM from the results list is returned
        if (!results.isEmpty()) {
            return results.get(0);
        }

        // Settings were not found
        return null;
    }

    /**
     * Retrieves remote settings based on the interface name and connection
     *
     * @param settingsClass the interface reference
     * @param connectionName name of the remote connection
     *
     * @return the user-defined model representation of the settings
     */
    public <U extends UserDefinedModel> U getRemoteSettings(Class<U> settingsClass, String connectionName) {
        JsonService jsonService = ServiceLocator.locate(JsonService.class);

        // If the Remote Settings UDM is not null then convert it to the ExampleSettings class
        SettingRecordModel remoteSettingModel = getRemoteSettingResponse(settingsClass, connectionName);
        if (remoteSettingModel != null) {
            return jsonService.convertToUserDefinedModel(remoteSettingModel.getJson(), settingsClass);
        }

        return null;
    }

    /**
     * Convert a user-defined model into it's canonical name
     *
     * @param settingsClass the interface reference
     *
     * @return fully qualified interface name
     */
    private <U extends UserDefinedModel> String getSettingName(Class<U> settingsClass) {
        return settingsClass.toString().replace("interface ", "");
    }

    /**
     * Saves local settings based on the interface name
     *
     * @param settingsModel model instance
     * @param settingsClass the interface reference
     *
     */
    public <U extends UserDefinedModel> void saveLocalSettings(U settingsModel, Class<U> settingsClass) {
        JsonService jsonService = ServiceLocator.locate(JsonService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);

        // Convert the UDM into JSON
        String json = jsonService.convertToString(settingsModel);

        List<Record> settingRecords = VaultCollections.newList();

        // Find the existing setting record
        QueryResponse queryResponse = getLocalSettingResponse(settingsClass, false);
        queryResponse.streamResults().forEach(queryResult -> {
            String id = queryResult.getValue(OBJECT_FIELD_ID, ValueType.STRING);
            Record record = recordService.newRecordWithId(OBJECT_SDK_SETTING, id);
            record.setValue(OBJECT_FIELD_JSON, json);
            settingRecords.add(record);
        });

        // If the settings aren't found then create a new record
        if (settingRecords.isEmpty()) {
            Record record = recordService.newRecord(OBJECT_SDK_SETTING);
            record.setValue(OBJECT_FIELD_NAME, getSettingName(settingsClass));
            record.setValue(OBJECT_FIELD_JSON, json);
            settingRecords.add(record);
        }

        // Save the settings record using the RecordService
        recordService.batchSaveRecords(settingRecords)
                .rollbackOnErrors()
                .execute();
    }

    /**
     * Saves remote settings based on the interface name and connection
     *
     * @param settingsModel model instance
     * @param settingsClass the interface reference
     * @param connectionName name of the remote connection
     *
     */
    public <U extends UserDefinedModel> void saveRemoteSettings(U settingsModel, Class<U> settingsClass, String connectionName) {
        HttpService httpService = ServiceLocator.locate(HttpService.class);
        JsonService jsonService = ServiceLocator.locate(JsonService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);

        // Convert the UDM into JSON using the convertToString method
        // Then use the UserDefinedModelService to create a SettingRecordModel and set the name and the json in it
        String settingName = getSettingName(settingsClass);
        String json = jsonService.convertToString(settingsModel);
        SettingRecordModel remoteSettingModel = modelService.newUserDefinedModel(SettingRecordModel.class);
        remoteSettingModel.setName(settingName);
        remoteSettingModel.setJson(json);

        // Using the HTTPService, create a record in the remote vault for the settings
        HttpRequest updateRequest = httpService.newHttpRequest(connectionName);
        updateRequest.setQuerystringParam(QUERY_ID_PARAM, OBJECT_FIELD_NAME);
        updateRequest.setMethod(HttpMethod.POST);
        updateRequest.appendPath("/api/" + API_VERSION + "/vobjects/" + OBJECT_SDK_SETTING);
        updateRequest.setBody(VaultCollections.asList(remoteSettingModel));
        httpService.send(updateRequest, HttpResponseBodyValueType.JSONDATA)
                .onError(response -> {
                    // Log error message using the logService
                    logService.error(response.getMessage());
                })
                .onSuccess(response -> {
                    // Log response body json using the logService
                    logService.info(response.getResponseBody().getJsonObject().asString());
                })
                .execute();
    }
}
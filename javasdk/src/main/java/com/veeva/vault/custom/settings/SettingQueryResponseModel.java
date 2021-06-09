/*
 * --------------------------------------------------------------------
 * UserDefinedModel:	SettingQueryResponseModel
 * Author:				Veeva Vault Developer Support
 *---------------------------------------------------------------------
 * Description:	This code provides examples of a common use case
 * 				for a User-Defined Model (UDM): Custom SDK Settings.
 * 				The custom interface provides an extendable model with
 * 				named getters and setters that represents JSON data.
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.settings;

import com.veeva.vault.sdk.api.core.UserDefinedModel;
import com.veeva.vault.sdk.api.core.UserDefinedModelInfo;
import com.veeva.vault.sdk.api.core.UserDefinedProperty;
import com.veeva.vault.sdk.api.core.UserDefinedPropertyInclude;

import java.util.List;
import java.util.Map;

/**
 * Model that presents VQL query response with data from Vault
 *
 * @return RemoteSettingResponseModel
 */
@UserDefinedModelInfo(include = UserDefinedPropertyInclude.NON_NULL)
public interface SettingQueryResponseModel extends UserDefinedModel {

	@UserDefinedProperty
	List<SettingRecordModel> getData();

	@UserDefinedProperty
	String getResponseStatus();

	@UserDefinedProperty
	List<Map<String,String>> getErrors();
}
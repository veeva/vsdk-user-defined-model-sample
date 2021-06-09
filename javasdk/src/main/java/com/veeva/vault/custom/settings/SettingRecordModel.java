/*
 * --------------------------------------------------------------------
 * UserDefinedModel:	SettingRecordModel
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

import com.veeva.vault.sdk.api.core.*;

/**
 * Model that presents a single vsdk_setting__c record for a remote vault
 *
 * @return RemoteSettingRecordModel
 */
@UserDefinedModelInfo()
public interface SettingRecordModel extends UserDefinedModel {

	@UserDefinedProperty(name = "name__v")
	String getName();
	void setName(String name);

	@UserDefinedProperty(name = "json__c")
	String getJson();
	void setJson(String json);
}
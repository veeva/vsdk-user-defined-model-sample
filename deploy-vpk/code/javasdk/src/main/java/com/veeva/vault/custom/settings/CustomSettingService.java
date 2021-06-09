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

@UserDefinedServiceInfo
public interface CustomSettingService extends UserDefinedService {
	/**
	 * Retrieves local setting
	 *
	 * @return the user-defined model representation of the settings
	 */
	<U extends UserDefinedModel> U getLocalSettings(Class<U> settingsClass);

	/**
	 * Saves local setting
	 *
	 * @return success/failure
	 */
	<U extends UserDefinedModel> void saveLocalSettings(U settingsModel, Class<U> settingsClass);

	/**
	 * Retrieves remote setting
	 *
	 * @return the user-defined model representation of the settings
	 */
	<U extends UserDefinedModel> U getRemoteSettings(Class<U> settingsClass, String connectionName);

	/**
	 * Saves remote setting
	 *
	 * @return success/failure
	 */
	<U extends UserDefinedModel> void saveRemoteSettings(U settingsModel, Class<U> settingsClass, String connectionName);
}
/*
 * --------------------------------------------------------------------
 * UserDefinedModel:	ExampleSettings
 * Author:				Veeva Vault Developer Support
 *---------------------------------------------------------------------
 * Description:	This code provides examples of a common use case
 * 				for a User-Defined Model (UDM): Custom SDK Settings.
 * 				The custom interface provides an extendable model with
 * 				named getters and setters that represents JSON data.
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.settings;

import com.veeva.vault.sdk.api.core.UserDefinedModel;
import com.veeva.vault.sdk.api.core.UserDefinedModelInfo;
import com.veeva.vault.sdk.api.core.UserDefinedProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Example model of settings from a local vault
 *
 * @return ExampleLocalSettings
 */
@UserDefinedModelInfo()
public interface ExampleSettings extends UserDefinedModel {
	@UserDefinedProperty(name = "batch_size")
	BigDecimal getBatchSize();
	void setBatchSize(BigDecimal batchSize);

	@UserDefinedProperty(name = "status_types")
	List<String> getStatusTypes();
	void setStatusTypes(List<String> statusTypes);
}
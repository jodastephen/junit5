/*
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.engine;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import org.apiguardian.api.API;

/**
 * @since 1.5
 */
@API(status = EXPERIMENTAL, since = "1.5")
public interface ConfigurationParameter {
	String getKey();

	String getDefaultValue();

	String getDescription();
}
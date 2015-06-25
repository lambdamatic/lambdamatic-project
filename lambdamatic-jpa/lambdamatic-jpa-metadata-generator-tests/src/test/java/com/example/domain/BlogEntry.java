/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package com.example.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

/**
 * @author xcoulon
 *
 */
@Entity
public class BlogEntry {

	@Basic
	private String author;
}

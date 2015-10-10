/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import java.util.Collection;

/**
 * Base class for Template elements.
 */
public abstract class TemplateElement {

  /**
   * @return all the required Java types for this {@link TemplateElement}.
   */
  public abstract Collection<String> getRequiredJavaTypes();

}

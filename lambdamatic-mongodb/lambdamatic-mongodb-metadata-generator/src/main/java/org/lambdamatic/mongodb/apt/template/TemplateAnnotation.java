/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An annotation used in a template when generating code.
 */
public class TemplateAnnotation extends TemplateElement {

  /** the annotation type. */
  private final TemplateType type;
  /** the attributes to output in the template. */
  private final Map<String, Object> attributes;

  /**
   * Builder for the {@link TemplateAnnotation}.
   */
  public static class Builder {

    /**
     * Constructor Factory.
     * 
     * @param type the actual type to wrap in a {@link TemplateType}
     * @return a new Builder instance
     */
    public static Builder type(final Class<?> type) {
      return new Builder(type);
    }

    /** the annotation type. */
    private final TemplateType type;
    /** the attributes to output in the template. */
    private final Map<String, Object> attributes;

    private Builder(final Class<?> type) {
      this.type = TemplateType.Builder.with(type.getName()).build();
      this.attributes = new HashMap<>();
    }

    /**
     * Adds an attribute to the annotation.
     * 
     * @param name the attribute name
     * @param value the attribute value, assuming it is a String
     * @return the current {@link Builder}
     */
    public Builder attribute(final String name, final Object value) {
      this.attributes.put(name, value);
      return this;
    }

    /**
     * @return a new {@link TemplateAnnotation}.
     */
    public TemplateAnnotation build() {
      return new TemplateAnnotation(this.type, this.attributes);
    }
  }

  /**
   * Constructor
   * 
   * @param type the annotation type.
   * @param attributes the attributes to output in the template.
   */
  TemplateAnnotation(final TemplateType type, final Map<String, Object> attributes) {
    this.type = type;
    this.attributes = attributes;
  }

  /**
   * Assumes that the annotation only requires its own type, ie, that the attribute values are _not_
   * nested annotations.
   */
  @Override
  public Collection<String> getRequiredJavaTypes() {
    return this.type.getRequiredJavaTypes();
  }

  /**
   * @return the printable version of the annotation in the generated source code.
   */
  @Override
  public String toString() {
    final StringBuilder printBuilder = new StringBuilder();
    printBuilder.append('@').append(this.type.getSimpleName());
    final String attributesOutput = this.attributes.entrySet().stream().map(entry -> {
      if (entry.getValue() instanceof String) {
        return entry.getKey() + " = \"" + entry.getValue() + "\"";
      } else if (entry.getValue() instanceof Class) {
        return entry.getKey() + " = " + ((Class<?>) entry.getValue()).getName() + ".class";
      } else {
        return entry.getKey() + " = " + entry.getValue();
      }
    }).collect(Collectors.joining(", "));
    if (!attributesOutput.isEmpty()) {
      printBuilder.append('(').append(attributesOutput).append(')');
    }
    return printBuilder.toString();
  }

}

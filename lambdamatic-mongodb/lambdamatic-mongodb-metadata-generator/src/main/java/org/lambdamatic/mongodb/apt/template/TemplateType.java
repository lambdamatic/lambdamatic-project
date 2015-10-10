/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import static org.lambdamatic.mongodb.apt.template.ElementUtils.isCollection;
import static org.lambdamatic.mongodb.apt.template.ElementUtils.isMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.apt.MetadataGenerationException;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.ProjectionArray;
import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMap;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMap;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateArray;
import org.lambdamatic.mongodb.metadata.UpdateMap;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * A type (or parameterized) type to be used in the class templates. Provides the simple Java type
 * name along with all types to declare as import statements.
 * <p>
 * Eg: field of type <code>List&lt;Foo&gt;</code> requires imports of <code>java.util.List</code>
 * and <code>com.sample.Foo</code>.
 * </p>
 * 
 * @author Xavier Coulon
 *
 */
public class TemplateType extends TemplateElement {

  private static final Function<TypeMirror, TemplateType> primitiveTypeToQueryType =
      variableType -> Builder.with(QueryField.class)
          .withTypeParameters(ElementUtils.getSimilarDeclaredType((PrimitiveType) variableType))
          .build();

  private static final Function<TypeMirror, TemplateType> primitiveTypeToProjectionType =
      variableType -> Builder.with(ProjectionField.class).build();

  private static final Function<TypeMirror, TemplateType> primitiveTypeToUpdateType =
      variableType -> Builder
          .with(ElementUtils.getSimilarDeclaredType((PrimitiveType) variableType)).build();

  private static final Function<DeclaredType, TemplateType> embeddedDocumentToQueryType =
      declaredType -> Builder
          .with(BaseTemplateContext.elementToPackageName.apply(declaredType) + '.'
              + MetadataTemplateContext.elementToQuerySimpleClassNameFunction.apply(declaredType))
          .build();

  private static final Function<DeclaredType, TemplateType> embeddedDocumentToProjectionType =
      declaredType -> Builder
          .with(BaseTemplateContext.elementToPackageName.apply(declaredType) + '.'
              + MetadataTemplateContext.elementToProjectionSimpleClassName.apply(declaredType))
          .build();

  private static final Function<DeclaredType, TemplateType> embeddedDocumentToUpdateType =
      declaredType -> Builder
          .with(BaseTemplateContext.elementToPackageName.apply(declaredType) + '.'
              + MetadataTemplateContext.elementToUpdateSimpleClassNameFunction.apply(declaredType))
          .build();

  private static final Function<TypeMirror, TemplateType> collectionToQueryType =
      collectionType -> Builder.with(QueryArray.class)
          .withTypeParameters(getQueryMetadataFieldType(collectionType)).build();

  private static final Function<TypeMirror, TemplateType> collectionToProjectionType =
      collectionType -> Builder.with(ProjectionArray.class)
          .withTypeParameters(getQueryMetadataFieldType(collectionType)).build();

  private static final Function<TypeMirror, TemplateType> collectionToUpdateType =
      collectionType -> Builder.with(UpdateArray.class).parameterTypes(collectionType.toString())
          .build();

  private static final Function<DeclaredType, TemplateType> mapToQueryType =
      mapType -> Builder.with(QueryMap.class)
          .withTypeParameters(Builder.with(mapType.getTypeArguments().get(0).toString()).build(),
              getQueryMetadataFieldType(mapType.getTypeArguments().get(1)))
          .build();

  private static final Function<DeclaredType, TemplateType> mapToProjectionType =
      mapType -> Builder.with(ProjectionMap.class)
          .withTypeParameters(Builder.with(mapType.getTypeArguments().get(0).toString()).build(),
              getQueryMetadataFieldType(mapType.getTypeArguments().get(1)))
          .build();

  private static final Function<DeclaredType, TemplateType> mapToUpdateType =
      mapType -> Builder.with(UpdateMap.class)
          .withTypeParameters(Builder.with(mapType.getTypeArguments().get(0).toString()).build(),
              getQueryMetadataFieldType(mapType.getTypeArguments().get(1)))
          .build();

  private static final Function<DeclaredType, TemplateType> declaredTypeToQueryType =
      declaredType -> {
        switch (declaredType.toString()) {
          case "org.lambdamatic.mongodb.types.geospatial.Location":
            return Builder.with(LocationField.class).build();
          default:
            return Builder.with(QueryField.class).parameterTypes(declaredType.toString()).build();
        }
      };

  private static final Function<DeclaredType, TemplateType> declaredTypeToProjectionType =
      declaredType -> Builder.with(ProjectionField.class).build();

  private static final Function<DeclaredType, TemplateType> declaredTypeToUpdateType =
      declaredType -> {
        switch (declaredType.toString()) {
          case "org.lambdamatic.mongodb.types.geospatial.Location":
            return Builder.with(LocationField.class).build();
          default:
            return Builder.with(declaredType.toString()).build();
        }
      };

  /**
   * Returns the fully qualified name of the type of the given {@link VariableElement}, or throws a
   * {@link MetadataGenerationException} if it was not a known or supported type.
   * 
   * @param variableType the variable to analyze
   * @return the {@link TemplateType} to use in the {@link QueryMetadata} implementation
   * @throws MetadataGenerationException if the given variable type is not supported
   */
  public static TemplateType getQueryMetadataFieldType(final TypeMirror variableType)
      throws MetadataGenerationException {
    return getMetadataFieldType(variableType, primitiveTypeToQueryType, embeddedDocumentToQueryType,
        collectionToQueryType, mapToQueryType, declaredTypeToQueryType);
  }

  /**
   * Returns the fully qualified name of the type of the given {@link VariableElement}, or
   * {@code null} if it was not a known or supported type.
   * 
   * @param variableType the variable type to analyze
   * @return the {@link TemplateType} to use in the {@link ProjectionMetadata} implementation
   * @throws MetadataGenerationException if the given variable type is not supported
   */
  public static TemplateType getProjectionMetadataFieldType(final TypeMirror variableType) {
    return getMetadataFieldType(variableType, primitiveTypeToProjectionType,
        embeddedDocumentToProjectionType, collectionToProjectionType, mapToProjectionType,
        declaredTypeToProjectionType);
  }

  /**
   * Returns the fully qualified name of the type of the given {@link VariableElement}, or
   * {@code null} if it was not a known or supported type.
   * 
   * @param variableType the variable to analyze
   * @return the {@link TemplateType} to use in the {@link UpdateMetadata} implementation
   * @throws MetadataGenerationException if the given variable type is not supported
   */
  public static TemplateType getUpdateMetadataFieldType(final TypeMirror variableType)
      throws MetadataGenerationException {
    return getMetadataFieldType(variableType, primitiveTypeToUpdateType,
        embeddedDocumentToUpdateType, collectionToUpdateType, mapToUpdateType,
        declaredTypeToUpdateType);
  }

  /**
   * Returns the {@link TemplateType} for the given {@link VariableType}, or throws a
   * {@link MetadataGenerationException} if it was not a known or supported type.
   * 
   * @param variableType the variable to analyze
   * @return the corresponding {@link TemplateType}
   * @throws MetadataGenerationException if the given variable type is not supported
   */
  private static TemplateType getMetadataFieldType(final TypeMirror variableType,
      final Function<TypeMirror, TemplateType> primitiveTypeToTemplateType,
      final Function<DeclaredType, TemplateType> embeddedDocumentToTemplateType,
      final Function<TypeMirror, TemplateType> collectionToTemplateType,
      final Function<DeclaredType, TemplateType> mapToTemplateType,
      final Function<DeclaredType, TemplateType> declaredTypeToTemplateType)
          throws MetadataGenerationException {
    if (variableType instanceof PrimitiveType) {
      return primitiveTypeToTemplateType.apply(variableType);
    } else if (variableType instanceof DeclaredType) {
      final DeclaredType declaredType = (DeclaredType) variableType;
      final TypeElement declaredElement = (TypeElement) declaredType.asElement();
      if (declaredElement.getAnnotation(EmbeddedDocument.class) != null) {
        // embedded documents
        return embeddedDocumentToTemplateType.apply(declaredType);
      } else if (isCollection(declaredElement)) {
        // collections (list/set)
        return collectionToTemplateType.apply(declaredType.getTypeArguments().get(0));
      } else if (isMap(declaredElement)) {
        // map
        return mapToTemplateType.apply(declaredType);
      } else {
        return declaredTypeToTemplateType.apply(declaredType);
      }
    } else if (variableType.getKind() == TypeKind.ARRAY) {
      final TypeMirror componentType = ((ArrayType) variableType).getComponentType();
      return collectionToTemplateType.apply(componentType);
    }
    throw new MetadataGenerationException("Unexpected variable type: " + variableType);
  }

  /**
   * {@link TemplateType} builder.
   */
  static class Builder {

    private String fullyQualifiedName;

    private List<TemplateType> parameterTypes;

    private Builder(final String fullyQualifiedName) {
      this.fullyQualifiedName = fullyQualifiedName;
    }

    static Builder with(final Class<?> javaType) {
      return new Builder(javaType.getName());
    }

    static Builder with(final String fullyQualifiedName) {
      return new Builder(fullyQualifiedName);
    }

    Builder withTypeParameters(final TemplateType... parameterTypes) {
      this.parameterTypes = Arrays.asList(parameterTypes);
      return this;
    }

    Builder withTypeParameters(final Class<?>... parameterTypes) {
      this.parameterTypes =
          Stream.of(parameterTypes).map(c -> new TemplateType(c.getName(), Collections.emptyList()))
              .collect(Collectors.toList());
      return this;
    }

    Builder parameterTypes(final String... parameterTypeNames) {
      this.parameterTypes = Stream.of(parameterTypeNames)
          .map(t -> new TemplateType(t, Collections.emptyList())).collect(Collectors.toList());
      return this;
    }

    TemplateType build() {
      if (this.parameterTypes == null) {
        this.parameterTypes = Collections.emptyList();
      }
      return new TemplateType(this.fullyQualifiedName, this.parameterTypes);
    }

  }

  /** the fully qualified name of the Java field type. */
  private final String fullyQualifiedName;

  /** the simple name of the Java field type. */
  private final String simpleName;

  /** the fully qualified names of the type parameters. */
  private final LinkedList<String> javaTypeParameterNames;

  /** all Java types to declare in the imports. */
  private final Set<String> requiredTypes;

  /**
   * Constructor for a parameterized type
   * 
   * @param fullyQualifiedName the fully qualified name of the corresponding Java field type.
   * @param parameterTypes the Java parameter types.
   */
  TemplateType(final String fullyQualifiedName, final List<TemplateType> parameterTypes) {
    this.fullyQualifiedName = fullyQualifiedName;
    this.javaTypeParameterNames = parameterTypes.stream().map(p -> p.fullyQualifiedName)
        .collect(Collectors.toCollection(LinkedList::new));
    final StringBuilder simpleNameBuilder = new StringBuilder();
    simpleNameBuilder.append(ClassUtils.getShortCanonicalName(fullyQualifiedName));
    if (!parameterTypes.isEmpty()) {
      simpleNameBuilder.append('<')
          .append(
              parameterTypes.stream().map(p -> p.getSimpleName()).collect(Collectors.joining(", ")))
          .append('>');
    }
    this.simpleName = simpleNameBuilder.toString();
    this.requiredTypes = parameterTypes.stream().flatMap(p -> {
      return p.getRequiredJavaTypes().stream();
    }).collect(Collectors.toSet());
    if (!ClassUtils.isPrimitiveOrWrapper(ElementUtils.getVariableType(fullyQualifiedName))) {
      this.requiredTypes.add(fullyQualifiedName);
    }
  }

  /**
   * @return the simple name of the Java field type.
   */
  public String getSimpleName() {
    return this.simpleName;
  }

  /**
   * @return the type parameters.
   */
  public LinkedList<String> getJavaTypeParameters() {
    return this.javaTypeParameterNames;
  }

  /**
   * @return all the Java types to declare in the imports (can be one or many if this
   *         {@link TemplateType} is for a parameterized type).
   */
  @Override
  public Collection<String> getRequiredJavaTypes() {
    return this.requiredTypes;
  }

  @Override
  public String toString() {
    return this.simpleName;
  }
}

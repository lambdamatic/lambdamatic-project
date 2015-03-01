package $packageName$;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * The {@link ProjectionMetadata} class associated with the {@link $domainClassName$} domain class annotated with {@link Document}.
 *
 */
@Generated(value="$processorClassName$")
public class $projectionMetadataClassName$ implements ProjectionMetadata<$domainClassName$> {

$projectionFields:{
  field | 
 	public $field.javaFieldType$ $field.javaFieldName$ = new $field.javaFieldType$("$field.documentFieldName$");
  
}$
}

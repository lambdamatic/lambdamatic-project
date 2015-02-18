package $packageName$;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * The {@link ProjectionMetadata} class associated with the {@link $domainClassName$} domain class.
 *
 */
@Generated(value="$processorClassName$")
public class $projectionMetadataClassName$ implements ProjectionMetadata<$domainClassName$> {

$queryFields:{
  field | 
 	public ProjectionField $field.javaFieldName$ = new ProjectionField("$field.documentFieldName$");
  
}$
}

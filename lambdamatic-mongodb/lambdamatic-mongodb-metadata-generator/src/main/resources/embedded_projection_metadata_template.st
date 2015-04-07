package $packageName$;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * The {@link ProjectionMetadata} class associated with the {@link $domainClassName$} domain class annotated with {@link EmbeddedDocument}.
 *
 */
@Generated(value="$processorClassName$")
public class $projectionMetadataClassName$ extends ProjectionField implements ProjectionMetadata<$domainClassName$> {

    /**
	 * Constructor
	 * @param fieldName the name of the field in MongoDB.
	 */
	public $projectionMetadataClassName$(final String fieldName) {
		super(fieldName);
	}
	
$projectionFields:{
  field | 
 	public $field.javaFieldType.simpleName$ $field.javaFieldName$ = new $field.javaFieldType.simpleName$("$field.documentFieldName$");
  
}$
}

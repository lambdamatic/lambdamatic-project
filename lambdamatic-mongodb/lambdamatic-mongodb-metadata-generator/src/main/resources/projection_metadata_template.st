package $packageName$;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.annotations.DocumentField;

$imports:{
 className |
import $className$;
}$
/**
 * The {@link ProjectionMetadata} class associated with the {@link $domainClassName$} domain class annotated with {@link Document}.
 *
 * <strong>Note:</strong>
 * <p>This class is not meant to be extended nor instantiated.</p>
 */
@Generated(value="$processorClassName$")
public abstract class $projectionMetadataClassName$ implements ProjectionMetadata<$domainClassName$> {

$projectionFields:{
  field | 
    @DocumentField(name="$field.documentFieldName$")
 	public $field.javaFieldType.simpleName$ $field.javaFieldName$;
  
}$
}

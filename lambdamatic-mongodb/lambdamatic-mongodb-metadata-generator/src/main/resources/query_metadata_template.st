package $packageName$;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.QueryMetadata;

import org.lambdamatic.mongodb.annotations.DocumentField;

$imports:{
 className |
import $className$;
}$

/**
 * The {@link QueryMetadata} class associated with the {@link $domainClassName$} domain class.
 *
 */
@Generated(value="$processorClassName$")
public class $queryMetadataClassName$ implements QueryMetadata<$domainClassName$> {

$queryFields:{
  field | 
	@DocumentField(name="$field.documentFieldName$")
 	public $field.javaFieldType.simpleName$ $field.javaFieldName$;
  
}$
}

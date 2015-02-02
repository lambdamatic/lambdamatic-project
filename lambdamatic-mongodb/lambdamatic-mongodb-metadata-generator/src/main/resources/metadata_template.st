package $packageName$;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.Metadata;

import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * The {@link Metadata} class associated with the {@link $domainClassName$} domain class.
 *
 */
@Generated(value="$processorClassName$")
public class $metadataDomainClassName$ implements Metadata<$domainClassName$> {

$fields:{
  field | 
	@DocumentField(name="$field.documentFieldName$")
 	public $field.javaFieldType$ $field.javaFieldName$;
  
}$
}

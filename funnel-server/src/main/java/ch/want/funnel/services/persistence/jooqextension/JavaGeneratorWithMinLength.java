/*
 * Created on 20 Apr 2018
 */
package ch.want.funnel.services.persistence.jooqextenstion;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.util.CheckConstraintDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DataTypeDefinition;
import org.jooq.util.GeneratorStrategy.Mode;
import org.jooq.util.JavaGenerator;
import org.jooq.util.JavaWriter;
import org.jooq.util.TypedElementDefinition;

public class JavaGeneratorWithMinLength extends JavaGenerator {
    private static final Pattern CHAR_LENGTH_CONSTRAINT = Pattern.compile(".*char_length\\(+\\w+\\)+::text\\)? > (\\d+).*");
    private List<CheckConstraintDefinition> checkConstraints;

    @Override
    protected void generatePojoGetter(final TypedElementDefinition<?> column, final int index, final JavaWriter out) {
	final String columnTypeFull = getJavaType(column.getType(), Mode.POJO);
	final String columnType = out.ref(columnTypeFull);
	final String columnGetter = getStrategy().getJavaGetterName(column, Mode.POJO);
	final String columnMember = getStrategy().getJavaMemberName(column, Mode.POJO);
	// Getter
	out.println();
	//
	printDeprecationIfUnknownType(out, columnTypeFull);
	//
	if (column instanceof ColumnDefinition) {
	    printColumnJPAAnnotation(out, (ColumnDefinition) column);
	}
	//
	printValidationAnnotation(out, column);
	//
	if (isScala()) {
	    out.tab(1).println("def %s : %s = {", columnGetter, columnType);
	    out.tab(2).println("this.%s", columnMember);
	    out.tab(1).println("}");
	} else {
	    out.tab(1).overrideIf(generateInterfaces());
	    out.tab(1).println("public %s %s() {", columnType, columnGetter);
	    out.tab(2).println("return this.%s;", columnMember);
	    out.tab(1).println("}");
	}
    }

    private void printValidationAnnotation(final JavaWriter out, final TypedElementDefinition<?> column) {// NOSONAR
	if (generateValidationAnnotations()) {
	    final DataTypeDefinition type = column.getType();
	    // [#5128] defaulted columns are nullable in Java
	    if (!column.getType().isNullable() &&
	            !column.getType().isDefaulted() &&
	            !column.getType().isIdentity()) {
		out.tab(1).println("@%s", out.ref("javax.validation.constraints.NotNull"));
	    }
	    if ("java.lang.String".equals(getJavaType(type))) {
		final int maxLength = type.getLength();
		final int minLength = getMinLength(column);
		if (minLength > 0 && maxLength > 0) {
		    out.tab(1).println("@%s(min = %s, max = %s)", out.ref("javax.validation.constraints.Size"), minLength, maxLength);
		} else if (maxLength > 0) {
		    out.tab(1).println("@%s(max = %s)", out.ref("javax.validation.constraints.Size"), maxLength);
		} else if (minLength > 0) {
		    out.tab(1).println("@%s(min = %s)", out.ref("javax.validation.constraints.Size"), minLength);
		}
	    }
	}
    }

    private int getMinLength(final TypedElementDefinition<?> column) {
	initCheckConstraints(column);
	// only dealing with syntax like
	// "((char_length((name)::text) > 2))"
	return findCheckConstraint(column).map(c -> {
	    final Matcher matcher = CHAR_LENGTH_CONSTRAINT.matcher(c.getCheckClause());
	    if (matcher.find()) {
		return Integer.parseInt(matcher.group(1));
	    }
	    return 0;
	}).orElse(0);
    }

    private void initCheckConstraints(final TypedElementDefinition<?> seedColumn) {
	if (checkConstraints == null) {
	    checkConstraints = seedColumn.getDatabase().getCheckConstraints(seedColumn.getSchema());
	}
    }

    private Optional<CheckConstraintDefinition> findCheckConstraint(final TypedElementDefinition<?> column) {
	return checkConstraints.stream().filter(c -> column.getQualifiedName().startsWith(c.getSchema().getName() + "." + c.getTable().getName()) && //
	        c.getCheckClause().contains(column.getName()))
	        .findFirst();
    }

    // --- Methods indicating lacking access to interna of JavaGenerator
    protected boolean isScala() {
	return false; // no access to private boolean 'scala'
    }

    // --- private methods copy/pasted from JavaGenerator
    private boolean printDeprecationIfUnknownType(final JavaWriter out, final String type) { // NOSONAR
	return printDeprecationIfUnknownType(out, type, 1);
    }

    private boolean printDeprecationIfUnknownType(final JavaWriter out, final String type, final int indentation) {// NOSONAR
	if (generateDeprecationOnUnknownTypes() && "java.lang.Object".equals(type)) {
	    out.tab(indentation).javadoc(
	            "@deprecated Unknown data type. " + "Please define an explicit {@link org.jooq.Binding} to specify how this " + "type should be handled.");
	    out.tab(indentation).println("@java.lang.Deprecated");
	    return true;
	}
	return false;
    }
}

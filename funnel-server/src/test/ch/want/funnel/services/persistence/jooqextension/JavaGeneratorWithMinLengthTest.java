/*
 * Created on 20 Apr 2018
 */
package ch.want.funnel.services.persistence.jooqextenstion;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.jooq.Name;
import org.jooq.util.CheckConstraintDefinition;
import org.jooq.util.DataTypeDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.JavaWriter;
import org.jooq.util.SchemaDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.TypedElementDefinition;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class JavaGeneratorWithMinLengthTest {
    @Test
    public void generatePojoGetter_columnWithoutConstraints_noSizeAnnotation() throws Exception {
	// arrange
	final JavaWriter writer = mock(JavaWriter.class);
	when(writer.tab(anyInt())).thenReturn(writer);
	final TypedElementDefinition<?> column = buildMockColumn(false, false);
	final JavaGeneratorWithMinLength testee = new JavaGeneratorWithMinLength();
	testee.setStrategy(new DefaultGeneratorStrategy());
	testee.setGenerateValidationAnnotations(true);
	// act
	testee.generatePojoGetter(column, 0, writer);
	// arrange
	verify(writer, never()).ref("javax.validation.constraints.Size");
    }

    @Test
    public void generatePojoGetter_columnWithCheckConstraint_minSizeIsSet() throws Exception {
	// arrange
	final JavaWriter writer = mock(JavaWriter.class);
	when(writer.tab(anyInt())).thenReturn(writer);
	final TypedElementDefinition<?> column = buildMockColumn(true, false);
	final JavaGeneratorWithMinLength testee = new JavaGeneratorWithMinLength();
	testee.setStrategy(new DefaultGeneratorStrategy());
	testee.setGenerateValidationAnnotations(true);
	// act
	testee.generatePojoGetter(column, 0, writer);
	// arrange
	verify(writer).ref("javax.validation.constraints.Size");
	verify(writer).println("@%s(min = %s)", null, 2);
    }

    @Test
    public void generatePojoGetter_columnWithMaxLength_maxSizeIsSet() throws Exception {
	// arrange
	final JavaWriter writer = mock(JavaWriter.class);
	when(writer.tab(anyInt())).thenReturn(writer);
	final TypedElementDefinition<?> column = buildMockColumn(false, true);
	final JavaGeneratorWithMinLength testee = new JavaGeneratorWithMinLength();
	testee.setStrategy(new DefaultGeneratorStrategy());
	testee.setGenerateValidationAnnotations(true);
	// act
	testee.generatePojoGetter(column, 0, writer);
	// arrange
	verify(writer).ref("javax.validation.constraints.Size");
	verify(writer).println("@%s(max = %s)", null, 128);
    }

    @Test
    public void generatePojoGetter_columnWithMaxLengthAndCheckConstraint_minAndMaxSizeIsSet() throws Exception {
	// arrange
	final JavaWriter writer = mock(JavaWriter.class);
	when(writer.tab(anyInt())).thenReturn(writer);
	final TypedElementDefinition<?> column = buildMockColumn(true, true);
	final JavaGeneratorWithMinLength testee = new JavaGeneratorWithMinLength();
	testee.setStrategy(new DefaultGeneratorStrategy());
	testee.setGenerateValidationAnnotations(true);
	// act
	testee.generatePojoGetter(column, 0, writer);
	// arrange
	verify(writer).ref("javax.validation.constraints.Size");
	verify(writer).println("@%s(min = %s, max = %s)", null, 2, 128);
    }

    private TypedElementDefinition<DefaultColumnDefinition> buildMockColumn(final boolean minSizeSet, final boolean maxSizeSet) {
	final DataTypeDefinition typeDefinition = buildDataTypeDefinition(minSizeSet, maxSizeSet);
	final Database database = typeDefinition.getDatabase();
	final TypedElementDefinition<DefaultColumnDefinition> columnDefinition = mock(TypedElementDefinition.class);
	when(columnDefinition.getType()).thenReturn(typeDefinition);
	when(columnDefinition.getName()).thenReturn("lastname");
	when(columnDefinition.getOutputName()).thenReturn("lastname");
	when(columnDefinition.getQualifiedName()).thenReturn("public.userlogin.lastname");
	when(columnDefinition.getDatabase()).thenReturn(database);
	return columnDefinition;
    }

    private DataTypeDefinition buildDataTypeDefinition(final boolean minSizeSet, final boolean maxSizeSet) {
	final Database database = buildDatabase();
	final SchemaDefinition schemaDefinition = buildSchemaDefinition(database);
	if (minSizeSet) {
	    buildCheckConstraints(schemaDefinition, database);
	} else {
	    final List<CheckConstraintDefinition> constraints = Lists.newArrayList();
	    when(database.getCheckConstraints(any(SchemaDefinition.class))).thenReturn(constraints);
	}
	final DataTypeDefinition typeDefinition = mock(DataTypeDefinition.class);
	when(typeDefinition.getDatabase()).thenReturn(database);
	when(typeDefinition.getSchema()).thenReturn(schemaDefinition);
	when(typeDefinition.getType()).thenReturn("character vararg");
	when(typeDefinition.getPrecision()).thenReturn(0);
	when(typeDefinition.getScale()).thenReturn(0);
	when(typeDefinition.getQualifiedUserType()).thenReturn(mock(Name.class));
	when(typeDefinition.getJavaType()).thenReturn("java.lang.String");
	if (maxSizeSet) {
	    when(typeDefinition.getLength()).thenReturn(128);
	} else {
	    when(typeDefinition.getLength()).thenReturn(0);
	}
	return typeDefinition;
    }

    private SchemaDefinition buildSchemaDefinition(final Database database) {
	return new SchemaDefinition(database, "public", "");
    }

    private Database buildDatabase() {
	final Database database = mock(Database.class);
	when(database.getCatalogs()).thenReturn(Lists.newArrayList());
	return database;
    }

    private void buildCheckConstraints(final SchemaDefinition schemaDefinition, final Database database) {
	final TableDefinition tableDefinition = mock(TableDefinition.class);
	when(tableDefinition.getName()).thenReturn("userlogin");
	final CheckConstraintDefinition lastNameConstraint = mock(CheckConstraintDefinition.class);
	when(lastNameConstraint.getTable()).thenReturn(tableDefinition);
	when(lastNameConstraint.getSchema()).thenReturn(schemaDefinition);
	when(lastNameConstraint.getCheckClause()).thenReturn("((char_length((lastname)::text) > 2))");
	//
	final List<CheckConstraintDefinition> constraints = Lists.newArrayList(lastNameConstraint);
	when(database.getCheckConstraints(any(SchemaDefinition.class))).thenReturn(constraints);
    }
}

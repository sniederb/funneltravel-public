/*
 * Created on 13 Apr 2018
 */
package ch.want.funnel.services.persistence.jooqextenstion;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.want.funnel.AbstractSpringTest;
import ch.want.funnel.model.tables.Customfield;
import ch.want.funnel.model.tables.records.CustomfieldRecord;

public class CustomDatatypeProcessorTest extends AbstractSpringTest {
    @Test
    public void parse_tableWithCustomDatatypes_enumToStringConvertedToOrdinal() throws Exception {
	final Path testfile = Paths.get(getClass().getResource("customfield.json").toURI());
	final CustomDatatypeProcessor<CustomfieldRecord> testee = new CustomDatatypeProcessor<>(Customfield.CUSTOMFIELD);
	// act
	final String jsonData = testee.parse(testfile, Charset.forName("UTF-8"));
	// arrange
	assertThat(jsonData, containsString("Cost center"));
	assertThat(jsonData, not(containsString("MANDATORY")));
    }
}

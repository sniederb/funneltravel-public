/*
 * Created on 13 Apr 2018
 */
package ch.want.funnel.services.persistence.jooqextenstion;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * jOOQ Version 3.10 has an issue serializing custom datatypes. These types are serialized using {@link Object#toString()}, but deserialized
 * using the database type. Thus serializing an enum which is stored as ordinal will yield a dataset which cannot be deserialized
 * back to the database.
 *
 * This class assumes a JSON structure:
 * <ul>
 * <li>"fields" holding an array of (eg)
 * <ul>
 * <li>"schema":"public"</li>
 * <li>"table":"customfield"</li>
 * <li>"name":"uuid"</li>
 * <li>"type":"UUID"</li>
 * </ul>
 * </li>
 * <li>"records" holding an array of arrays, where array indexes correspond to the "fields"</li>
 * </ul>
 *
 * @see https://github.com/jOOQ/jOOQ/issues/5670
 * @see https://github.com/jOOQ/jOOQ/issues/5673
 */
public class CustomDatatypeProcessor<R extends Record> {
    private final Table<R> table;
    private List<Converter<Object, Object>> converters;
    private List<Field<?>> fields;
    private JooqJsonFilePreprocessor jooqFileProcessor;

    public CustomDatatypeProcessor(final Table<R> table) {
	this.table = table;
    }

    public CustomDatatypeProcessor<R> setFileProcessor(final JooqJsonFilePreprocessor processor) {
	this.jooqFileProcessor = processor;
	return this;
    }

    public String parse(final Path jsonFile, final Charset charset) throws IOException {
	try {
	    final JSONObject jsonRoot = new JSONObject(new String(Files.readAllBytes(jsonFile), charset));
	    if (jooqFileProcessor != null) {
		jooqFileProcessor.processFields(jsonRoot.getJSONArray("fields"));
	    }
	    initializeConverters(jsonRoot.getJSONArray("fields"));
	    converterRecords(jsonRoot.getJSONArray("records"));
	    return jsonRoot.toString();
	} catch (final JSONException ex) {
	    throw new IOException(ex.getMessage(), ex);
	}
    }

    @SuppressWarnings("unchecked")
    private void initializeConverters(final JSONArray jsonArray) throws JSONException {
	converters = new ArrayList<>();
	fields = new ArrayList<>();
	for (int i = 0; i < jsonArray.length(); i++) {
	    final JSONObject field = jsonArray.getJSONObject(i);
	    final Field<?> jooqField = table.field(field.getString("name"));
	    fields.add(jooqField);
	    converters.add((Converter<Object, Object>) jooqField.getConverter());
	}
    }

    private void converterRecords(final JSONArray records) throws JSONException {
	for (int r = 0; r < records.length(); r++) {
	    final JSONArray fields = records.getJSONArray(r);
	    if (jooqFileProcessor != null) {
		jooqFileProcessor.processRecord(fields);
	    }
	    for (int f = 0; f < fields.length(); f++) {
		convert(fields, f);
	    }
	}
    }

    private void convert(final JSONArray fields, final int index) throws JSONException {
	final Converter<Object, Object> converter = converters.get(index);
	final Class<?> toClass = converter.toType();
	final Class<?> fromClass = converter.fromType();
	if (!fromClass.equals(toClass)) {
	    final String toStringResult = fields.getString(index);
	    final String dbTypeString = converter.to(coerce(toStringResult, toClass)).toString();
	    fields.put(index, dbTypeString);
	}
    }

    private <T> T coerce(final String value, final Class<T> toClass) {
	if (toClass.isEnum()) {
	    for (final T enumConstant : toClass.getEnumConstants()) {
		if (enumConstant.toString().equalsIgnoreCase(value)) {
		    return enumConstant;
		}
	    }
	    throw new IllegalArgumentException("Unknown value " + value + " on enum " + toClass);
	}
	try {
	    return toClass.getConstructor(String.class).newInstance(value);
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException// NOSONAR
	        | SecurityException e) { // NOSONAR
	    // no-op, conversion via String c'tor apparently doesn't work
	}
	throw new IllegalArgumentException("Don't know how to convert " + value + " to " + toClass);
    }

    public List<Field<?>> fields() {
	return fields;
    }
}

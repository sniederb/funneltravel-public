/*
 * Created on 13 Apr 2018
 */
package ch.want.funnel.services.persistence.jooqextenstion;

import org.json.JSONArray;
import org.json.JSONException;

public interface JooqJsonFilePreprocessor {
    void processFields(JSONArray fields) throws JSONException;

    void processRecord(JSONArray record) throws JSONException;
}

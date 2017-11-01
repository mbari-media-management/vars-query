package org.mbari.m3.vars.query.gson;

import com.google.gson.*;
import org.mbari.vcr4j.time.Timecode;

import java.lang.reflect.Type;

/**
 * @author Brian Schlining
 * @since 2016-07-11T16:32:00
 */
public class TimecodeConverter implements JsonSerializer<Timecode>, JsonDeserializer<Timecode> {

    @Override
    public Timecode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Timecode(json.getAsString());
    }

    @Override
    public JsonElement serialize(Timecode src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}

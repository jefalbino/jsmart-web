/*
 * JSmart Framework - Java Web Development Framework
 * Copyright (c) 2015, Jeferson Albino da Silva, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
*/

package com.jsmartframework.web.manager;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

class JsonConverter {

    private static final Logger LOGGER = Logger.getLogger(JsonConverter.class.getPackage().getName());

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String LOCAL_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private JsonConverter() {}

    static class LocalDateTimeTypeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String jsonDate = jsonElement.getAsString();
            String datePattern = DATE_PATTERN;
            if (!jsonDate.contains("Z")) {
                datePattern = LOCAL_DATE_PATTERN;
            }
            try {
                return LocalDateTime.parse(jsonDate, DateTimeFormatter.ofPattern(datePattern));
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to parse date [" + jsonDate + "] with pattern [" + datePattern + "]");
                throw ex;
            }
        }
    }

    static class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

        @Override
        public JsonElement serialize(DateTime dateTime, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.toString());
        }

        @Override
        public DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String jsonDate = jsonElement.getAsString();
            String datePattern = DATE_PATTERN;
            if (!jsonDate.contains("Z")) {
                datePattern = LOCAL_DATE_PATTERN;
            }
            try {
                return DateTimeFormat.forPattern(datePattern).parseDateTime(jsonDate);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to parse date [" + jsonDate + "] with pattern [" + datePattern + "]");
                throw ex;
            }
        }
    }

    static class DateTypeConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        public JsonElement serialize(Date date, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(new SimpleDateFormat(DATE_PATTERN).format(date));
        }

        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String jsonDate = jsonElement.getAsString();
            String datePattern = DATE_PATTERN;
            if (!jsonDate.contains("Z")) {
                datePattern = LOCAL_DATE_PATTERN;
            }
            try {
                return new SimpleDateFormat(datePattern).parse(jsonDate);
            } catch (ParseException e) {
                LOGGER.log(Level.SEVERE, "Failed to parse date [" + jsonDate + "] with pattern [" + datePattern + "]");
                throw new JsonParseException(e);
            }
        }
    }
}

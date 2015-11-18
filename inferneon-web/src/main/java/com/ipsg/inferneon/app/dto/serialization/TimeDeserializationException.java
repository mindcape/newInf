package com.ipsg.inferneon.app.dto.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * Custom exception thrown when it was not possible to deserialize a time field,
 * @see com.ipsg.inferneon.app.dto.serialization.CustomTimeDeserializer
 *
 */
public class TimeDeserializationException extends JsonProcessingException {

    protected TimeDeserializationException(Throwable rootCause) {
        super(rootCause);
    }

}

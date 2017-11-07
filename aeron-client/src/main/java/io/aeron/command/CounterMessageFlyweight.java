/*
 * Copyright 2014-2017 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.command;

import org.agrona.DirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_LONG;

/**
 * Message to denote a new counter.
 *
 * @see ControlProtocolEvents
 * <pre>
 *   0                   1                   2                   3
 *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                         Correlation ID                        |
 *  |                                                               |
 *  +---------------------------------------------------------------+
 *  |                        Counter Type ID                        |
 *  +---------------------------------------------------------------+
 *  |                           Key Length                          |
 *  +---------------------------------------------------------------+
 *  |                           Key Buffer                         ...
 * ...                                                              |
 *  +---------------------------------------------------------------+
 *  |                          Label Length                         |
 *  +---------------------------------------------------------------+
 *  |                          Label (ASCII)                       ...
 * ...                                                              |
 *  +---------------------------------------------------------------+
 * </pre>
 */
public class CounterMessageFlyweight extends CorrelatedMessageFlyweight
{
    private static final int COUNTER_TYPE_ID_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + SIZE_OF_LONG;
    private static final int KEY_LENGTH_OFFSET = COUNTER_TYPE_ID_FIELD_OFFSET + SIZE_OF_INT;

    /**
     * return type id field
     *
     * @return type id field
     */
    public int typeId()
    {
        return buffer.getInt(offset + COUNTER_TYPE_ID_FIELD_OFFSET);
    }

    /**
     * set counter type id field
     *
     * @param typeId field value
     * @return flyweight
     */
    public CounterMessageFlyweight counterTypeId(final long typeId)
    {
        buffer.putLong(offset + COUNTER_TYPE_ID_FIELD_OFFSET, typeId);

        return this;
    }

    public int keyBufferOffset()
    {
        return KEY_LENGTH_OFFSET + SIZE_OF_INT;
    }

    public int keyBufferLength()
    {
        return buffer.getInt(offset + KEY_LENGTH_OFFSET);
    }

    public CounterMessageFlyweight keyBuffer(
        final DirectBuffer keyBuffer,
        final int keyOffset,
        final int keyLength)
    {
        buffer.putBytes(keyBufferOffset(), keyBuffer, keyOffset, keyLength);

        return this;
    }

    public int labelBufferOffset()
    {
        return labelOffset() + SIZE_OF_INT;
    }

    public int labelBufferLength()
    {
        return buffer.getInt(offset + labelOffset());
    }

    public CounterMessageFlyweight labelBuffer(
        final DirectBuffer labelBuffer,
        final int labelOffset,
        final int labelLength)
    {
        buffer.putBytes(labelBufferOffset(), labelBuffer, labelOffset, labelLength);

        return this;
    }

    /**
     * Get the length of the current message
     * <p>
     * NB: must be called after the data is written in order to be accurate.
     *
     * @return the length of the current message
     */
    public int length()
    {
        final int labelOffset = labelOffset();
        return labelOffset + buffer.getInt(offset + labelOffset) + SIZE_OF_INT;
    }

    private int labelOffset()
    {
        return KEY_LENGTH_OFFSET + buffer.getInt(offset + KEY_LENGTH_OFFSET) + SIZE_OF_INT;
    }
}

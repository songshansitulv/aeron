package io.aeron.agent;

import org.agrona.MutableDirectBuffer;

/**
 * Cluster events and codecs for encoding/decoding events recorded to the {@link EventConfiguration#EVENT_RING_BUFFER}.
 */
public enum ClusterEventCode
{
    ELECTION_STATE_CHANGE(0, ClusterEventDissector::electionStateChange);

    static final int EVENT_CODE_TYPE = EventCodeType.CLUSTER.getTypeCode();
    private static final int MAX_ID = 63;
    private static final ClusterEventCode[] EVENT_CODE_BY_ID = new ClusterEventCode[MAX_ID];

    private final long tagBit;
    private final int id;
    private final DissectFunction<ClusterEventCode> dissector;

    static
    {
        for (final ClusterEventCode code : ClusterEventCode.values())
        {
            final int id = code.id();
            if (null != EVENT_CODE_BY_ID[id])
            {
                throw new IllegalArgumentException("id already in use: " + id);
            }

            EVENT_CODE_BY_ID[id] = code;
        }
    }

    ClusterEventCode(final int id, final DissectFunction<ClusterEventCode> dissector)
    {
        this.id = id;
        this.tagBit = 1L << id;
        this.dissector = dissector;
    }

    static ClusterEventCode get(final int eventCodeId)
    {
        return EVENT_CODE_BY_ID[eventCodeId];
    }

    public int id()
    {
        return id;
    }

    public long tagBit()
    {
        return tagBit;
    }

    public void decode(final MutableDirectBuffer buffer, final int offset, final StringBuilder builder)
    {
        dissector.dissect(this, buffer, offset, builder);
    }

    public static boolean isEnabled(final ClusterEventCode code, final long mask)
    {
        return ((mask & code.tagBit()) == code.tagBit());
    }
}
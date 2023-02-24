package com.ufutao.account.admin.entity;

/**
 * @author lutong
 */
public class IdBuilder {
    private final long[] masks;
    private final int[] locations;

    private long lastTime;
    private long builderId;
    private long incId;

    private IdBuilder(int[] sectionLengths) {
        this.locations = new int[sectionLengths.length];
        this.masks = new long[sectionLengths.length];

        int bitUsed = 0;
        for (int i = 0; i < sectionLengths.length; i++) {
            bitUsed += sectionLengths[i];
            this.locations[i] = 63 - bitUsed;

            long mask = 0;
            for (int j = 1; j <= sectionLengths[i]; j++) {
                mask = mask | (1L << j);
            }
            this.masks[i] = mask;
        }
    }

    public IdBuilder(long builderId) {
        this(new int[]{41, 10, 12});
        this.builderId = builderId;
    }

    private long buildId(long[] values) {
        long id = 0;
        for (int i = 0; i < values.length; i++) {
            id = ((values[i] & masks[i]) << locations[i]) | id;
        }
        return id & Long.MAX_VALUE;
    }

    public long getLastTime() {
        return lastTime;
    }

    public long createId() {
        long now = System.currentTimeMillis();
        long inc = 0;
        synchronized (this) {
            if (now > lastTime) {
                this.lastTime = now;
                this.incId = 0;
            } else if (now == lastTime) {
                inc = this.incId;
                this.incId=this.incId + 1;
            } else {
                return -1;
            }
        }
        return this.buildId(new long[]{now, builderId, inc});
    }
}

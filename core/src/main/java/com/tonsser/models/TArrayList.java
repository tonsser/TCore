package com.tonsser.models;

import com.tonsser.utils.TUtils;
import com.tonsser.utils.math.TUnits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class TArrayList<T> extends ArrayList<T> implements Serializable {
    private long lastLoadedUnix;
    private long cacheTime = 5 * TUnits.MIN_IN_MS;
    private String lastModified;

    public String getLastModified() {
        return lastModified;
    }

    public void resetLastLoadedUnix() {
        lastLoadedUnix = 0;
    }

    /**
     * Set the cache time, used for isOutdated
     *
     * @param cacheTime
     */
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    /**
     * Get the cache time, used for isOutdated
     *
     * @return
     */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * Will return if the cache time is exceeded or not
     *
     * @return
     */
    public boolean isOutdated() {
        return TUtils.isOutdated(cacheTime, lastLoadedUnix);
    }

    /**
     * Reset the cache time
     */
    public void setLastLoadedUnix() {
        lastLoadedUnix = System.currentTimeMillis();
    }

    /**
     * Resetting the cache time and super.add
     */
    @Override
    public void add(int index, T object) {
        setLastLoadedUnix();
        super.add(index, object);
    }

    @Override
    public boolean add(T object) {
        setLastLoadedUnix();
        return super.add(object);
    }

    /**
     * Resetting the cache time and super.addAll
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        setLastLoadedUnix();
        return super.addAll(collection);
    }

    /**
     * Resetting the cache time and super.addAll
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        setLastLoadedUnix();
        return super.addAll(index, collection);
    }

    @Override
    public void clear() {
        super.clear();
        lastLoadedUnix = 0;
    }

    /**
     * Clearing list and setting it
     *
     * @param collection
     */
    public void setList(Collection<? extends T> collection) {
        clear();
        addAll(collection);
    }

    public void setList(Collection<? extends T> collection, String lastModified) {
        setList(collection);
        this.lastModified = lastModified;
    }

    public boolean doesIndexExist(int index) {
        return (index < size() && index >= 0) ? true : false;
    }
}

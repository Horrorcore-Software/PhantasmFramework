package com.horrorcoresoftware.core.resource;

/**
 * Holds a resource and its reference count.
 * @param <T> The type of resource being held
 */
class ResourceEntry<T> {
    private final T resource;
    private int refCount;

    /**
     * Creates a new resource entry.
     * @param resource The resource to hold
     */
    public ResourceEntry(T resource) {
        this.resource = resource;
        this.refCount = 1;
    }

    public T getResource() { return resource; }

    public synchronized int incrementRefCount() {
        return ++refCount;
    }

    public synchronized int decrementRefCount() {
        return --refCount;
    }

    public int getRefCount() { return refCount; }
}
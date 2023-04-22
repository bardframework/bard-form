package org.bardframework.form.model.filter;

/**
 * Filter class for Comparable types, where less than / greater than / etc relations could be interpreted. It can be
 * added to a criteria class as a member, to support the following query parameters:
 * <pre>
 *      fieldName.equals=42
 *      fieldName.notEquals=42
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.in=43,42
 *      fieldName.notIn=43,42
 *      fieldName.from=42
 *      fieldName.to=44
 * </pre>
 * Due to problems with the type conversions, the descendant classes should be used, where the generic type parameter
 * is materialized.
 *
 * @param <T> the type of filter.
 * @see NumberRangeFilter
 * @see LocalDateFilter
 */
public abstract class RangeFilter<T, F extends RangeFilter<T, F>> extends Filter<T, F> {

    private T from;
    private T to;

    /**
     * <p>Getter for the field <code>from</code>.</p>
     *
     * @return a T object.
     */
    public T getFrom() {
        return from;
    }

    /**
     * <p>Setter for the field <code>from</code>.</p>
     *
     * @param from a T object.
     * @return a {@link RangeFilter} object.
     */
    public F setFrom(T from) {
        this.from = from;
        return (F) this;
    }

    /**
     * <p>Getter for the field <code>to</code>.</p>
     *
     * @return a T object.
     */
    public T getTo() {
        return to;
    }

    /**
     * <p>Setter for the field <code>to</code>.</p>
     *
     * @param to a T object.
     * @return a {@link RangeFilter} object.
     */
    public F setTo(T to) {
        this.to = to;
        return (F) this;
    }
}

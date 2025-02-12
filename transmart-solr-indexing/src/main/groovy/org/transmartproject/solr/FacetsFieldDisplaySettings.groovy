package org.transmartproject.solr

import org.springframework.core.Ordered

/**
 * Display properties for fields.
 */
interface FacetsFieldDisplaySettings extends Ordered, Comparable<FacetsFieldDisplaySettings> {
    public final static int BROWSE_FIELDS_DEFAULT_PRECEDENCE = 0

    String getDisplayName()
    boolean isHideFromListings()
}

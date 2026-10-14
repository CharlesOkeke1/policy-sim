package org.cokeke.policyengine.models;


/** Independent random streams. Append only: reordering changes every seeded run. */
public enum Stream {
    RESOURCE,
    EVENTS,
    ELECTIONS,
    AI
}

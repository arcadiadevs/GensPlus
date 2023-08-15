package xyz.arcadiadevs.gensplus.models.events;

/**
 * The ActiveEvent record represents an active event in GensPlus.
 * It stores information about the event, its start time, and end time.
 */
public record ActiveEvent(Event event, long startTime, long endTime) {

}
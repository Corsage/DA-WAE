package jc.edu.corsage.dawae.mapquest.collections;

/**
 * MapQuest - TurnType
 */

public enum TurnType {
    STRAIGHT(0),
    SLIGHT_RIGHT(1),
    RIGHT(2),
    SHARP_RIGHT(3),
    REVERSE(4),
    SHARP_LEFT(5),
    LEFT(6),
    SLIGHT_LEFT(7),
    RIGHT_UTURN(8),
    LEFT_UTURN(9),
    RIGHT_MERGE(10),
    LEFT_MERGE(11),
    RIGHT_ON_RAMP(12),
    LEFT_ON_RAMP(13),
    RIGHT_OFF_RAMP(14),
    LEFT_OFF_RAMP(15),
    RIGHT_FORK(16),
    LEFT_FORK(17),
    STRAIGHT_FORK(18),
    TAKE_TRANSIT(19),
    TRANSFER_TRANSIT(20),
    PORT_TRANSIT(21),
    ENTER_TRANSIT(22),
    EXIT_TRANSIT(23);

    private final int id;

    TurnType(int id) {
        this.id = id;
    }

    public int getValue() {
        return this.id;
    }
}

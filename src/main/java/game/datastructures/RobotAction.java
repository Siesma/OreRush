package game.datastructures;

/**
 * Emum containing the Robot Actions. This means the actions the robot can perform, for example to dig, which means
 * it sends a packet to the server asking if the is any ore on the cell that was dug on, and then figuratively dig it up
 * and store it the robots inventory. The other action would be to move where it isn't actually moving but teleporting
 * to a different cell, another action listed in this enum is the request radar where the action of the robot is to
 * request the radar. The is requested by the action of the robot to the sever upon which the request of a radar is
 * approved if the robot is on the column of the game map and declined otherwise. The request Trap action is an action
 * which requests a trap. This means the robot performs an action and the result of the action is obtain a trap or not
 * that is the definition of request. The request is granted by the holy server if the robot is on the first column.
 * Another action of the robot ist to wait, we aknowledge waiting is not an action in the strict sense of the therm,
 * well it is, but somebody holding the power could say nein nein nein this is not an action, thus this object
 * in the Robot action Enum will be referred as the object. The object can be called as an robot action and allows the
 * robot to perform no action while still performing an action.
 */
public enum RobotAction {
    Move(), Dig(), Wait(), RequestRadar(), RequestTrap();

    /**
     * Enum of the different robot actions
     */
    RobotAction() {

    }

}

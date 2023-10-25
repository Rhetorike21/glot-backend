package rhetorike.glot.domain._3writing.service;

public interface Movable {
    int getSequence();
    void increaseSequence();
    void decreaseSequence();
    void setSequence(int sequence);
}

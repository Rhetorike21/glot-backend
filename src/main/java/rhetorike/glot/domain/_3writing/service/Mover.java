package rhetorike.glot.domain._3writing.service;

import java.util.List;

public interface Mover<T extends Movable> {
    void move(T target, T destination, List<T> elements);
}

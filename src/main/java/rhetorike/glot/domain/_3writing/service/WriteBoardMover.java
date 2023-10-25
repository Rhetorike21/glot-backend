package rhetorike.glot.domain._3writing.service;

import org.springframework.stereotype.Service;
import rhetorike.glot.domain._3writing.entity.WritingBoard;

import java.util.Comparator;
import java.util.List;

@Service
public class WriteBoardMover implements Mover<WritingBoard> {

    @Override
    public void move(WritingBoard target, WritingBoard dest, List<WritingBoard> boards) {
        boards.sort(Comparator.comparing(WritingBoard::getSequence));
        if (target.getSequence() < dest.getSequence()) {
            moveUpper(target, dest, boards);
            return;
        }
        moveLower(target, dest, boards);
    }

    public void moveUpper(WritingBoard target, WritingBoard dest, List<WritingBoard> boards) {
        int value = dest.getSequence();
        int destIdx = boards.indexOf(dest);
        int targetIdx = boards.indexOf(target);
        List<WritingBoard> subList = boards.subList(targetIdx + 1, destIdx + 1);
        for (WritingBoard writingBoard : subList) {
            writingBoard.decreaseSequence();
        }
        target.setSequence(value);
    }

    public void moveLower(WritingBoard target, WritingBoard dest, List<WritingBoard> boards) {
        int value = dest.getSequence();
        int destIdx = boards.indexOf(dest);
        int targetIdx = boards.indexOf(target);
        List<WritingBoard> subList = boards.subList(destIdx, targetIdx);
        for (WritingBoard writingBoard : subList) {
            writingBoard.increaseSequence();
        }
        target.setSequence(value);
    }
}

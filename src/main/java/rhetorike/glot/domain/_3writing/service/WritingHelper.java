package rhetorike.glot.domain._3writing.service;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public interface WritingHelper {
    @AllArgsConstructor
    enum Type {
        PROGRESS("progress", () -> "그런데"),
        REVERSE("reverse", () -> "그러나"),
        CONCLUSION("conclusion", () -> "따라서");
        private final String name;
        private final Supplier<String> param;

        public static Type findByName(String name) {
            return Arrays.stream(values())
                    .filter(type -> type.name.equals(name))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
        }
        public String param(){
            return this.param.get();
        }
    }

    List<String> help(Type type, String sentence);
}

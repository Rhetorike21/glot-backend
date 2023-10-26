package rhetorike.glot.domain._3writing.service;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.function.Supplier;

public interface WritingHelper {
    @AllArgsConstructor
    enum Type {
        PROGRESS("progress", () -> ""),
        REVERSE("reverse", () -> "reverse"),
        CONCLUSION("conclusion", () -> "");
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

    String help(Type type, String sentence);
}

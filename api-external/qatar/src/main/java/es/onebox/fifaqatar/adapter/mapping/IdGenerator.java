package es.onebox.fifaqatar.adapter.mapping;

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    private IdGenerator() {}

    public static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
    }
}

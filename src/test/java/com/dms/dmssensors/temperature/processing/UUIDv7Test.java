package com.dms.dmssensors.temperature.processing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

class UUIDv7Test {

    @Test
    void shouldGenerateUUIDv7() {
        UUID uuid = IdGenerator.generateTimeBaseUUID();
        OffsetDateTime expectedTime = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime uuidTime = UUIDv7Utils.extractTimestampFromUUIDv7(uuid).truncatedTo(ChronoUnit.MINUTES);

        Assertions.assertThat(uuidTime).isEqualTo(expectedTime);
    }
}

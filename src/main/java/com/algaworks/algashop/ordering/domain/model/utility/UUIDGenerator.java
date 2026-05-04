package com.algaworks.algashop.ordering.domain.model.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public class UUIDGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator();

    private UUIDGenerator() {
    }

    public static UUID generateTimeBasedUUID(){
        return timeBasedEpochRandomGenerator.generate();
    }
}

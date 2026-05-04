package com.algaworks.algashop.ordering.domain.model.utility;

import io.hypersistence.tsid.TSID;

public class TSIDGenerator {

    private static final TSID.Factory tsidFactory = TSID.Factory.INSTANCE;

    public static TSID generateTSID() {
        return tsidFactory.generate();
    }

}

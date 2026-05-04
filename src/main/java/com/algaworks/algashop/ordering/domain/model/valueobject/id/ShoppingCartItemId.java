package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.TSIDGenerator;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record ShoppingCartItemId(TSID value) {

    public ShoppingCartItemId {
        Objects.requireNonNull(value);
    }

    public ShoppingCartItemId(Long value){
        this(TSID.from(value));
    }

    public ShoppingCartItemId(String value){
        this(TSID.from(value));
    }

    public ShoppingCartItemId(){
        this(TSIDGenerator.generateTSID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

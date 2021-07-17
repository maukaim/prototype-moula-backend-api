package com.maukaim.cryptohub.commons.order;

import com.maukaim.cryptohub.commons.parameter.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Getter
@NoArgsConstructor
public class OrderParameter extends Parameter {
    private Boolean shouldBeFilled;

    public boolean isFilled() {
        return !Objects.isNull(this.getValue()) && !this.getValue().isEmpty();
    }
}

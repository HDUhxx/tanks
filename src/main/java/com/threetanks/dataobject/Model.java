package com.threetanks.dataobject;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "model")
public class Model {

    @Id
    @KeySql(genId = GenerateUUID.class)
    private String modelId;

    @NotNull
    private Double k1;
    @NotNull
    private Double k2;
    @NotNull
    private Double a1;
    @NotNull
    private Double a2;

}

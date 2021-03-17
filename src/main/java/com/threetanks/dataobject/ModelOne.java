package com.threetanks.dataobject;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "modelone")
public class ModelOne {

    @Id
    @KeySql(genId = GenerateUUID.class)
    private String modelId;

    @NotNull
    private Double k1;
    @NotNull
    private Double a1;
}

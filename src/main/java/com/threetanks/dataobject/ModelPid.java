package com.threetanks.dataobject;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "modelpid")
public class ModelPid {

    @Id
    @KeySql(genId = GenerateUUID.class)
    private String pidId;
    @NotNull
    private String modelId;
    @NotNull
    private Double kp;
    @NotNull
    private Double ki;
    @NotNull
    private Double kd;
    @NotNull
    private Double t;
    @NotNull
    private Double r;

    private Double disturb;
}

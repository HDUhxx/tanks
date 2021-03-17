package com.threetanks.dataobject;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "modelchuanpid")
public class ModelChuanPid {

    @Id
    @KeySql(genId = GenerateUUID.class)
    private String chuanPidId;
    @NotNull
    private String modelId;
    @NotNull
    private Double kp;
    @NotNull
    private Double ki;
    @NotNull
    private Double kd;
    @NotNull
    private Double kp1;
    @NotNull
    private Double ki1;
    @NotNull
    private Double t;
    @NotNull
    private Double r;

    private Double disturb;
}

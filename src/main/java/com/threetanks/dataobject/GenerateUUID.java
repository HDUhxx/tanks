package com.threetanks.dataobject;

import tk.mybatis.mapper.genid.GenId;

import java.util.UUID;

public class GenerateUUID implements GenId {
    public String genId(String s, String s1) {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

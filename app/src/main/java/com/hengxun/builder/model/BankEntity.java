package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/6/24.
 */
public class BankEntity implements Serializable {
    private List<BankList> bank;

    public List<BankList> getBank() {
        return bank;
    }

    public void setBank(List<BankList> bank) {
        this.bank = bank;
    }

    public class BankList implements Serializable {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

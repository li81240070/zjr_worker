package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/3/30.
 */
public class RegionList implements Serializable{

    /**
     * code : 200
     * time : 1459342816114
     * message : null
     * dataMap : {"postCodes":[{"code":130000,"name":"河北省","sub":[{"code":131000,"name":"廊坊市","sub":[{"code":131024,"name":"香河县","sub":null}]}]},{"code":210000,"name":"辽宁省","sub":[{"code":210200,"name":"大连市","sub":[{"code":210202,"name":"中山区","sub":null},{"code":210204,"name":"沙河口区","sub":null}]},{"code":210300,"name":"鞍山市","sub":[{"code":210321,"name":"台安县","sub":null}]}]},{"code":110000,"name":"北京市","sub":[{"code":110100,"name":"市辖区","sub":[{"code":110111,"name":"房山区","sub":null}]}]}]}
     */

    private int code;
    private long time;
    private Object message;
    private DataMapEntity dataMap;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public DataMapEntity getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMapEntity dataMap) {
        this.dataMap = dataMap;
    }

    public static class DataMapEntity implements Serializable{
        /**
         * code : 130000
         * name : 河北省
         * sub : [{"code":131000,"name":"廊坊市","sub":[{"code":131024,"name":"香河县","sub":null}]}]
         */

        private List<PostCodesEntity> postCodes;

        public List<PostCodesEntity> getPostCodes() {
            return postCodes;
        }

        public void setPostCodes(List<PostCodesEntity> postCodes) {
            this.postCodes = postCodes;
        }

        public static class PostCodesEntity {
            private int code;
            private String name;
            /**
             * code : 131000
             * name : 廊坊市
             * sub : [{"code":131024,"name":"香河县","sub":null}]
             */

            private List<SubEntity> sub;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<SubEntity> getSub() {
                return sub;
            }

            public void setSub(List<SubEntity> sub) {
                this.sub = sub;
            }

            public static class SubEntity implements Serializable{
                private int code;
                private String name;
                /**
                 * code : 131024
                 * name : 香河县
                 * sub : null
                 */

                private List<SubEntity> sub;

                public int getCode() {
                    return code;
                }

                public void setCode(int code) {
                    this.code = code;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<SubEntity> getSub() {
                    return sub;
                }

                public void setSub(List<SubEntity> sub) {
                    this.sub = sub;
                }

                public static class SubListEntity implements Serializable{
                    private int code;
                    private String name;
                    private Object sub;

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public Object getSub() {
                        return sub;
                    }

                    public void setSub(Object sub) {
                        this.sub = sub;
                    }
                }
            }
        }
    }
}

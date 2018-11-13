package com.itdream.wll.prient.bean;

/**
 * Created by wll on 2018/11/9.
 */

public class UserInfo {


    /**
     * result : 200
     * data : {"age":11,"id":1,"loginname":"王丽丽","password":"111111","phone":"13661148369","sex":"女"}
     */

    private String result;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * age : 11
         * id : 1
         * loginname : 王丽丽
         * password : 111111
         * phone : 13661148369
         * sex : 女
         */

        private int age;
        private int id;
        private String loginname;
        private String password;
        private String phone;
        private String sex;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLoginname() {
            return loginname;
        }

        public void setLoginname(String loginname) {
            this.loginname = loginname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}

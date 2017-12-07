package com.github.lahahana.xtrpc.common.constant;

public class Constraints {

    public static final String PROTOCOL = "protocol";

    public static final String APPLICATION = "application";

    public static final String SERVICE = "service";

    public static final String REFERENCE = "reference";

    public static final String  REGISTRY= "registry";

    public static final int STATUS_OK = 200;

    public static final int STATUS_ERROR = 500;

    public static final int STATUS_METHOD_ERROR = 501;


    public static enum Protocol {

        XT("xt");

        private String value;

        private Protocol(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }


    public static enum Serialization {

        JAVA("java"), KRYO("kryo");

        private String value;

        private Serialization(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    public static enum Transporter {

        NETTY("netty");

        private String value;

        private Transporter(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

}

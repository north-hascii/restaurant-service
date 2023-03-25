package service.util.errors;

public class MyError {
        public String err_type;
        public String err_entity;
        public String err_field;

        public MyError(String err_type, String err_entity, String err_field) {
                this.err_type = err_type;
                this.err_entity = err_entity;
                this.err_field = err_field;
        }

        @Override
        public String toString() {
                return "Error{" +
                        "err_type='" + err_type + '\'' +
                        ", err_entity='" + err_entity + '\'' +
                        ", err_field='" + err_field + '\'' +
                        '}';
        }
}

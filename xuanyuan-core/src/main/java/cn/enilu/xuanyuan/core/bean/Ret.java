package cn.enilu.xuanyuan.core.bean;

import lombok.Data;

/**
 * Created  on  2018/7/11 0011
 * Ret
 *
 * @author enilu
 */
@Data
public class Ret<T> {

    private String code;
    private String msg;
    private T data;
    private boolean success;

    public Ret() {

    }

    public Ret(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = "0000".equals(code);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("'code':").append(code).append(",");
        builder.append("'msg':").append(msg).append(",");
        builder.append("'success':").append(success).append(",");
        builder.append("}");
        return builder.toString();
    }
}

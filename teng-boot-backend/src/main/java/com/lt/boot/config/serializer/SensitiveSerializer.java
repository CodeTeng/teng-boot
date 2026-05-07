package com.lt.boot.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.lt.boot.annotation.Sensitive;

import java.io.IOException;

/**
 * 敏感数据序列化器
 * 根据类型对敏感数据做掩码处理
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType type;

    public SensitiveSerializer() {
    }

    public SensitiveSerializer(SensitiveType type) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(desensitize(value, type));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            Sensitive sensitive = property.getAnnotation(Sensitive.class);
            if (sensitive != null) {
                return new SensitiveSerializer(sensitive.value());
            }
        }
        return this;
    }

    /**
     * 根据类型执行脱敏
     */
    public static String desensitize(String value, SensitiveType type) {
        if (value == null) {
            return null;
        }
        if (type == null) {
            return value;
        }
        return switch (type) {
            case PHONE -> desensitizePhone(value);
            case EMAIL -> desensitizeEmail(value);
            case ID_CARD -> desensitizeIdCard(value);
        };
    }

    /**
     * 手机号脱敏：138****1234
     */
    private static String desensitizePhone(String phone) {
        if (phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏：t***@example.com
     */
    private static String desensitizeEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * 身份证号脱敏：320***********1234
     */
    private static String desensitizeIdCard(String idCard) {
        if (idCard.length() < 10) {
            return idCard;
        }
        return idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4);
    }
}

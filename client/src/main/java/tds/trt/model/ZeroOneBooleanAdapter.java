package tds.trt.model;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ZeroOneBooleanAdapter extends XmlAdapter<String, Boolean> {
    public ZeroOneBooleanAdapter() {
    }

    public Boolean unmarshal(String v) {
        return v == null ? null : DatatypeConverter.parseBoolean(v);
    }

    public String marshal(Boolean v) {
        if (v == null) {
            return null;
        } else {
            return v ? "1" : "0";
        }
    }
}

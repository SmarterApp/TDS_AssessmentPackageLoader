package tds.support.tool.security.permission.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Permission REST service response model
 * This class represents a REST web service response.
 */
public class Response<T> implements Serializable {

    private String status;
    private String message;
    private List<T> value;

    @JsonCreator
    public Response(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("value") List<T> value) {
        this.status = status;
        this.message = message;
        this.value = value;
    }

    /**
     * The status of the response
     * @return "SUCCESS" when successful and "FAILURE" when unsuccessful
     */
    public String getStatus() {
        return status;
    }

    /**
     * The error message for a failed request
     * @return reason for failure status. This field will be null if the request was successful.
     */
    public String getMessage() {
        return message;
    }

    /**
     * The service method payload
     * @return request payload
     */
    public List<T> getValue() {
        return value;
    }

    public static final class Status {

        private Status(){}

        public static final String SUCCESS = "SUCCESS";
        public static final String FAILURE = "FAILURE";

    }
}
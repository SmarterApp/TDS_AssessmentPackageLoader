package tds.support.job;

import java.util.List;

public class Step {
    private String description;
    private List<Error> errors;
    private Status status;

    public Step(){}

    public Step(String description, Status status) {
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(final List<Error> errors) {
        this.errors = errors;
    }

}

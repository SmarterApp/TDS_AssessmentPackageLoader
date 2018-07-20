package tds.support.job;

public class ScoringValidationReport {
    //TODO: Remove this property - only added because Spring REST cannot serialize blank classes
    private String removeThis;

    public ScoringValidationReport() {
    }


    public String getRemoveThis() {
        return removeThis;
    }

    public void setRemoveThis(final String removeThis) {
        this.removeThis = removeThis;
    }
}

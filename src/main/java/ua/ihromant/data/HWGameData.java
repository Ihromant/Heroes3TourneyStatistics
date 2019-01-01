package ua.ihromant.data;

public class HWGameData {
    private String approveLink;
    private String discardLink;
    private String discardedLink;
    private boolean isApproved;
    private boolean isDiscarded;
    private String report;

    public String getApproveLink() {
        return approveLink;
    }

    public void setApproveLink(String approveLink) {
        this.approveLink = approveLink;
    }

    public String getDiscardLink() {
        return discardLink;
    }

    public void setDiscardLink(String discardLink) {
        this.discardLink = discardLink;
    }

    public String getDiscardedLink() {
        return discardedLink;
    }

    public void setDiscardedLink(String discardedLink) {
        this.discardedLink = discardedLink;
    }

    public boolean isIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isIsDiscarded() {
        return isDiscarded;
    }

    public void setIsDiscarded(boolean discarded) {
        isDiscarded = discarded;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}

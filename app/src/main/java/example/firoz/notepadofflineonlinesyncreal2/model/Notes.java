package example.firoz.notepadofflineonlinesyncreal2.model;

public class Notes {
    private String unique_id;
    private String notes;
    private int userid;
    private String created_at;
    private String updated_at;
    private int status;
    private int delete_status;

    public Notes(String unique_id, String notes, int userid, String created_at, int status) {
        this.unique_id = unique_id;
        this.notes = notes;
        this.userid = userid;
        this.created_at = created_at;
        this.status = status;
    }

    public Notes(String unique_id, String notes, int userid, String created_at, int status, int delete_status) {
        this.unique_id = unique_id;
        this.notes = notes;
        this.userid = userid;
        this.created_at = created_at;
        this.status = status;
        this.delete_status = delete_status;
    }

    public Notes(String unique_id, String notes, int userid, String created_at, String updated_at, int status, int delete_status) {
        this.unique_id = unique_id;
        this.notes = notes;
        this.userid = userid;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
        this.delete_status = delete_status;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public String getNotes() {
        return notes;
    }

    public int getUserid() {
        return userid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getStatus() {
        return status;
    }

    public int getDelete_status() {
        return delete_status;
    }
}

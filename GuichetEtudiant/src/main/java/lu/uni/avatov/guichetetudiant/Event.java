package lu.uni.avatov.guichetetudiant;

import java.util.Date;

public class Event {
    /**
     *
     */
    private Date start; //Corresponds to DateDebut in Guichet Etudaint JSON response
    private Date end; //Corresponds to DateFin in Guichet Etudaint JSON response
    private String id; //Corresponds to Id in Guichet Etudaint JSON response
    private String subject; //Corresponds to Cours in Guichet Etudaint JSON response
    private String subjectId; //Corresponds to IdCours in Guichet Etudaint JSON response
    private String eventType; //Corresponds to LibelleType in Guichet Etudaint JSON response
    private boolean isCancelled; //Corresponds to IsCancelled in Guichet Etudaint JSON response
    private String description; //Corresponds to Description in Guichet Etudaint JSON response
    private String room; //Corresponds to Local in Guichet Etudaint JSON response
    private String mainFormationId; //Corresponds to IdFormPrincipal in Guichet Etudaint JSON response

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getMainFormationId() {
        return mainFormationId;
    }

    public void setMainFormationId(String mainFormationId) {
        this.mainFormationId = mainFormationId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
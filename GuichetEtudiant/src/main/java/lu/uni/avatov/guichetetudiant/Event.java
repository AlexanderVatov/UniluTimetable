package lu.uni.avatov.guichetetudiant;

import java.util.Date;

public class Event {
    /**
     *
     */
    public Date start; //Corresponds to DateDebut in Guichet Étudiant JSON response
    public Date end; //Corresponds to DateFin in Guichet Étudiant JSON response
    public String id; //Corresponds to Id in Guichet Étudiant JSON response
    public String description; //Corresponds to Description in Guichet Étudiant JSON response
    public String subject; //Corresponds to Cours in Guichet Étudiant JSON response
    public String subjectId; //Corresponds to IdCours in Guichet Étudiant JSON response
    public String lecturer; //Corresponds to Enseignant in Guichet Étudiant JSON response
    public String eventType; //Corresponds to LibelleType in Guichet Étudiant JSON response
    public boolean isCancelled; //Corresponds to IsCancelled in Guichet Étudiant JSON response
    public String room; //Corresponds to Local in Guichet Étudiant JSON response
    public String mainFormationId; //Corresponds to IdFormPrincipal in Guichet Étudiant JSON response

}

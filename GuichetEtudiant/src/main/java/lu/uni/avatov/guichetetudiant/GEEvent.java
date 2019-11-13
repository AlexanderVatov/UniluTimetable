package lu.uni.avatov.guichetetudiant;

import java.util.Date;

public class GEEvent {
    /**
     *
     */
    public Date start; //Corresponds to DateDebut in Guichet Étudiant JSON response
    public Date end; //Corresponds to DateFin in Guichet Étudiant JSON response
    public String id; //Corresponds to Id in Guichet Étudiant JSON response
    public String title; //Corresponds to Title in Guichet Étudiant JSON response
    public String subject; //Corresponds to Cours in Guichet Étudiant JSON response
    public String subjectId; //Corresponds to IdCours in Guichet Étudiant JSON response
    public String lecturer; //Corresponds to Enseignant in Guichet Étudiant JSON response
    public String eventType; //Corresponds to TypeCPE in Guichet Étudiant JSON response
    public boolean isCanceled; //Corresponds to IsCancelled in Guichet Étudiant JSON response
    public String room; //Corresponds to Local in Guichet Étudiant JSON response
    public String mainStudyProgramId; //Corresponds to IdFormPrincipal in Guichet Étudiant JSON response

}

package advancejavaproject3;

/**
 *
 * @author aral
 */
import java.time.LocalDate;

public class Email {
    private String sender;
    private String subject;
    private LocalDate date;
    private boolean hasAttachment;
    private String body;
    
    public Email(String sender, String subject, LocalDate date, boolean hasAttachment, String body){
        this.sender = sender;
        this.subject = subject;
        this.date = date;
        this.hasAttachment = hasAttachment;
        this.body = body;
    }
    
    public String getSender(){
        return sender;
    }
    
    public String getSubject(){
        return subject;
    }
    
    public LocalDate getDate(){
        return date;
    }
    
    public boolean isHasAttachment(){
        return hasAttachment;
    }
    
    public String getBody(){
        return body;
    }
}


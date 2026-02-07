package advancejavaproject3;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Date;
import javax.activation.*;

public class EmailService {

    public static void sendEmail(HostConfig host, String to, String subject, String body, String attachmentPath) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", host.getSendHost());
        props.put("mail.smtp.port", String.valueOf(host.getSendPort()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.from", host.getUsername());

        if (host.getSendPort() == 465) {
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else if (host.getSendPort() == 587) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(host.getUsername(), host.getPassword());
            }
        };

        Session session = Session.getInstance(props, auth);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom();
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        if (attachmentPath != null && !attachmentPath.trim().isEmpty()) {
            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);
            multipart.addBodyPart(textPart);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentPath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(new java.io.File(attachmentPath).getName());
            multipart.addBodyPart(attachmentPart);

            msg.setContent(multipart);
        } else {
            msg.setText(body);
        }

        Transport.send(msg);
    }

    public static Email[] receiveEmailsFromFolder(HostConfig host, String folderName) throws Exception {
        String actualFolderName = folderName;

        if (folderName.equals("Sent Items")) {
            actualFolderName = findSentMailFolder(host);
            if (actualFolderName == null) {
                System.err.println("Could not find Sent Mail folder");
                return new Email[0];
            }
        } else if (folderName.equals("INBOX")) {
            actualFolderName = "INBOX";
        }

        return receiveEmailsFromSpecificFolder(host, actualFolderName);
    }

    private static String findSentMailFolder(HostConfig host) throws Exception {
        Properties props = new Properties();
        String protocol = host.getProtocol().toLowerCase();
        props.put("mail.store.protocol", protocol);
        props.put("mail." + protocol + ".host", host.getReceiveHost());
        props.put("mail." + protocol + ".port", String.valueOf(host.getReceivePort()));
        props.put("mail." + protocol + ".ssl.enable", "true");

        Session session = Session.getInstance(props);
        Store store = session.getStore(protocol);
        store.connect(host.getReceiveHost(), host.getUsername(), host.getPassword());

        String[] possibleNames = {
            "[Gmail]/Sent Mail",
            "[Gmail]/Gönderilmiş Postalar",
            "Sent",
            "Sent Messages",
            "Gönderilmiş Postalar",
            "INBOX.Sent"
        };

        System.out.println("Searching for Sent Mail folder...");
        for (String name : possibleNames) {
            try {
                Folder folder = store.getFolder(name);
                if (folder.exists()) {
                    System.out.println("Found Sent Mail folder: " + name);
                    store.close();
                    return name;
                }
            } catch (Exception e) {
            }
        }

        store.close();
        return null;
    }

    public static Email[] receiveEmails(HostConfig host) throws Exception {
        return receiveEmailsFromSpecificFolder(host, "INBOX");
    }

    private static Email[] receiveEmailsFromSpecificFolder(HostConfig host, String folderName) throws Exception {
        Properties props = new Properties();

        String protocol = host.getProtocol().toLowerCase();
        props.put("mail.store.protocol", protocol);
        props.put("mail." + protocol + ".host", host.getReceiveHost());
        props.put("mail." + protocol + ".port", String.valueOf(host.getReceivePort()));
        props.put("mail." + protocol + ".ssl.enable", "true");

        Session session = Session.getInstance(props);
        Store store = session.getStore(protocol);

        System.out.println("Connecting to: " + host.getReceiveHost());
        store.connect(host.getReceiveHost(), host.getUsername(), host.getPassword());
        System.out.println("Connected successfully!");

        Folder folder = store.getFolder(folderName);
        if (!folder.exists()) {
            System.err.println("Folder does not exist: " + folderName);
            store.close();
            return new Email[0];
        }

        folder.open(Folder.READ_ONLY);
        System.out.println("Opened folder: " + folderName);

        Message[] messages = folder.getMessages();
        System.out.println("Total messages: " + messages.length);

        int messageCount = Math.min(messages.length, 10);
        Email[] emails = new Email[messageCount];

        for (int i = 0; i < messageCount; i++) {
            Message msg = messages[messages.length - messageCount + i];

            String sender = "Unknown";
            if (msg.getFrom() != null && msg.getFrom().length > 0) {
                sender = msg.getFrom()[0].toString();
            }

            String subject = msg.getSubject();
            if (subject == null) {
                subject = "(No Subject)";
            }

            Date date = msg.getSentDate();
            if (date == null) {
                date = new Date();
            }

            java.time.LocalDate localDate = date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

            String bodyContent = "";
            boolean hasAttachment = false;

            try {
                Object content = msg.getContent();
                if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    hasAttachment = checkForAttachment(multipart);
                    bodyContent = getTextFromMultipart(multipart);
                } else if (content instanceof String) {
                    bodyContent = (String) content;
                } else {
                    bodyContent = "Unable to read message content";
                }
            } catch (Exception e) {
                bodyContent = "Error reading message: " + e.getMessage();
            }

            emails[i] = new Email(sender, subject, localDate, hasAttachment, bodyContent);
            System.out.println("Processed email " + (i+1) + ": " + subject);
        }

        folder.close(false);
        store.close();

        System.out.println("Email retrieval completed from " + folderName + "!");
        return emails;
    }

    private static boolean checkForAttachment(Multipart multipart) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                return true;
            }
        }
        return false;
    }

    private static String getTextFromMultipart(Multipart multipart) throws Exception {
        //e-postadan text/plain iceriği cıkarır.
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent().toString());
            }
        }
        return result.toString();
    }
}

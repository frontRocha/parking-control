package br.project.com.parkingcontrol.domain.email;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "TB_EMAIL")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private String ownerRef;
    @Column(nullable = false)
    private String emailFrom;
    @Column(nullable = false)
    private String emailTo;
    @Column(nullable = false)
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    @Column(nullable = false)
    private LocalDateTime sendDateEmail;

    private static final String DEFAULT_EMAIL_OWNERREF = "frontRocha";
    private static final String DEFAULT_EMAIL_FROM = "mauriciopedeo007@gmail.com";
    private static final String DEFAULT_EMAIL_SUBJECT = "Seja bem vindo ao sistema";
    private static final String DEFAULT_EMAIL_TEXT = "<html>"
            + "<head>"
            + "<style>"
            + "body { font-family: Poppins; color: #000000; font-size: 22px; }"
            + "h1 { color: #000000 }"
            + "h2 { color: #000000; }"
            + "p { color: #000000 }"
            + "a { color: #000000; }"
            + ".icon { width: 18px; height: 18px; margin-right: 5px; }"
            + "</style>"
            + "<link href='https://fonts.googleapis.com/css2?family=Poppins&display=swap' rel='stylesheet'>"
            + "</head>"
            + "<body>"
            + "<h1>Seja bem vindo!!</h1>"
            + "<h2>Muito obrigado por testar meu sistema de controle de estacionamento.</h2>"
            + "<p>Aqui estão alguns links de contato:</p>"
            + "<ul>"
            + "<li><img class=\"icon\" src=\"https://cdn-icons-png.flaticon.com/512/174/174857.png\"><a href=\"https://github.com/frontRocha\">GitHub</a></li>"
            + "<li><img class=\"icon\" src=\"https://cdn-icons-png.flaticon.com/512/25/25231.png\"><a href=\"https://www.linkedin.com/in/frontrocha\">LinkedIn</a></li>"
            + "</ul>"
            + "<p>Você também pode conferir os repositórios no GitHub <a href=\"https://github.com/frontRocha?tab=repositories\">aqui</a>.</p>"
            + "<p>Agradeço novamente por seu interesse e apoio!</p>"
            + "</body>"
            + "</html>";

    public static class Builder {
        private UUID id;
        private String ownerRef;
        private String emailFrom;
        private String emailTo;
        private String subject;
        private String text;
        private LocalDateTime sendDateTime;

        public Builder() {
            this.id = null;
            this.ownerRef = DEFAULT_EMAIL_OWNERREF;
            this.emailFrom = DEFAULT_EMAIL_FROM;
            this.emailTo = null;
            this.subject = DEFAULT_EMAIL_SUBJECT;
            this.text = DEFAULT_EMAIL_TEXT;
            this.sendDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setOwnerRef(String ownerRef) {
            this.ownerRef = ownerRef;
            return this;
        }

        public Builder setEmailFrom(String emailFrom) {
            this.emailFrom = emailFrom;
            return this;
        }

        public Builder setEmailTo(String emailTo) {
            this.emailTo = emailTo;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setSendDateTime(LocalDateTime sendDateTime) {
            this.sendDateTime = sendDateTime;
            return this;
        }

        public Email build() {
            return new Email(id, ownerRef, emailFrom, emailTo, subject, text, sendDateTime);
        }
    }
}

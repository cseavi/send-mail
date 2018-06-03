package com.mail.bootcampmail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by Avinash on 03-06-2018.
 */
@RestController
public class SendMailController {

    private static final Logger LOG = LoggerFactory.getLogger(SendMailController.class);

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private CSVReader csvReader;

    @RequestMapping("/sendMail")
    public String sendMail() throws Exception {

        String csvFilePath = "C:\\Users\\Avinash\\Downloads\\login.csv";
        Map<String, String> emailPassword = csvReader.readCSVFile(csvFilePath);

        List<MimeMessage> mimeMessagesList = new ArrayList<>();

        for (Map.Entry<String, String> entry : emailPassword.entrySet()) {
            mimeMessagesList.add(createMimeMessage(entry.getKey().toString(), entry.getValue().toString()));
        }

        MimeMessage[] mimeMessages = new MimeMessage[mimeMessagesList.size()];
        mimeMessages = mimeMessagesList.toArray(mimeMessages);

        LOG.info("START: mail send in batch");
        sender.send(mimeMessages);
        LOG.info("END: mail send in batch");

        return "Mail Sent Success!";
    }

    MimeMessage createMimeMessage(String email, String password) {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(email);
            helper.setText("<p>Hello,</p>\n" +
                    "<p>Please find login credential for Bootcamp site:&nbsp;</p>\n" +
                    "<table style=\"height: 50px; width: 394px;\">\n" +
                    "<tbody>\n" +
                    "<tr>\n" +
                    "<td style=\"width: 103px;\">&nbsp;URL</td>\n" +
                    "<td style=\"width: 279px;\">http://bootcamp.techolution.com</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "<td style=\"width: 103px;\">&nbsp;Username</td>\n" +
                    "<td style=\"width: 279px;\">" + email + "</td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "<td style=\"width: 103px;\">&nbsp;Password</td>\n" +
                    "<td style=\"width: 279px;\">" + password + "</td>\n" +
                    "</tr>\n" +
                    "</tbody>\n" +
                    "</table>\n" +
                    "<p>Note:&nbsp;Please don't share login credentials with anyone.</p>\n" +
                    "<p>Thanks,<br />Avinash</p>\n" +
                    "<p>&nbsp;</p>", true);
            helper.setSubject("Bootcamp Login Credential");

            LOG.info("Mail has been composed for - " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            LOG.info("Error while composed mail to - " + email);
        }
        return message;
    }
}

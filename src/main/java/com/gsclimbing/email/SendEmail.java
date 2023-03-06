package com.gsclimbing.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendEmail implements Runnable {

	Logger log = LoggerFactory.getLogger(SendEmail.class);

	String to = null;
	String filename = null;
	String subject = null;
	String message = null;
	byte[] bytes = null;

	public SendEmail(String to, String subject, String message, String filename, byte[] bytes) {
		this.to = to;
		this.subject = subject;
		this.filename = filename;
		this.message = message;
		this.bytes = bytes;
	}

	public void sendEmailTLS() {

		final String username = "reports@gsclimbing.com";
		final String password = "reports.73Xg&u4V";

//		final String username = "mail.gsclimbing@gmail.com";
//		final String password = "mail.messages.gsclimbing.987";

		String host = "smtp.gmail.com";
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			MimeMessage mail = new MimeMessage(session);
			mail.setFrom(new InternetAddress(username));
			mail.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			mail.setSubject(this.subject);

			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart1 = new MimeBodyPart();

			messageBodyPart1.setContent(this.message, "text/html; charset=utf-8");
			// messageBodyPart1.setText(this.message);

			multipart.addBodyPart(messageBodyPart1);

			if (this.bytes != null) {
				MimeBodyPart messageBodyPart2 = new MimeBodyPart();
				File report = null;
				report = createPDFFile(filename, this.bytes);
				DataSource source = new FileDataSource(report);
				messageBodyPart2.setDataHandler(new DataHandler(source));
				messageBodyPart2.setFileName(filename);
				multipart.addBodyPart(messageBodyPart2);
			}
			mail.setContent(multipart);
			Transport.send(mail);
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		sendEmailTLS();
	}

	public File createPDFFile(String filename, byte[] bytes) {

		File pdf = null;

		try {
			pdf = File.createTempFile(filename, null);
			FileOutputStream fos = new FileOutputStream(pdf);
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pdf;
	}

}

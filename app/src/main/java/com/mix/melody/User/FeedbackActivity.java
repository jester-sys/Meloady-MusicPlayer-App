package com.mix.melody.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.mix.melody.databinding.ActivityFeedbackBinding;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class FeedbackActivity extends AppCompatActivity {

       ActivityFeedbackBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Feedback");


        binding.sendFA.setOnClickListener(view -> {
            String feedbackMsg = binding.FeedbackMsgFA.getText().toString() + "\n" + binding.EmailFA.getText().toString();
            String subject = binding.TopicFA.getText().toString();
            String userName = "kanhaiyakumar06794@gmail.com";
            String pass = "MusicPlayer";
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (feedbackMsg.length() > 0 && subject.length() > 0 && (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting())) {
                new Thread(() -> {
                    try {
                        Properties properties = new Properties();
                        properties.put("mail.smtp.auth", "true");
                        properties.put("mail.smtp.starttls.enable", "true");
                        properties.put("mail.smtp.host", "smtp.gmail.com");
                        properties.put("mail.smtp.port", "587");
                        Session session = Session.getInstance(properties, new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(userName, pass);
                            }
                        });
                        Message mail = new MimeMessage(session);
                        mail.setSubject(subject);
                        mail.setText(feedbackMsg);
                        mail.setFrom(new InternetAddress(userName));
                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName));
                        Transport.send(mail);
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Thanks For Feedback!!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Went Something Wrong!!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}

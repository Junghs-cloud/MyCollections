package com.example.mycollections

import android.util.Log
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(private val receiverEmail:String, private val newPassword: String) {
    fun sendMail()
    {
        try {
            val user="rabbit4935@gmail.com"
            val password="wdrk perh wboa rajj"
            val props = Properties()
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.port"] = "587"
            props["mail.smtp.ssl.trust"] = "*";

            val session = Session.getInstance(props, object: javax.mail.Authenticator() {
                override  fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(user, password)
                }
            })

            val message = MimeMessage(session)
            message.setFrom(InternetAddress(user))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail))
            message.subject = "MyCollections 비밀번호 변경 메일입니다."
            message.setText("임시 비밀번호는"+ newPassword+"입니다.\n")

            Transport.send(message)
        }
        catch (e: Exception)
        {
            Log.d("jhs", e.toString())
        }
    }
}
package com.example.sendemail;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

/*
 * SendEmail : Email sending example by using SMTP. Attaching image file arrowed.
 * Author : Dennis (DONGGEUN JUNG)
 * Email : topsan72@gmail.com / topofsan@naver.com
 * How to change source code :
 *   = SENDER_ID : Your email address
 *   = SENDER_PW : Your email password
 *   = String filePath : Attached file path
 */

public class MainActivity extends AppCompatActivity {
    final String SENDER_ID = "Your Email address";
    final String SENDER_PW = "Your Email password";
    TextView textAddress = null;
    TextView textMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAddress = (TextView) findViewById(R.id.editReceiver); //Email address of receiver
        textMessage = (TextView) findViewById(R.id.editMessage); //Message text

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        // Check Memory Read permission
        checkStoragePermission();
    }

    // 메모리 Read 권한을 체크
    private boolean checkStoragePermission() {
        // 마시멜로우 이후 버전이라면 사용자에게 Permission 을 획득
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 사용자가 Permission 을 부여하지 않았다면
            // 권한을 요청하는 팝업창을 표시
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(this,
                            "No Permission to use the Read Storage",
                            Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]
                                {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        111);
                return false;
            }
        }
        return true;
    }

    // 권한 요청 결과 이벤트 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        switch (requestCode){
            // 메모리 Read 권한 요청 결과 일때
            case 111:
                // 사용자가 권한 부여를 거절 했을때
                if (grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,
                            "Cannot run application because Storage permission is not granted",
                            Toast.LENGTH_SHORT).show();
                }
                // 사용자가 권한을 부여했을때
                else {

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,
                        permissions, grantResults);
                break;
        }
    }

    public void onClick(View v) {
        try {
            GMailSender gMailSender = new GMailSender(SENDER_ID, SENDER_PW);
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
            //String filePath = sd + "/" + "simple_color_1.jpg";
            String filePath = null;

            gMailSender.sendMail("Title", textMessage.getText().toString(),
                    textAddress.getText().toString(), filePath);
            Toast.makeText(getApplicationContext(), "Sending email succeeded.",
                    Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "Wrong email address.",
                    Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

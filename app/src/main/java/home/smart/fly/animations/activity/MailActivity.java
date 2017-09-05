package home.smart.fly.animations.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import home.smart.fly.animations.R;
import home.smart.fly.animations.utils.DpConvert;
import home.smart.fly.animations.utils.SendEmail;

public class MailActivity extends AppCompatActivity {
    private static final String TAG = "MailActivity";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        ButterKnife.bind(this);

        Intent receiveIntent = getIntent();

        if (receiveIntent != null) {

            if (receiveIntent.getClipData() != null) {
                ClipData mClipData = receiveIntent.getClipData();

                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item mItem = mClipData.getItemAt(i);
                    CharSequence text = mItem.getText();

                    String[] datas = text.toString().split("http");

                    if (datas.length > 1) {
                        Log.e(TAG, "onCreate: " + datas[0]);
                        Log.e(TAG, "onCreate: " + "http" + datas[1]);
                    }


                }


            }
        }
    }

    @OnClick({R.id.sendMailNative, R.id.sendMailApp})
    public void Click(View view) {
        switch (view.getId()) {
            case R.id.sendMailNative:
                sendMailNative();
                break;
            case R.id.sendMailApp:
                sendMailWithApp();
                break;
            default:
                break;
        }
    }

    private void sendMailWithApp() {
        Uri uri = Uri.parse("mailto:zhuyongqing89@163.com");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, uri);
        mIntent.setType("message/rfc822");
        String[] tos = {"zhuyongqing89@163.com"};
        mIntent.putExtra(Intent.EXTRA_EMAIL, tos);

        mIntent.putExtra(Intent.EXTRA_TEXT, "body");
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");

//        mIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Chrysanthemum.jpg"));
//        mIntent.setType("image/*");
//        mIntent.setType("message/rfc882");
        Intent.createChooser(mIntent, "Choose Email Client");
        startActivity(mIntent);
    }

    private void sendMailNative() {
        //耗时操作要起线程...有几个新手都是这个问题

        final EditText mEditText = new EditText(this);
        mEditText.setWidth(DpConvert.dip2px(this, 200));
        mEditText.setHeight(DpConvert.dip2px(this, 40));
        LinearLayout mLinearLayout = new LinearLayout(this);
        mLinearLayout.setHorizontalGravity(Gravity.CENTER);
        mLinearLayout.addView(mEditText);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("please input password !");
        mBuilder.setView(mLinearLayout);
        mBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilder.setPositiveButton("sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    Toast.makeText(MailActivity.this, "password is null", Toast.LENGTH_LONG).show();
                } else {
                    new MyAsyncTask().execute(mEditText.getText().toString().trim());
                }
            }
        });
        mBuilder.show();


    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(MailActivity.this, s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MailActivity.this);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SendEmail sender = new SendEmail();
                //设置服务器地址和端口，网上搜的到
                sender.setProperties("smtp.163.com", "25");
                //分别设置发件人，邮件标题和文本内容
                sender.setMessage("foreverlove.zyl@163.com", "EmailSender", "Java Mail ！");
                //设置收件人
                sender.setReceiver(new String[]{"foreverlove.zyl@163.com"});
                //添加附件
                //这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
//                          sender.addAttachment("/sdcard/DCIM/Camera/asd.jpg");
                //发送邮件
                sender.sendEmail("smtp.163.com", "foreverlove.zyl@163.com", params[0]);
                //sender.setMessage("你的163邮箱账号", "EmailS//ender", "Java Mail ！");这里面两个邮箱账号要一致</span>
                return "success";
            } catch (AddressException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
        }
    }


}

package com.example.st_25_sdk_sample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.type5.st25dv.ST25DVTag;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TagDiscovery.onTagDiscoveryCompletedListener {
    private NfcAdapter mNfcAdapter;

    // Last tag taped
    private ST25DVTag mNfcTag;

    private boolean mMailboxEnabled;
    private boolean mHostPutMsg;
    private boolean mRfPutMsg;
    private boolean mHostMissMsg;
    private boolean mRfMissMsg;

    enum Action {
        WRITE_NDEF_MESSAGE,
        READ_MEMORY_SIZE,
        READ_FAST_MEMORY,
        WRITE_MAIL_BOX,
        GET_INFO,
        READ_MAIL_BOX,
        RESET_MAIL_BOX,

    };

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Button writeMailBoxMsgButton = (Button) findViewById(R.id.writeMailBoxBtn);

        Button readMailBoxMsgButton = (Button) findViewById(R.id.readMailBoxBtn);

        Button resetMailBoxMsgButton = (Button) findViewById(R.id.resetMailBoxBtn);

        TextView stInfoText = (TextView) findViewById(R.id.StInfo);
        stInfoText.setMovementMethod(new ScrollingMovementMethod());

        writeMailBoxMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNfcTag != null) {
                    TextView mailBoxText = (TextView) findViewById(R.id.MailboxInfo);
                    // All the actions doing a transceive() to communicate with the tag should be done
                    // in an Async Task to avoid disturbance of Android UI Thread
                    mailBoxText.setText("");
                    executeAsynchronousAction(Action.WRITE_MAIL_BOX);
                }
            }
        });

        readMailBoxMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNfcTag != null) {
                    TextView mailBoxText = (TextView) findViewById(R.id.MailboxInfo);
                    // All the actions doing a transceive() to communicate with the tag should be done
                    // in an Async Task to avoid disturbance of Android UI Thread
                    mailBoxText.setText("");
                    executeAsynchronousAction(Action.READ_MAIL_BOX);
                }
            }
        });

        resetMailBoxMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNfcTag != null) {
                    TextView mailBoxText = (TextView) findViewById(R.id.MailboxInfo);
                    // All the actions doing a transceive() to communicate with the tag should be done
                    // in an Async Task to avoid disturbance of Android UI Thread
                    mailBoxText.setText("");
                    executeAsynchronousAction(Action.RESET_MAIL_BOX);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if if this phone has NFC hardware
        if (mNfcAdapter == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
            alertDialogBuilder.setTitle("Warning!");
        // set dialog message
            alertDialogBuilder
                    .setMessage("This phone doesn't have NFC hardware!")
                    .setMessage("This phone doesn't have NFC hardware!")
                    .setCancelable(true)
                    .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        } else {
            // Toast.makeText(this, "We are ready to play with NFC!", Toast.LENGTH_SHORT).show();
            // Give priority to the current activity when receiving NFC events (over other actvities)
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter[] nfcFilters = null;
            String[][] nfcTechLists = null;
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcFilters, nfcTechLists);
        }
        // The current activity can be resumed for several reasons (NFC tag tapped is one of them).
        // Check what was the reason which triggered the resume of current application
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ||
                action.equals(NfcAdapter.ACTION_TECH_DISCOVERED) ||
                action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            // If the resume was triggered by an NFC event, it will contain an EXTRA_TAG provinding
            // the handle of the NFC Tag
            Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (nfcTag != null) {
                Toast.makeText(this, "Starting Tag discovery", Toast.LENGTH_SHORT).show();

                // This action will be done in an Asynchronous task.
                // onTagDiscoveryCompleted() of current activity will be called when the discovery is completed.
                new TagDiscovery(this).execute(nfcTag);
            }
        }
    }

    @Override
    public void onTagDiscoveryCompleted(NFCTag nfcTag, TagHelper.ProductID productId, STException error) {
        TextView tagInfoText = (TextView) findViewById(R.id.TagInfo);
        TextView mailBoxText = (TextView) findViewById(R.id.MailboxInfo);
        tagInfoText.setText("");
        TextView stInfoText = (TextView) findViewById(R.id.StInfo);
        stInfoText.setMovementMethod(new ScrollingMovementMethod());
        mailBoxText.setText("");
        if (error != null) {
            Toast.makeText(getApplication(), "Error while reading the tag: " + error.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if (nfcTag != null && productId.name().contains("PRODUCT_ST_ST25") ) {
            mNfcTag = (ST25DVTag) nfcTag;

            String name = nfcTag.getName();
            String description = nfcTag.getDescription();
            String[] techList = nfcTag.getTechList();

            String txt = "Name: "+name+"\nDescription: "+description+"\n Tech list: \n";
            for(int i=0;i<techList.length;i++){
                txt+=techList[i]+"\n";
            }


            tagInfoText.setText(txt);

            try {

                String tagName = nfcTag.getName();
                //Toast.makeText(this,"TAG AQUIRED "+productId.name().toString(),Toast.LENGTH_LONG).show();

                //TextView tagNameTextView = (TextView) findViewById(R.id.);
                //tagNameTextView.setText(tagName);

                String uidString = nfcTag.getUidString();
              //  TextView uidTextView = (TextView) findViewById(R.id.uidTextView);
               // uidTextView.setText(uidString);

                executeAsynchronousAction(Action.GET_INFO);

            } catch (STException e) {
                e.printStackTrace();
                Toast.makeText(this, "Discovery successful but failed to read the tag!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Tag discovery failed! or unsupported device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // onResume() gets called after this to handle the intent
        setIntent(intent);
    }

    private void executeAsynchronousAction(Action action) {
        Log.d("nfc", "Starting background action " + action);
        new myAsyncTask(action).execute();
    }
    class myAsyncTask extends AsyncTask<Void, Void, ActionStatus> {
        Action mAction;
        int memSizeInBytes;

        List<STRegister> list;
        TextView stInfoText = (TextView) findViewById(R.id.StInfo);
        TextView mailBoxText = (TextView) findViewById(R.id.MailboxInfo);

        String msg="";
        String infoTxt="";
        String mailBoxTxt="";

        public myAsyncTask(Action action) {
            mAction = action;
        }

        void getInfo() throws STException {

                infoTxt="";
                mailBoxTxt="";

                mMailboxEnabled = false;
                mHostPutMsg = false;
                mHostMissMsg = false;
                mRfPutMsg = false;
                mRfMissMsg = false;
                memSizeInBytes = mNfcTag.getMemSizeInBytes();
                List<STRegister> dynRegister = mNfcTag.getDynamicRegisterList();
                List<STRegister> register = mNfcTag.getRegisterList();
                int nAreas = mNfcTag.getNumberOfAreas();
                infoTxt = "MEMORY SIZE (bytes): " + memSizeInBytes + "\nArea numbers: " + nAreas;


                infoTxt += "\n-------------------------";
                infoTxt += "\nRegister:";
                for (STRegister reg : register) {
                    infoTxt += "\nName: " + reg.getRegisterName() + "\nAddress: " + Integer.toHexString(reg.getRegisterAddress()) + "\nDescription: " + reg.getRegisterContentDescription() + "\nContent: " + Integer.toHexString(reg.getRegisterValue()) + "\n";
                }
                infoTxt += "-------------------------";
                infoTxt += "\nDynamic register:";
                for (STRegister reg : dynRegister) {
                    infoTxt += "\nName: " + reg.getRegisterName() + "\nAddress: " + Integer.toHexString(reg.getRegisterAddress()) + "\nDescription: " + reg.getRegisterContentDescription() + "\nContent: " + Integer.toHexString(reg.getRegisterValue()) + "\n";
                }

                mMailboxEnabled = mNfcTag.isMailboxEnabled(false);
                mHostPutMsg = mNfcTag.hasHostPutMsg(false);
                mHostMissMsg = mNfcTag.hasHostMissMsg(false);
                mRfPutMsg = mNfcTag.hasRFPutMsg(false);
                mRfMissMsg = mNfcTag.hasRFMissMsg(false);
                mailBoxTxt += "Mailbox enabled: " + mMailboxEnabled;
                if (!mMailboxEnabled) {
                    mNfcTag.enableMailbox();
                    mNfcTag.refreshMailboxStatus();
                    boolean mailBoxEnabled2 = mNfcTag.isMailboxEnabled(true);
                    mailBoxTxt += "\nTRYING TO ACTIVATE MAILBOX:" + mailBoxEnabled2;
                }

                mailBoxTxt += "\n(HOST) msg put via serial interface : " + mHostPutMsg;
                mailBoxTxt += "\n(HOST) msg missed by interface: " + mHostMissMsg;
                mailBoxTxt += "\n(RF) msg put via rf: " + mRfPutMsg;
                mailBoxTxt += "\n(RF) msg miss via rf: " + mRfMissMsg;
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result;

            try {
                switch (mAction) {
                    case GET_INFO:

                        getInfo();

                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;


                    case WRITE_NDEF_MESSAGE:
                        // Create a NDEFMsg
                        NDEFMsg ndefMsg = new NDEFMsg();

                        // Create a URI record containing http://www.st.com
                       // UriRecord uriRecord = new UriRecord(NDEF_RTD_URI_ID_HTTP_WWW, "st.com/st25");

                        // Add the record to the NDEFMsg
                        //ndefMsg.addRecord(uriRecord);

                        // Write the NDEFMsg into the tag
                        mNfcTag.writeNdefMessage(ndefMsg);

                        // If we get to this point, it means that no STException occured so the action was successful
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;
                    case READ_MEMORY_SIZE:
                        memSizeInBytes = mNfcTag.getMemSizeInBytes();
                        ;
                        // If we get to this point, it means that no STException occured so the action was successful
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;
                    case WRITE_MAIL_BOX:
                      //  mNfcTag.writeMailboxMessage();
                        byte[] myvar = "HELLO NFC!".getBytes();

                        mNfcTag.writeMailboxMessage(myvar);
                        getInfo();

                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;
                    case READ_MAIL_BOX:
                        byte[] ris = mNfcTag.readMailboxMessage(0,10);

                        msg = new String(ris);
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;
                    case RESET_MAIL_BOX:
                        mNfcTag.resetMailbox();
                        getInfo();
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;
                    case READ_FAST_MEMORY:

                        // If we get to this point, it means that no STException occured so the action was successful
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;


                    default:
                        result = ActionStatus.ACTION_FAILED;
                        break;
                }

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = ActionStatus.TAG_NOT_IN_THE_FIELD;
                        break;

                    default:
                        e.printStackTrace();
                        result = ActionStatus.ACTION_FAILED;
                        break;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    switch (mAction) {
                        case RESET_MAIL_BOX:
                            stInfoText.setText(infoTxt);
                            mailBoxText.setText(mailBoxTxt);
                            Toast.makeText(MainActivity.this, "RESET OK", Toast.LENGTH_LONG).show();
                            break;
                        case READ_MAIL_BOX:
                            stInfoText.setText(infoTxt);
                            mailBoxText.setText(mailBoxTxt);
                            Toast.makeText(MainActivity.this, "READ: "+msg, Toast.LENGTH_LONG).show();

                        case WRITE_MAIL_BOX:
                            Toast.makeText(MainActivity.this, "SEND successful", Toast.LENGTH_LONG).show();
                        case GET_INFO:
                            stInfoText.setText(infoTxt);
                            mailBoxText.setText(mailBoxTxt);
                            break;

                        case WRITE_NDEF_MESSAGE:
                            Toast.makeText(MainActivity.this, "Write successful", Toast.LENGTH_LONG).show();
                            break;
                        case READ_MEMORY_SIZE:
                            Toast.makeText(MainActivity.this, "READ successful "+memSizeInBytes+" BYTES", Toast.LENGTH_LONG).show();

                            //mTagMemSizeTextView.setText(String.valueOf(memSizeInBytes));
                            break;
                    }
                    break;

                case ACTION_FAILED:
                    Toast.makeText(MainActivity.this, "Action failed!", Toast.LENGTH_LONG).show();
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    Toast.makeText(MainActivity.this, "Tag not in the field!", Toast.LENGTH_LONG).show();
                    break;
            }

            return;
        }
    }


}



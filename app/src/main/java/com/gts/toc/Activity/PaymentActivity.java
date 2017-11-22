package com.gts.toc.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gts.toc.GlobalApplication;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstBank;
import com.gts.toc.model.MstParams;
import com.gts.toc.object.ObjSpinnerItem;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.gts.toc.view.spinner.NiceSpinner;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private int PICK_GALLERY_REQUEST    = 100;
    private int PICK_CAMERA_REQUEST     = 200;
    private DatabaseHandler mDataBase   = new DatabaseHandler();
    private double scaleFactor          = 0.5;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private String orderID;
    private String mType;
    private TextView mLabelPayment;
    private ImageView mImagePayment;
    private ImageView mAttachCamera;
    private ImageView mAttachGallery;
    private String photoFileName;
    private Bitmap mBitmap;
    private byte[] mBitmapData;
    private FloatingActionButton mBtnUpload;
    private int ServiceCost;
    private NiceSpinner mSpinnerBank;
    private EditText mInputNominal;
//    private EditText mInputDescription;
    private List<ObjSpinnerItem> mObjSpinnerBank    = new ArrayList<ObjSpinnerItem>();
    private int CostPayment;
    private int CashBack;
    private TextView mViewCost, mViewCashback, mViewPayment;
    private RelativeLayout mInvoiceLayout;
    private String rekening;
    private String nominal;
//    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Intent mIntent  = getIntent();
        orderID         = mIntent.getStringExtra(GeneralConstant.PARAM_ORDERID);
        mType           = mIntent.getStringExtra(GeneralConstant.PAYMENT_TYPE);
        ServiceCost     = mIntent.getIntExtra(GeneralConstant.PARAM_COST, 0);
        Initialise();
    }
    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(PaymentActivity.this, MainActivity.class);
        intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
        ActivityTransitionLauncher.with(PaymentActivity.this).from(findViewById(R.id.bank_recycler)).launch(intent);
    }
    private void Initialise() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.label_payment_confirmation));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mInvoiceLayout      = (RelativeLayout) findViewById(R.id.layoutInvoice);
        mViewCost           = (TextView) findViewById(R.id.viewCost);
        mViewCashback       = (TextView) findViewById(R.id.viewCashback);
        mViewPayment        = (TextView) findViewById(R.id.viewPayment);
        mLabelPayment       = (TextView) findViewById(R.id.labelPayment);
        mRecyclerView       = (RecyclerView) findViewById(R.id.bank_recycler);
        mImagePayment       = (ImageView) findViewById(R.id.imagePayment);
        mAttachCamera       = (ImageView) findViewById(R.id.attachCamera);
        mAttachGallery      = (ImageView) findViewById(R.id.attachGallery);
        mBtnUpload          = (FloatingActionButton) findViewById(R.id.btnUpload);
        mSpinnerBank        = (NiceSpinner) findViewById(R.id.bank_name);
        mInputNominal       = (EditText) findViewById(R.id.input_nominal);
//        mInputDescription   = (EditText) findViewById(R.id.input_desc);
        switch (mType) {
            case GeneralConstant.PAYMENT_BOOKING:
                CostPayment = ServiceCost;
                mLabelPayment.setText(getResources().getString(R.string.label_payment_booking, "Rp. "+Utility.doubleToStringNoDecimal(CostPayment)));
                mInvoiceLayout.setVisibility(View.GONE);
                break;
            case GeneralConstant.PAYMENT_CANCEL:
                CostPayment = ServiceCost;
                mLabelPayment.setText(getResources().getString(R.string.label_payment_cancel, "Rp. "+Utility.doubleToStringNoDecimal(CostPayment)));
                mInvoiceLayout.setVisibility(View.GONE);
                break;
            case GeneralConstant.PAYMENT_LUNAS:
                CashBack    = getCashBack();
                CostPayment = ServiceCost - CashBack;
                mLabelPayment.setText(getResources().getString(R.string.label_payment_lunas, "Rp. "+Utility.doubleToStringNoDecimal(CostPayment)));
                mInvoiceLayout.setVisibility(View.VISIBLE);
                mViewCost.setText("Rp. "+Utility.doubleToStringNoDecimal(ServiceCost)+"");
                mViewCashback.setText("Rp. "+Utility.doubleToStringNoDecimal(CashBack)+"");
                mViewPayment.setText("Rp. "+Utility.doubleToStringNoDecimal(CostPayment)+"");
                break;
        }
        mInputNominal.setText(CostPayment+"");
        SpinnerBankInitialize();
        mSpinnerBank.setSelectedIndex(0);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(GlobalApplication.getContext()));
        RekeningAdapter mAdapter    = new RekeningAdapter(mDataBase.GetAllBank());
        mRecyclerView.setAdapter(mAdapter);
        mAttachCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAttachCamera();
            }
        });
        mAttachGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAttachGallery();
            }
        });
        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rekening    = mObjSpinnerBank.get(mSpinnerBank.getSelectedIndex()).getItemName();
                nominal     = mInputNominal.getText().toString();
//                description = mInputDescription.getText().toString();
                if (mBitmapData != null ){
                    if (rekening.length() != 0 && nominal.length() != 0 )
                        uploadImage();
                    else
                        Dialog.InformationDialog(PaymentActivity.this, getResources().getString(R.string.msg_empty_nominal));
                }else
                    Dialog.InformationDialog(PaymentActivity.this, getResources().getString(R.string.msg_empty_image));
            }
        });
    }

    private int getCashBack(){
        List<MstParams> ParamsList = mDataBase.GetParams(GeneralConstant.PARAMS_CHASHBACK_ID);
        if (ParamsList.size() > 0){
            MstParams mParams = ParamsList.get(0);
            if (mParams.getParamsValue() != null){
                return Integer.parseInt(mParams.getParamsValue());
            }else{
                return 20000;
            }
        }else{
            return 20000;
        }
    }

    public class RekeningAdapter extends RecyclerView.Adapter<RekeningAdapter.ViewHolder> {
        private List<MstBank> mItems;

        public RekeningAdapter(List<MstBank> itemData) {
            super();
            mItems                  = itemData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v      = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rekening_item, viewGroup, false);
            ViewHolder viewHolder  = new ViewHolder(v);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            final MstBank Bank    = mItems.get(position);
            viewHolder.recDesc.setText(getResources().getString(R.string.rekening, Bank.getBankName(), Bank.getAccountName(), Bank.getRecNo()));
        }
        @Override
        public int getItemCount() {
            return mItems.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            public TextView recDesc;
            public ViewHolder(View itemView) {
                super(itemView);
                recDesc     = (TextView)itemView.findViewById(R.id.rekening_desc);
            }
        }
    }

    private void SpinnerBankInitialize(){
        List<MstBank> ListBank = mDataBase.GetAllBank();
        if (mObjSpinnerBank.size() > 0)
            mObjSpinnerBank.clear();
        int Count  = ListBank.size();
        if (Count > 0){
            for (int i=0; i<Count; i++){
                MstBank ObjBank     = ListBank.get(i);
                ObjSpinnerItem NewBank  = new ObjSpinnerItem();
                NewBank.setItemid(ObjBank.getBankName());
                NewBank.setItemName(ObjBank.getBankName()+" ("+ObjBank.getRecNo()+ ")" );
                mObjSpinnerBank.add(NewBank);
            }
            mSpinnerBank.attachDataSource(mObjSpinnerBank);
        }
    }
    
    private void onClickAttachCamera(){
        String timeStamp    = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        photoFileName       = "IMG_" + timeStamp + ".jpg";
        Intent intent       = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utility.getPhotoFileCamera(photoFileName));
        startActivityForResult(intent, PICK_CAMERA_REQUEST);
    }

    private void onClickAttachGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.label_select_image)), PICK_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_GALLERY_REQUEST ) {
                Uri selectedImageUri    = data.getData();
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    mImagePayment.setImageBitmap(mBitmap);
                    ByteArrayOutputStream bos 	= new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
                    mBitmapData                 = bos.toByteArray();
                } catch (IOException e) {}
            }
        }
        if (requestCode == PICK_CAMERA_REQUEST && resultCode == RESULT_OK) {
                Uri takenPhotoUri   = Utility.getPhotoFileCamera(photoFileName);
                mBitmap             = Utility.rotateBitmapOrientation(takenPhotoUri.getPath());
                mImagePayment.setImageBitmap(mBitmap);
                ByteArrayOutputStream bos 	= new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
                mBitmapData                 = bos.toByteArray();
        }
    }

    private void uploadImage(){
        if (Utility.isNetworkConnected()) {
            OrderTask.UploadPageAttachment(PaymentActivity.this, orderID, mType, rekening, nominal, "", mBitmapData, UploadSuccess);
        }else
            Dialog.InformationDialog(PaymentActivity.this, getResources().getString(R.string.msg_internet_problem));
    }

    private Runnable UploadSuccess = new Runnable() {
        public void run() {
            Toast.makeText(PaymentActivity.this, getResources().getString(R.string.msg_confirm_success), Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    };
}

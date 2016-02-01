package com.egneese.sellers.dashboardfragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.egneese.sellers.R;
import com.egneese.sellers.activities.PayMobileNumberActivity;
import com.egneese.sellers.activities.RequestMoneyActivity;
import com.egneese.sellers.activities.ScanQRCodeActivity;
import com.egneese.sellers.adapters.DashboardListAdapter;
import com.egneese.sellers.asynctasks.QRInfoAsyncTask;
import com.egneese.sellers.dto.DashboardListItemDTO;
import com.egneese.sellers.dto.MessageCustomDialogDTO;
import com.egneese.sellers.dto.QRDataDTO;
import com.egneese.sellers.dto.RequestDTO;
import com.egneese.sellers.dto.SellerDTO;
import com.egneese.sellers.security.Encryption;
import com.egneese.sellers.ui.SnackBar;
import com.egneese.sellers.util.RequiredDTOFactory;
import com.egneese.sellers.util.SessionDTODFactory;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.neopixl.pixlui.components.textview.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Dell on 1/8/2016.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    @InjectView(R.id.listDashboard)
    ListView listDashboard;
    LinearLayout headerView;
    private Button btnSendMoney, btnRequestMoney;
    @InjectView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;
    private static TextView txtAmount;
    private static final int SELECT_PHOTO = 100;
    private final int SCAN_QR = 50;
    //private SellerDTO sellerDTO;
    private final int REQUEST_MONEY = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = (LinearLayout) layoutInflater.inflate(R.layout.fragment_dashboard_header, null);
        //sellerDTO = SessionDTODFactory.getSellerDTO(getActivity());
        ButterKnife.inject(this, rootView);
        populate();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //sellerDTO = SessionDTODFactory.getSellerDTO(getActivity());
        //txtAmount.setText(getActivity().getResources().getString(R.string.Rs) + "  " + sellerDTO.getWallet().getBalance());
    }

    private void populate() {
        List<DashboardListItemDTO> dashboardListItemDTOs = new ArrayList<>();
        DashboardListAdapter dashboardListAdapter = new DashboardListAdapter(getActivity(), dashboardListItemDTOs);

        DashboardListItemDTO dashboardListItemDTO = new DashboardListItemDTO();
        dashboardListItemDTO.setDisc(getResources().getString(R.string.home_disc_1));
        dashboardListItemDTO.setTitle(getResources().getString(R.string.home_title_1));
        dashboardListItemDTO.setImage(R.mipmap.ic_profile_customers);

        dashboardListItemDTOs.add(dashboardListItemDTO);
        dashboardListItemDTO = new DashboardListItemDTO();
        dashboardListItemDTO.setDisc(getResources().getString(R.string.home_disc_2));
        dashboardListItemDTO.setTitle(getResources().getString(R.string.home_title_2));
        dashboardListItemDTO.setImage(R.mipmap.ic_profile_offers);

        dashboardListItemDTOs.add(dashboardListItemDTO);
        dashboardListItemDTO = new DashboardListItemDTO();
        dashboardListItemDTO.setDisc(getResources().getString(R.string.home_disc_3));
        dashboardListItemDTO.setTitle(getResources().getString(R.string.home_title_3));
        dashboardListItemDTO.setImage(R.mipmap.ic_profile_products);

        dashboardListItemDTOs.add(dashboardListItemDTO);


        btnSendMoney = (Button) headerView.findViewById(R.id.btnSendMoney);
        btnRequestMoney = (Button) headerView.findViewById(R.id.btnRequestMoney);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnSendMoney.setBackgroundResource(R.drawable.ripple_white);
            btnRequestMoney.setBackgroundResource(R.drawable.ripple_white);
        }

        btnRequestMoney.setOnClickListener(this);
        btnSendMoney.setOnClickListener(this);
        listDashboard.setAdapter(dashboardListAdapter);
        listDashboard.addHeaderView(headerView);

        txtAmount = (TextView) headerView.findViewById(R.id.txtAmount);
       // txtAmount.setText(getActivity().getResources().getString(R.string.Rs) + "  " + sellerDTO.getWallet().getBalance());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendMoney:
                showMenuSheet(MenuSheetView.MenuType.GRID);
                break;
            case R.id.btnRequestMoney:
                Intent intent = new Intent(getActivity(), RequestMoneyActivity.class);
                startActivityForResult(intent, REQUEST_MONEY);
                break;

        }
    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(getActivity(), menuType, "Pay using", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }

                        if (item.getItemId() == R.id.action_pay_id) {
                            Intent intent = new Intent(getActivity(), PayMobileNumberActivity.class);
                            startActivity(intent);
                        } else if (item.getItemId() == R.id.action_pay_qr) {
                            Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", "key");
                            intent.putExtras(bundle);
                            startActivityForResult(intent, SCAN_QR);
                        } else if (item.getItemId() == R.id.action_pay_qr_image) {
                            pickImage();
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_dashboard_fragment);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void pickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                try {
                    int width = bitmap.getWidth(), height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    bitmap.recycle();
                    bitmap = null;
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    MultiFormatReader reader = new MultiFormatReader();
                    Result result = reader.decode(bBitmap);
                    String dataFromQR = result.toString();
                    getData(dataFromQR);
                } catch (Exception e) {
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle(getActivity().getResources().getString(R.string.oops));
                    messageCustomDialogDTO.setButton(getActivity().getResources().getString(R.string.ok));
                    messageCustomDialogDTO.setMessage(getActivity().getResources().getString(R.string.invalid_qr));
                    messageCustomDialogDTO.setContext(getActivity());
                    SnackBar.show(getActivity(), messageCustomDialogDTO);
                }

            }
        }else if(requestCode == SCAN_QR){
            if (resultCode == Activity.RESULT_OK) {
                getData(data.getStringExtra("qrDataDTO"));
            }
        }
    }

    public static void updateBalance(SellerDTO sellerDTO){
        txtAmount.setText("\u20B9  " + sellerDTO.getWallet().getBalance());
    }

    private void getData(String dataFromQR) {
        try {
            String data = Encryption.decrypt(dataFromQR);

            Gson gson = new Gson();
            QRDataDTO qrDataDTO = gson.fromJson(data, QRDataDTO.class);
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setRequestId(qrDataDTO.getRequestId());

            QRInfoAsyncTask qrInfoAsyncTask = new QRInfoAsyncTask(getActivity(), RequiredDTOFactory.getObject(getActivity()), requestDTO, headerView);
            qrInfoAsyncTask.execute();

        } catch (Exception e) {
            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
            messageCustomDialogDTO.setTitle(getActivity().getResources().getString(R.string.oops));
            messageCustomDialogDTO.setButton(getActivity().getResources().getString(R.string.ok));
            messageCustomDialogDTO.setMessage(getActivity().getResources().getString(R.string.invalid_qr));
            messageCustomDialogDTO.setContext(getActivity());
            SnackBar.show(getActivity(), messageCustomDialogDTO);
        }


        /*QRInfoAsyncTask qrInfoAsyncTask = new QRInfoAsyncTask(getActivity(), RequiredDTOFactory.getObject(getActivity()), requestDTO);
        qrInfoAsyncTask.execute();*/

        /*final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity()).setTitle("Pay Confirmation").setMessage("Madarchod pay kar de nahi to gaand maar lege !! \n Regards : \n Abhishek Sharma (CEO EggWallet)");
        mMaterialDialog.setPositiveButton("Pay", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });

        mMaterialDialog.setNegativeButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });

        mMaterialDialog.show();*/

    }

}

package com.huntloc.handheldpv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PersonnelFragment extends Fragment {
    public static final String ARG_RESPONSE = "response";
    private OnPersonnelFragmentInteractionListener mListener;
    private String response;
    TextView textView_Personnel;
    TextView textView_PBIP_date, textView_HazardousGoods_date,
            textView_PortSecurity_date;
     ImageView imageView_Portrait;
    ImageView imageView_PBIP, imageView_HazardousGoods, imageView_PortSecurity;
    public PersonnelFragment() {
        // Required empty public constructor
    }

   public static PersonnelFragment newInstance(String response) {
        PersonnelFragment fragment = new PersonnelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESPONSE, response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private Date parseString(String date) {
        String value = date.replaceFirst("\\D+([^\\)]+).+", "$1");
        String[] timeComponents = value.split("[\\-\\+]");
        long time = Long.parseLong(timeComponents[0]);

		/*  int timeZoneOffset = Integer.valueOf(timeComponents[1]) * 36000; if
		  (value.indexOf("-") > 0) { timeZoneOffset *= -1; } time +=
		  timeZoneOffset;*/

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();

    }

    private void showTraining(String dateString, TextView textView,
                              ImageView imageView) {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMMM yyyy");
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        Calendar monthAhead = Calendar.getInstance();
        monthAhead.add(Calendar.MONTH, 1);
        monthAhead.add(Calendar.DATE, 1);
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        if (dateString != null) {
            //Date trainingDate = parseString(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parseString(dateString));
            textView.setText(" " + newDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
            //Log.d("ultimo dia", newDateFormat.format(parseString(dateString)));
            //Log.d("dia vencido", newDateFormat.format(calendar.getTime()));
            //Log.d("today", newDateFormat.format(today.getTime()) );
            if (calendar.getTime().before(today.getTime())) {
                showTrainingStatus(imageView, R.drawable.ic_error,
                        R.color.error);
            } else {
                if (calendar.getTime().before(monthAhead.getTime())) {// a un mes
                    showTrainingStatus(imageView, R.drawable.ic_warning,
                            R.color.warning);
                } else {
                    showTrainingStatus(imageView, R.drawable.ic_check,
                            R.color.check);
                }
            }
        } else {
            textView.setText(" " + "No hay información");
            showTrainingStatus(imageView, R.drawable.ic_question,
                    R.color.warning);
        }
    }

    private void showTrainingStatus(ImageView imageView, int iconResource,
                                    int colorResource) {
        imageView.setImageResource(iconResource);
        imageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), colorResource));
    }

    private void showPersonnel(Personnel personnel) {
        if (personnel != null) {
             String outputData;
            outputData = "Credencial: " + personnel.getPrintedCode() + "\r\n";
            outputData += "Nombre: " + personnel.getName() + "";

            if(personnel.getPBIPCode()!=null){
                outputData += "\r\nNúmero Tarjeta PBIP: " + personnel.getPBIPCode() + "";
                imageView_Portrait.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), personnel.getPBIPColorCode()));
            }

            textView_Personnel.setText(outputData);
            showTraining(personnel.getPBIP(), textView_PBIP_date,
                    imageView_PBIP);
            showTraining(personnel.getHazardousGoods(),
                    textView_HazardousGoods_date, imageView_HazardousGoods);
            showTraining(personnel.getPortSecurity(),
                    textView_PortSecurity_date, imageView_PortSecurity);
            byte[] byteArray;
            Bitmap bitmap;
            try {
                byteArray = Base64.decode(personnel.getPortrait(), 0);
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imageView_Portrait.setImageBitmap(bitmap);
            } catch (Exception ex) {
            }

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setTitle("Jetty Access Control");
            alertDialogBuilder.setMessage("Badge no tiene acceso");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.create().show();
            textView_Personnel.setText("Credencial:\r\nNombre:");
            imageView_Portrait.setImageBitmap(null);

            textView_PBIP_date.setText("");
            imageView_PBIP.setImageBitmap(null);
            textView_HazardousGoods_date.setText("");
            imageView_HazardousGoods.setImageBitmap(null);
            textView_PortSecurity_date.setText("");
            imageView_PortSecurity.setImageBitmap(null);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personnel,
                container, false);
        if (getArguments() != null) {
            response = getArguments().getString(ARG_RESPONSE);
        }
        Log.d("Response",response);
        textView_Personnel = (TextView) view.findViewById(R.id.textView_Personnel);
        imageView_Portrait = (ImageView) view.findViewById(R.id.imageView_Portrait);
        textView_PBIP_date = (TextView) view.findViewById(R.id.textView_PBIP_date);
        textView_HazardousGoods_date = (TextView) view.findViewById(R.id.textView_HazardousGoods_date);
        textView_PortSecurity_date = (TextView) view.findViewById(R.id.textView_PortSecurity_date);

        imageView_PBIP = (ImageView) view.findViewById(R.id.imageView_PBIP);
        imageView_HazardousGoods = (ImageView) view.findViewById(R.id.imageView_HazardousGoods);
        imageView_PortSecurity = (ImageView) view.findViewById(R.id.imageView_PortSecurity);

        SQLiteHelper db = new SQLiteHelper(getContext());
        showPersonnel(db.getPersonnel(response));

     return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPersonnelFragmentInteractionListener) {
            mListener = (OnPersonnelFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnPersonnelFragmentInteractionListener {
        void onPersonnelFragmentInteraction();
    }
}
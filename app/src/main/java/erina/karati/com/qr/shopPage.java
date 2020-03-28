package erina.karati.com.qr;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class shopPage extends AppCompatActivity
{
    private Button scanBtn;
    private String str;
    private int sum;
    private TextView textViewName, textViewAddress;
    private Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page);

        scanBtn = (Button) findViewById(R.id.scanBtn);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        final Activity activity = this;

        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(startIntent);
            }
        });

        //attaching onclick listener
        scanBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // Scan
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }

        });

        final TextView sumTxt = (TextView) findViewById(R.id.textViewPrice);


        Button addBtn = (Button) findViewById(R.id.addBtn);
        // Initializing a new String Array
        String[] items = new String[]
                {
                "ITEMS"
        };
        // Create a List from String Array elements
        final List<String> items_list = new ArrayList<String>(Arrays.asList(items));
        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, items_list);

        flag = false;
        addBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(flag==true)
                {
                    items_list.add(str);
                    arrayAdapter.notifyDataSetChanged();

                    sum = sum + Integer.parseInt(textViewAddress.getText().toString());
                    sumTxt.setText(Integer.toString(sum));
                }
                else
                {
                    Toast.makeText(shopPage.this, "Scan an item first", Toast.LENGTH_LONG).show();
                }
            }
        });

        final ListView lv = (ListView) findViewById(R.id.listView);
        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            String val;
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {

                val = items_list.get(position);
                val = val.replaceAll("\\D+","");
                sum = sum - Integer.parseInt(val);
                sumTxt.setText(Integer.toString(sum));
                items_list.remove(position);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(shopPage.this, "Item Deleted", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        Button checkBtn = (Button) findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(new View.OnClickListener()
        {
            String total;
            @Override
            public void onClick(View view)
            {
                if(sum!=0)
                {
                    total = "Total - Rs." + Integer.toString(sum);
                    items_list.add(total);
                    Intent startIntent = new Intent(getApplicationContext(), billPage.class);
                    startIntent.putExtra("items_list", (Serializable) items_list);
                    startActivity(startIntent);
                }
                else
                {
                    Toast.makeText(shopPage.this, "Add an item to your cart", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {

                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();

            }
            else
            {
                try
                {
                    JSONObject json = new JSONObject(result.getContents());
                    textViewName.setText(json.getString("name"));
                    textViewAddress.setText(json.getString("price"));
                    str = textViewName.getText().toString();
                    flag = true;

                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

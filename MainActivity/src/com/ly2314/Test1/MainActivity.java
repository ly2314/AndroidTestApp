package com.ly2314.Test1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings.Secure;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.PushService;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Parse.initialize(this, "UL5aUW60NIRAoOzKCK0Oe9ddu8jRrkQKZ61WJT2l", "P5e07cjyEJJAXdvcpfK0nuSsO7DPy5f2ISoXXlgx");
        PushService.setDefaultPushCallback(this, ActionBarActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
		PushService.subscribe(this, "all", ActionBarActivity.class);
		PushService.subscribe(this, "id_" + getDeviceId(), MainActivity.class);

		register();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void Click(View view)
    {
        final EditText txt = (EditText)findViewById(R.id.editText1);
        Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Title");
        alert.setMessage(txt.getText().toString());
        alert.show();
    }*/

	private String getDeviceId() {
		return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}
	
	private void register() {
		String id = getDeviceId();
		ParseObject object = new ParseObject("DeviceId");
		object.put("deviceId", id);
		object.saveInBackground();
	}
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        private Button _button1;
        private EditText _textbox1;
        private CheckBox _checkbox1;
        private SharedPreferences sp;
        private SharedPreferences.Editor editor;
        private Spinner _spinner;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        	sp = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        	editor = sp.edit();
        			
            _textbox1 = (EditText)rootView.findViewById(R.id.editText1);
            _textbox1.setHint("Enter Something");
            _textbox1.setOnKeyListener(new OnKeyListener()
            {
            	@Override
            	public boolean onKey(View v, int keyCode, KeyEvent event)
            	{
            		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
            		{
            			SendMessage();
            			return true;
            		}
            		else
            		{
        				editor.putString("text", _textbox1.getText().toString());
        				editor.commit();
            		}
            		return false;
            	}
            });   
            
            _button1 = (Button)rootView.findViewById(R.id.button1);
            _button1.setText("Enter");
            _button1.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v)
            	{
            		SendMessage();
            	}
            });
            
            _checkbox1 = (CheckBox)rootView.findViewById(R.id.checkBox1);
            _checkbox1.setOnCheckedChangeListener(new OnCheckedChangeListener()
            {

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if (arg1)
					{
						_textbox1.setTransformationMethod(PasswordTransformationMethod.getInstance());
					}
					else
					{
						_textbox1.setTransformationMethod(SingleLineTransformationMethod.getInstance());
					}
				}            	
            });
            
            _spinner = (Spinner) rootView.findViewById(R.id.spinner1);
            loadDeviceId();
            
            return rootView;
        }
        
        private void loadDeviceId()
        {
        	String deviceId = (String) _spinner.getSelectedItem();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("DeviceId");
			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					Set<String> set = new HashSet<String>();
					for (int i = 0; i < objects.size(); i++) {
						set.add(objects.get(i).getString("deviceId"));
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item, set.toArray(new String[set.size()]));
						_spinner.setAdapter(adapter);
					}
			});
		}        	 
        
        public void SendMessage()
        {
    		String str = _textbox1.getText().toString();
    		if (str.length() == 0)
    		{
    			str = "Please enter something!";
    			return;
    		}
    		if (_checkbox1.isChecked() == true)
    		{
    			int i = str.length();
    			str = "";
    			for (int j = 0; j < i; ++j)
    			{
    				str += "*";
    			}
    		}
    		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

    		_textbox1.setText("");
    		
    		String deviceId = (String) _spinner.getSelectedItem();
    		
    		ParsePush push = new ParsePush();
    		push.setChannel("id_" + deviceId);
    		push.setMessage(str);
    		push.sendInBackground();
    		
    		Intent _i = new Intent();
    		_i.setClass(getActivity(), MessageActivity.class);
    		_i.putExtra("text", _textbox1.getText().toString());
    		getActivity().startActivity(_i);    		
        }
    }

}

package com.erinix.elogo;

import java.util.StringTokenizer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void parseCommands(View view) {
		EditText commandText = (EditText) findViewById(R.id.commandText);
		String commands = commandText.getText().toString();
		StringTokenizer commandTokens = new StringTokenizer(commands);
		commandParser parser = new commandParser();
		eLogoView logoView = (eLogoView) findViewById(R.id.logoView);
		parser.parseCommands(commandTokens, logoView);
	}
}

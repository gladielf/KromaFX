package com.kromafx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements NumberPicker.OnValueChangeListener{

	//private ListView drawer;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	//private ArrayList<DrawerItems> drawerList;
	DrawerAdapter drawerAdapter;
	ArrayList<TakesItem> takesList;

    ActionBar actionBar;

    //static Dialog dialog ;
    static TableLayout tableLayout;
    static TableRow tableRow;
    static TableRow splitterRow;
    static TextView textViewScene;
    static TextView textViewTake;
    static TextView textViewTrack;
    static FrameLayout splitterLine;
    int lastScene = 1; 
    int lastTake = 0;
    int lastTrack = 0;
    
    int actualScene;
 	int actualTake;
 	int actualTrack;
 	
 	String filename;
    String fileSelected;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.main_layout);

        actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		//Se establece el nombre del archivo que se utilizará
		setFilename();
		
		takesList = new ArrayList<TakesItem>();
		
		ArrayList<DrawerItems> drawerList = new ArrayList<DrawerItems>();
		
		drawerList.add(new DrawerItems(getString(R.string.drawer_option_save), R.drawable.ic_action_save));
		drawerList.add(new DrawerItems(getString(R.string.drawer_option_load), R.drawable.ic_action_paste));
		drawerList.add(new DrawerItems(getString(R.string.drawer_option_share), R.drawable.ic_action_share));
	
		
		tableLayout = (TableLayout)findViewById(R.id.TableLayout1);

		ListView drawer = (ListView) findViewById(R.id.drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		drawerAdapter = new DrawerAdapter(this, drawerList);
		drawer.setAdapter(drawerAdapter);
		
		drawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch (position) {
				case 0:
					//GUARDAR
					if(filename!=null) {
                        saveTakes(filename, Constants.FileDirectory);
                    }
                    else {
                        setFilename();
                        //TODO Si falla hay que modificar algo: un posible parametro para setfilename()?
                        saveTakes(filename, Constants.FileDirectory);
                        /*if(filename!=null) {
                            saveTakes(filename, Constants.FileDirectory);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),R.string.messageNoFile,Toast.LENGTH_SHORT).show();
                        }*/
                    }
					break;
					
				case 1:
					//CARGAR
					loadTakes();
					break;
					
				case 2:
					//COMPARTIR
					shareTakes();
					break;

				default:
					break;
				}
				drawerLayout.closeDrawers();

			}
		});
		
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		toggle = new ActionBarDrawerToggle(
				this, // Activity
				drawerLayout, // Panel del Navigation Drawer
				R.drawable.ic_drawer, // Icono que va a utilizar
				R.string.app_name, // Descripcion al abrir el drawer
				R.string.app_name // Descripcion al cerrar el drawer
				){
			public void onDrawerClosed(View view) {
				// Drawer cerrado
                actionBar.setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// Drawer abierto
                actionBar.setTitle(R.string.app_name_drawer_open);
				invalidateOptionsMenu(); 
			}
		};

		drawerLayout.setDrawerListener(toggle);
        actionBar.show();



    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (toggle.onOptionsItemSelected(item)) {
			return true;
		}
    	switch (item.getItemId()) {
		case R.id.menu_new:
			dialogNewRow();
			return true;
			
		case R.id.menu_remove:
			removeLastRow();
			//Toast.makeText(getApplicationContext(),"Aún NO puede borrar filas",Toast.LENGTH_LONG).show();
			return true;
			
		case R.id.menu_note:
            //setFilename();
			dialogNewNote();
			//Toast.makeText(getApplicationContext(),"Aún NO puedes añadir notas",Toast.LENGTH_LONG).show();
			return true;

			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}
	
	
	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {	
		
		Log.i("value is",""+newVal);
	}
	
	/*
	 * Funcion para nueva linea
	 */
	public void dialogNewRow(){
		
        tableRow = new TableRow(this);
        splitterRow = new TableRow(this);
        splitterLine= new FrameLayout(this);
		textViewScene = new TextView(this);
	 	textViewTake = new TextView(this);
	 	textViewTrack = new TextView(this);
	 	
		
		final Dialog dialogo = new Dialog(MainActivity.this);
        dialogo.setTitle(R.string.dialogTitle);
        dialogo.setContentView(R.layout.dialog_new_frame);
        dialogo.setCanceledOnTouchOutside(false);
        Button buttonNewFrameCancel = (Button) dialogo.findViewById(R.id.buttonNewFrameCancel);
        Button buttonNewFrameOk = (Button) dialogo.findViewById(R.id.buttonNewFrameOk);
        
        final NumberPicker pikerScene = (NumberPicker) dialogo.findViewById(R.id.SceneNumberPicker);
        pikerScene.setMaxValue(100);
        pikerScene.setMinValue(1);
        pikerScene.setWrapSelectorWheel(false);
        pikerScene.setOnValueChangedListener(this);
        pikerScene.setValue(lastScene);
        
        final NumberPicker pikerTake = (NumberPicker) dialogo.findViewById(R.id.TakeNumberPicker);
        pikerTake.setMaxValue(500);
        pikerTake.setMinValue(0);
        pikerTake.setWrapSelectorWheel(false);
        pikerTake.setOnValueChangedListener(this);
        pikerTake.setValue(lastTake);
        
        final NumberPicker pikerTrack = (NumberPicker) dialogo.findViewById(R.id.TrackNumberPicker);
        pikerTrack.setMaxValue(500);
        pikerTrack.setMinValue(0);
        pikerTrack.setWrapSelectorWheel(false);
        pikerTrack.setOnValueChangedListener(this);
        pikerTrack.setValue(lastTrack);
        
        
        buttonNewFrameOk.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
              
            tableRow.setPadding(5, 5, 5, 5);

            actualScene = pikerScene.getValue();
    	 	actualTake = pikerTake.getValue();
    	 	actualTrack = pikerTrack.getValue();
            
 	        textViewScene.setText(String.valueOf(actualScene));
 	        textViewScene.setGravity(Gravity.CENTER);
 	        lastScene = pikerScene.getValue();
 	        
 	        textViewTake.setText(String.valueOf(actualTake));
 	        textViewTake.setGravity(Gravity.CENTER);
 	        lastTake = pikerTake.getValue();
 	        
 	        textViewTrack.setText(String.valueOf(actualTrack));
 	        textViewTrack.setGravity(Gravity.CENTER);
 	        lastTrack = pikerTrack.getValue();

 	        tableRow.addView(textViewScene);
 	        tableRow.addView(textViewTake);
 	        tableRow.addView(textViewTrack);          

 	        
 	        splitterRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
 	        TableRow.LayoutParams splitterLineParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,2);
 	        splitterLineParams.span = 3; //Este valor indica el numero de columnas que ocupara la linea
 	        splitterLine.setBackgroundColor(Color.parseColor("#000000"));
 	        splitterRow.addView(splitterLine, splitterLineParams);

 	        
 	        tableLayout.addView(tableRow); 
 	        tableLayout.addView(splitterRow);


 	        takesList.add(new TakesItem(actualScene, actualTake, actualTrack));
            if (filename != null){
                saveTakes(filename , Constants.FileDirectory);
            }


 	        
 	        dialogo.dismiss();
             
          }    
         });
        buttonNewFrameCancel.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
             dialogo.dismiss();
          }    
         });
      dialogo.show();
		
	}
	
	
	/*
	 * Funcion para eliminar la ULTIMA linea
	 */
	public void removeLastRow(){
		
		if(tableLayout.getChildCount()>2){
			tableLayout.removeViews(tableLayout.getChildCount()-2,2); //Borra las DOS ultimas lineas (Datos y separadora)
			takesList.remove(takesList.size()-1);
			saveTakes(filename , Constants.FileDirectory);
		}else{
            Toast.makeText(getApplicationContext(), "No hay toma que eliminar", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/*
	 * Funcion para nueva nota
	 */
	public void dialogNewNote(){
		
		
		final Dialog dialogNote = new Dialog(MainActivity.this);
        dialogNote.setTitle(R.string.dialogNoteTitle);
        dialogNote.setContentView(R.layout.dialog_new_note);
        dialogNote.setCanceledOnTouchOutside(false);
        Button buttonNoteCancel = (Button) dialogNote.findViewById(R.id.buttonNoteCancel);
        Button buttonNoteOk = (Button) dialogNote.findViewById(R.id.buttonNoteOk);
        TextView textviewtest= (TextView) dialogNote.findViewById(R.id.textViewtest);
  
        Time today =new Time(Time.getCurrentTimezone());
        today.setToNow();
        String date= today.format("%d-%m-%Y");
        textviewtest.setText(date+"");
        
        buttonNoteOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				dialogNote.dismiss();
				// TODO Auto-generated method stub
				
			}
		});
        
        
        buttonNoteCancel.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
        	 dialogNote.dismiss();
          }    
         });
       
        dialogNote.show();
		
	}
	
	/*
	 * Funcion para guardar tomas
	 */
	public void saveTakes(String filename, String dirFile){
		//TODO
		String ext_storage_state = Environment.getExternalStorageState();
		File mediaStorage = new File(Environment.getExternalStorageDirectory() + Constants.FileDirectory);
		if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			if (!mediaStorage.exists()){
				if(mediaStorage.mkdirs()){
                    Log.i(Constants.LogTag,"Directorio creado correctamente");
                }
			}
			String filetext01 = "| Escena | Toma | Pista |\n";
			byte[] dataHead = filetext01.getBytes();
			
 			
			File myFile = new File(Environment.getExternalStorageDirectory() + dirFile ,filename);
			if(!myFile.exists()){
				try {
					if(myFile.createNewFile()){
                        Log.i(Constants.LogTag,"Archivo creado correctamente");
                    }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				
				FileOutputStream fos = new FileOutputStream(myFile);
				try{
					fos.write(dataHead);
					fos.flush();
					if(takesList.size()!=0){
						//Log.i(Constants.LogTag, filetext01);
						for (int i =0 ; i< takesList.size();i++){
							String format = "|%1$-8s|%2$-6s|%3$-7s|\n";
							String takeLine = String.format(format, StringUtils.center(Integer.toString(takesList.get(i).getScene()), 8), StringUtils.center(Integer.toString(takesList.get(i).getTake()), 6), StringUtils.center(Integer.toString(takesList.get(i).getTrack()),7));
							//Log.i(Constants.LogTag, takeLine); 
					        	byte[] dataTakes = takeLine.getBytes();
								fos.write(dataTakes);
								fos.flush();
					        }
					}else{
						Log.i(Constants.LogTag, "La take lista esta vacia");
					}
					
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		else {
			Toast.makeText(getApplicationContext(), "Tarjeta de memoria no encontrada", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	/*
	 * Funcion para cargar tomas
	 */
	public void loadTakes(){
		//TODO
        // Este dialog mostrara una lista con los archivos que hay en el directorio (solo txt si es posible)
        // y
        fileSelected = null;
        final Dialog dialogLoadFile = new Dialog(MainActivity.this);
        dialogLoadFile.setTitle(R.string.dialogLoadFileTitle);
        dialogLoadFile.setContentView(R.layout.dialog_load_file);
        dialogLoadFile.setCanceledOnTouchOutside(false);

        Button buttonLoadFileCancel = (Button) dialogLoadFile.findViewById(R.id.buttonLoadFileCancel);
        Button buttonLoadFileOk = (Button) dialogLoadFile.findViewById(R.id.buttonLoadFileOk);
        ListView listViewLoadFile = (ListView) dialogLoadFile.findViewById(R.id.listViewLoadFile);


        String path = Environment.getExternalStorageDirectory().toString() + Constants.FileDirectory;
        Log.i(Constants.LogTag, "Path: " + path );
        File file = new File(path);
        File fileList[] = file.listFiles();
        int fileNumber = fileList.length;
        final String[] fileNames = new String[fileNumber];
        for (int i = 0; i<fileList.length; i++){
            fileNames[i]= fileList[i].getName();
            //Log.i(Constants.LogTag, "nombre archivo: " + fileList[i].getName());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,android.R.id.text1,fileNames);
        listViewLoadFile.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewLoadFile.setAdapter(adapter);

        listViewLoadFile.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileSelected = fileNames[position];

            }
        });

        buttonLoadFileCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoadFile.dismiss();
            }
        });

        buttonLoadFileOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileSelected== null){
                    Toast.makeText(getApplicationContext(),R.string.dialogLoaDFileNoFileSelected,Toast.LENGTH_SHORT).show();
                }
                else {
                    //Se carga el archivo seleccionado
                    Log.i(Constants.LogTag,"se va a cargar el archivo: " + fileSelected);
                    Toast.makeText(getApplicationContext(),"se va a cargar el archivo: " + fileSelected,Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialogLoadFile.show();
		//Toast.makeText(getApplicationContext(), "Vamos a cargar las tomas del dia elegido", Toast.LENGTH_SHORT).show();
		
	}
	
	/*
	 * Funcion para compartir tomas
	 */
	public void shareTakes(){
		//TODO
		Toast.makeText(getApplicationContext(), "Vamos a compartir las tomas de hoy", Toast.LENGTH_SHORT).show();
		
	}
	
	public void setFilename(){
		//TODO
		//Será un dialog nada más iniciar la aplicación y comprobara la existencia de dicho archivo y de ser así pedira si se reescribe, no o su lo desea cargar para seguir desde donde estaba
		
		/*Time today =new Time(Time.getCurrentTimezone());
        today.setToNow();
		filename = today.format("KFX_%Y%m%d"+ Constants.TxtExtension);
		*/
		
		final Dialog dialogFileName = new Dialog(MainActivity.this);
        dialogFileName.setTitle(R.string.dialogFileNameTitle);
        dialogFileName.setContentView(R.layout.dialog_file_name);
        dialogFileName.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogFileName.setCanceledOnTouchOutside(false);
        Button buttonFileNameCancel = (Button) dialogFileName.findViewById(R.id.buttonFileNameCancel);
        Button buttonFileNameOk = (Button) dialogFileName.findViewById(R.id.buttonFileNameOk);
        Button buttonFileNameToday = (Button) dialogFileName.findViewById(R.id.buttonFileNameToday);
        final EditText editTextFileName = (EditText) dialogFileName.findViewById(R.id.editTextFileName);
        
        buttonFileNameToday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Time today = new Time(Time.getCurrentTimezone());
		        today.setToNow();
		        String defaultFileName = today.format("KFX_%Y%m%d");
				editTextFileName.setText(defaultFileName);
                editTextFileName.setSelection(defaultFileName.length());
			}
		});
        
        
        buttonFileNameOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String name = editTextFileName.getText().toString();
				if (name == null || name.isEmpty() ){
					Toast.makeText(MainActivity.this, R.string.no_file_name, Toast.LENGTH_SHORT).show();
				}
				else{
					final String newFilename = editTextFileName.getText().toString()+Constants.TxtExtension;
					Log.i(Constants.LogTag, "El nombre del archivo es: " + newFilename);
                    File file = new File(Environment.getExternalStorageDirectory() + Constants.FileDirectory ,newFilename);
                    if(file.exists()){
                        Log.i(Constants.LogTag,"el archivo ya existe: " + newFilename);
                        new AlertDialog.Builder(MainActivity.this)
                        //.setTitle(getResources().getString(R.string.dialogOverwriteFileTitle))
                        .setMessage(R.string.dialogOverwriteFile)
                        .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO se cierra el dialog para sobreescribir y hacer cambios
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO se cierra el dialog y ¿se borra el archivo?
                                filename = newFilename;
                                dialog.dismiss();
                                dialogFileName.dismiss();
                            }
                        }).show();
                    }
                    else {
                        filename = newFilename;
                        dialogFileName.dismiss();
                    }
				}
				
				// TODO Auto-generated method stub
				//se comprobara la existencia del archivo y de ser así pedira si se reescribe, no o su lo desea cargar para seguir desde donde estaba
				
			}
		});
        
        
        buttonFileNameCancel.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
        	 //TODO
        	 //al pulsar cancelar se debera cerrar la app
             new AlertDialog.Builder(MainActivity.this)
             .setMessage(R.string.dialogNoFile)
             .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             }).setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                     dialogFileName.dismiss();
                 }
             }).show();
        	 //dialogFileName.dismiss();
          }    
         });
       
        dialogFileName.show();
		
		
	}
}

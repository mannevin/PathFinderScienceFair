package com.example.vmann.mapbox;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.SyncStateContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.augugrumi.ghioca.listener.UploadingListener;
import com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener;
import com.augugrumi.ghioca.utility.ConvertUriToFilePath;
import com.augugrumi.ghioca.utility.NetworkingUtility;
import com.augugrumi.ghioca.utility.SavingUtility;
import com.augugrumi.ghioca.utility.SearchType;
import com.augugrumi.ghioca.utility.SharedPreferencesManager;
import com.augugrumi.ghioca.utility.UploadingUtility;
import com.example.vmann.mapbox.dialog.AIButtonSampleActivity;
import com.example.vmann.mapbox.dialog.AIDialogSampleActivity;
import com.example.vmann.mapbox.dialog.AITextSampleActivity;
import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.internal.manager.impl.Camera1Manager;
import com.github.florent37.camerafragment.internal.ui.BaseAnncaFragment;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener,
        PermissionsListener, MapboxMap.OnMapClickListener, AdapterView.OnItemClickListener {
    private static boolean isStarting = true;

    public static final String FRAGMENT_TAG = "camera";
    private static final int SELECT_PICTURE = 100;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;

    @BindView(R.id.flash_switch_view)
    FlashSwitchView flashSwitchView;
    @BindView(R.id.front_back_camera_switcher)
    CameraSwitchView cameraSwitchView;
    @BindView(R.id.record_button)
    RecordButton recordButton;
    @BindView(R.id.pick_file)
    ImageButton pickFile;
    @BindView(R.id.menu_button)
    ImageButton buttonMenu;

    @BindView(R.id.record_duration_text)
    TextView recordDurationText;
    @BindView(R.id.record_size_mb_text)
    TextView recordSizeText;

    @BindView(R.id.cameraLayout)
    View cameraLayout;
    @BindView(R.id.addCameraButton)
    View addCameraButton;

    private DialogFragment wifiFragment;
    private Drawer menu;

    private static final String TAG = "MNISTActivity";
    private static final int PIXEL_WIDTH = 28;
    private static final String MODEL_FILE_MNIST = "file:///android_asset/_frozen_mnistTFonAndroid.pb";
    private static final String MODEL_FILE_CONVNET = "file:///android_asset/_frozen_convnetTFonAndroid.pb";
    private static final String[] INPUT_NODES = {"modelInput"};
    private static final String[] OUTPUT_NODES = {"modelOutput"};
    private static final int[] INPUT_DIM = {1, PIXEL_WIDTH * PIXEL_WIDTH};
    //tensorflow
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    //how many pixels to capture
    private static final int PIXEL_WIDTH = 28;
    //Create a BroadcastReciever for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReciever1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //When discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    //Buttons and text editors for text to speech
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };
    private TextView mResultTextMnist;
    private TextView mResultTextConvnet;
    private float mLastX;
    private float mLastY;
    private DrawModel mModel;
    private DrawView mDrawView;
    private PointF mTmpPiont = new PointF();
    // Tensorflow related code
    private TensorFlowInferenceInterface inferenceInterface_mnist;
    private TensorFlowInferenceInterface inferenceInterface_convnet;
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private TextView mResultText;
    private float mLastX;
    private float mLastY;
    private DrawModel mModel;
    private DrawView mDrawView;
    private static final String TAG = "MainActivity";
    private final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private final int REQUEST_CODE_DETECTION = 0;
    private final int REQUEST_CODE_TRACK = 1;
    private PointF mTmpPiont = new PointF();
    private DigitDetector mDetector = new DigitDetector();
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

    //maps and buttons for main map
    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;
    BluetoothConnectionService mBluetoothConnection;
    Button btnStartConnection;
    Button btnSend;
    EditText etSend;
    BluetoothDevice mBTDevice;
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };
    ListView lvNewDevices;
    //calling variables
    Button callButton;
    //object identification tools
    private PermissionsManager mPermissionsManager;
    //text view for speech to text
    private TextView txvResult;
    //text sms variables
    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextToSpeech mTTS;

    //Bluetooth Adapter and button to connect to bluetooth devices
    private EditText mEditText;
    private Button mButtonSpeak;
    private MapView mapView;
    private MapboxMap map;
    private Button startButton;
    private PermissionsManager permissionsManager;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private com.mapbox.geojson.Point originPosition;
    private com.mapbox.geojson.Point destinationPosition;
    private Marker destinationMarker;
    //Broadcast Receiver for changes made to bluetooth states such as:
    //1) Discoverability mode on/off or expire
    private NavigationMapRoute navigationMapRoute;

    //Broadcast Receiver for listing devices that are not yet paired
    //excecuted by btnDiscover() method
    private Button sendSMS;
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    @SuppressWarnings("SuspuciousNameCombination")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        final String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        final List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.isEmpty())
            addCamera();

        if (isStarting && SharedPreferencesManager.getUserWiFiPreference()) {
            FragmentManager fm = getSupportFragmentManager();
            if (savedInstanceState == null) {
                wifiFragment = new WiFiFragment();
            }
            if (wifiFragment != null && !NetworkingUtility.isWifiEnabled())
                wifiFragment.show(fm, WiFiFragment.TAG_WIFI_FRAGMENT);
            isStarting = false;
        }

        setUpDrawerMenu();
        inferenceInterface_mnist = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE_MNIST);
        inferenceInterface_convnet = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE_CONVNET);

        mModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);
        mDrawView = findViewById(R.id.view_draw);
        mDrawView.setModel(mModel);
        mDrawView.setOnTouchListener(this);

        View detectButton = findViewById(R.id.button_detect);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetectClicked();
            }
        });

        View clearButton = findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClicked();
            }
        });

        mResultTextMnist = findViewById(R.id.text_result_mnist);
        mResultTextConvnet = findViewById(R.id.text_result_convnet);
        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnDetectObject = findViewById(R.id.btnDetectObject);

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                textViewResult.setText(results.toString());

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        initTensorFlowAndLoadModel();
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                MainActivity.this.setProgress(progress * 1000);
            }
        });

        webView.setWebViewClient(new UberWebViewClient());

        webView.loadUrl(buildUrl());
        boolean ret = mDetector.setup(this);
        if (!ret) {
            Log.i(TAG, "Detector setup failed");
            return;
        }
        mModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);
        mDrawView = findViewById(R.id.view_draw);
        mDrawView.setModel(mModel);
        mDrawView.setOnTouchListener(this);

        View detectButton = findViewById(R.id.button_detect);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetectClicked();
            }
        });

        View clearButton = findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClicked();
            }
        });

        mResultText = findViewById(R.id.text_result);

        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                switch (requestCode) {
                    case REQUEST_CODE_DETECTION:
                        startActivity(new Intent(MainActivity.this, ObjectDetectingActivity.class));
                        break;
                    case REQUEST_CODE_TRACK:
                        startActivity(new Intent(MainActivity.this, ObjectTrackingActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                showPermissionDialog();
            }

            @Override
            public void ignore(int requestCode) {
                authorized(requestCode);
            }
        };
        final EditText phoneNumber = findViewById(R.id.phoneNumber);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                requestPermission();
            }
        }
        final EditText smsText = findViewById(R.id.message);
        sendSMS = findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms = smsText.getText().toString();
                String phoneNum = phoneNumber.getText().toString();
                if (!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
                    if (checkPermission()) {
                        //get the default SmsManager
                        SmsManager smsManager = SmsManager.getDefault();
                        //send the sms
                        smsManager.sendTextMessage(phoneNum, null, sms, null, null);
                    } else {
                        Toast.makeText(SMSActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mButtonSpeak = findViewById(R.id.button_speak);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "Permission already granted.");
            } else {
                //if app doesn't have CALL_PHONE permission, request it
                requestPermission();
            }
        }
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mButtonSpeak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });


        Button btnONOFF = findViewById(R.id.btnONOFF);
        btnEnableDisable_Discoverable = findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        btnStartConnection = findViewById(R.id.btnStartConnection);
        btnSend = findViewById(R.id.btnSend);
        etSend = findViewById(R.id.editText);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(MainActivity.this);
        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
            }
        });
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        startButton = findViewById(R.id.startButton);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(MainActivity.this, options);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    public void onDetecting(View view) {
        mPermissionsManager.checkPermissions(REQUEST_CODE_DETECTION, PERMISSIONS);
    }

    public void onTracking(View view) {
        mPermissionsManager.checkPermissions(REQUEST_CODE_TRACK, PERMISSIONS);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SMSActivity.this, Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
        int CallPermissionResult = ContextCompat
                .checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

        return CallPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);
    }


    private void speak() {
        String text = "";
        if (mBroadcastReciever1 == 1) {
            text = null;
        } else if (mBroadcastReciever1 == 2) {
            text = "right";

        } else if (mBroadcastReciever1 == 3) {
            text = "left";
        } else {
            text = "back";
        }
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    //gets speech input
    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }


    //create method for starting connection
    //remember the connection will fail and app will crash if you havent paired first
    public void startConnection() {
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    //starting chat service method
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device, uuid);
    }

    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciever1, BTIntent);
        }
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciever1, BTIntent);
        }

    }

    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);

    }

    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        map.addOnMapClickListener(this);
        enableLocation();
    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            initiateLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initiateLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 13.0));
    }


    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (destinationMarker != null) {
            map.removeMarker(destinationMarker);
        }
        destinationMarker = map.addMarker(new MarkerOptions().position(p1));

        destinationPosition = com.mapbox.geojson.Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPosition = com.mapbox.geojson.Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
        getRoute(originPosition, destinationPosition);

        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.color.mapboxBlue);
    }
    //Method to build a route to the destination from the current location of the user
    private void getRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    //Responses for if the route is not found
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, check user and access token");

                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        //if there is already a route then replace it with the new one
                        DirectionsRoute currentRoute = response.body().routes().get(0);
                        if (navigationMapRoute != null) {
                            //remove the old route to be replaced
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                        }
                        //replace the old route with the new route
                        navigationMapRoute.addRoute(currentRoute);
                    }
                    //if the route is not create execute the method
                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });

    }

    private String buildUrl() {
        Uri.Builder uriBuilder = Uri.parse(Constants.AUTHORIZE_URL).buildUpon();
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("client_id", Constants.getUberClientId(this));
        uriBuilder.appendQueryParameter("scope", Constants.SCOPES);
        uriBuilder.appendQueryParameter("redirect_uri", Constants.getUberRedirectUrl(this));
        return uriBuilder.build().toString().replace("%20", "+");
    }

    @Override
    @SuppressWarnings("MissingPermission")
    //execute this method when the android phone is connected to WiFi or the Internet
    public void onConnected() {
        //request location updates as the user moves
        locationEngine.requestLocationUpdates();
    }

    //execute the method when the location is changed
    @Override
    public void onLocationChanged(Location location) {
        //if there is no stored location
        if (location != null) {
            //set the new location as the current location and set camera onto that position
            originLocation = location;
            setCameraPosition(location);
        }
    }

    //this method is not required
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    //if the locations permission needs to be granted execute this method
    @Override
    public void onPermissionResult(boolean granted) {
        //if permission is granted
        if (granted) {
            //enable the location
            enableLocation();
        }
    }

    //checks status of permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SMSActivity.this, "Permission accepeted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SMSActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                    Button sendSMS = findViewById(R.id.sendSMS);
                    sendSMS.setEnabled(false);
                }
                break;
        }
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //cell phone
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                callButton = findViewById(R.id.call);

                if (grantResults.length > 0) {
                    boolean CallPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (CallPermission) {
                        Toast.makeText(MainActivity.this, "Permission accepted", Toast.LENGTH_LONG).show();
                    } else {
                        //if permission is denied
                        Toast.makeText(MainActivity.this,
                                "Permission denied", Toast.LENGTH_LONG).show();
                        callButton.setEnabled(false);
                    }
                    break;
                }
        }
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    public void launchSMS(View view) {
        Intent intent = new Intent(MainActivity.this, SMSActivity.class);
        startActivity(intent);
    }
    //call using the cell service
    public void call(View view) {
        //create a variable for phone number
        final EditText phoneNumber = findViewById(R.id.phoneNumber);
        //create a new string for the phone number
        String phoneNum = phoneNumber.getText().toString();
        if (!TextUtils.isEmpty(phoneNum)) {
            //create another string to find what to dial
            String dial = "tel:" + phoneNum;
            //Make an Intent object of type intent.ACTION_CALL
            startActivity(new Intent(Intent.ACTION_CALL,
                    //Extract the telephone number from the URI
                    Uri.parse(dial)));
        } else {
            //if the phone number is not valid
            Toast.makeText(MainActivity.this, "Please enter a valid telephone number", Toast.LENGTH_SHORT).show();
        }
    }
    //execute this method when app is started
    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        //if location is not available
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        //start the map
        mapView.onStart();
    }
    //executed when map is paused and resumed
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    //executed when map is paused
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    //executed when the map is terminated
    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        //stop map
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy:called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReciever1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event);
            return true;

        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;

        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    private void processTouchDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        mDrawView.calcPos(mLastX, mLastY, mTmpPiont);
        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;
        mModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        mDrawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        mModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        mDrawView.invalidate();
    }

    private void processTouchUp() {
        mModel.endLine();
    }

    private void onDetectClicked() {
        int pixels[] = mDrawView.getPixelData();
        int digit = mDetector.detectDigit(pixels);

        Log.i(TAG, "digit =" + digit);

        mResultText.setText("Detected = " + digit);
    }

    private void onClearClicked() {
        mModel.clear();
        mDrawView.reset();
        mDrawView.invalidate();

        mResultText.setText("");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event);
            return true;

        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;

        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }
    @OnClick(R.id.addCameraButton)
    public void onAddCameraClicked() {
        final String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        final List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_CAMERA_PERMISSIONS);
        } else addCamera();
    }

    @OnClick(R.id.flash_switch_view)
    public void onFlashSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    @OnClick(R.id.front_back_camera_switcher)
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            final String name = photoName();

            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                @Override
                public void onPhotoTaken(final byte[] bytes, String filePath) {
                    //Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                    ((BaseAnncaFragment) cameraFragment).reSetZoom();
                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean b = false;
                            File f = new File("");
                            while (!b) {
                                try {
                                    Thread.sleep(700);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                f = new File(MyApplication.appFolderPath, name + ".jpg");
                                b = f.exists();
                            }
                            SavingUtility.mediaScannerCall(MainActivity.this, f);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            final String filePath = MyApplication.appFolderPath +
                                    File.separator + name + ".jpg";
                            setUpUpload(filePath);
                            if (!hasCamera2(MainActivity.this) && Camera1Manager.getCameraInstance() != null) {
                                Camera1Manager.getCameraInstance().startPreview();
                            }
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                }
            }, MyApplication.appFolderPath, name);
        }
    }

    @OnClick(R.id.pick_file)
    public void onPickFileClicked() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    final String filePath = ConvertUriToFilePath.getPathFromURI(MainActivity.this,
                            selectedImageUri);
                    setUpUpload(filePath);
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final String[] p = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        final List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : p) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.isEmpty())
            addCamera();
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() throws SecurityException{
        addCameraButton.setVisibility(View.GONE);
        cameraLayout.setVisibility(View.VISIBLE);

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            cameraFragment.setResultListener(new CameraFragmentResultListener() {
               @Override
               public void onVideoRecorded(String filePath) {
                   Intent intent = PreviewActivity.newIntentVideo(MainActivity.this, filePath);
                   startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                   Intent intent = PreviewActivity.newIntentPhoto(MainActivity.this, filePath);
                   startActivityForResult(intent, REQUEST_PREVIEW_CODE);
              }
            });

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    cameraSwitchView.displayBackCamera();
                }

                @Override
                public void onCurrentCameraFront() {
                    cameraSwitchView.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                    flashSwitchView.displayFlashAuto();
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    if (((degrees>88 && degrees <92) || (degrees>178 && degrees <182) ||
                            (degrees>268 && degrees <272) || (degrees > 358 || degrees < 2)) &&
                            degrees % 2 == 0 ) {

                        ViewCompat.setRotation(cameraSwitchView, degrees);
                        ViewCompat.setRotation(flashSwitchView, degrees);
                        ViewCompat.setRotation(recordDurationText, degrees);
                        ViewCompat.setRotation(recordSizeText, degrees);
                        ViewCompat.setRotation(pickFile, degrees);
                    }
                }


                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    cameraSwitchView.setEnabled(false);
                    recordButton.setEnabled(false);
                    //settingsView.setEnabled(false);
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                    //settingsView.setEnabled(true);
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }
            });

        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    private String photoName() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString().replaceAll(" ", "_").replaceAll(":","_");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @OnClick(R.id.menu_button)
    public void showMenu() {
        menu.openDrawer();
    }

    private void setUpUpload(String filePath) {
        Class toStart = null;
        if (SharedPreferencesManager.getUserSearchPreference() == SearchType.REVERSE_IMAGE_SEARCH)
            toStart = ReverseImageSearchResultActivity.class;
        else if (SharedPreferencesManager.getUserSearchPreference() == SearchType.OCR_SEARCH)
            toStart = OCRResultActivity.class;
        UploadingListener listener =
                new DefaultUploadingListener(filePath, MainActivity.this, toStart);
        if (NetworkingUtility.isConnectivityAvailable()) {
            listener.onStart();
            UploadingUtility.uploadToServer("file://" + filePath, MainActivity.this, listener);

        } else {
            listener.onFailure(null);
        }
    }

    private void setUpDrawerMenu() {
        menu = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.photo_size).withIcon(R.drawable.ic_photo_size)
                                .withIdentifier(1).withSelectable(false),
                        new SwitchDrawerItem().withName(R.string.wifi_reminder).withIcon(R.drawable.ic_wifi)
                                .withIdentifier(2).withSwitchEnabled(true).withSelectable(false).withSetSelected(false)
                                .withCheckable(false).withChecked(SharedPreferencesManager.getUserWiFiPreference())
                                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                                        SharedPreferencesManager.setUserWiFiPreference(isChecked);
                                    }
                                }),
                        new PrimaryDrawerItem().withName(R.string.change_camera_type).withIcon(R.drawable.ic_search)
                                .withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withSelectable(true).withName(R.string.ris)
                                        .withIdentifier(SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13)
                                        .withSetSelected(SharedPreferencesManager.getUserSearchPreference().ordinal()
                                                == SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13),
                                new SecondaryDrawerItem().withSelectable(true).withName(R.string.ocr)
                                        .withIdentifier(SearchType.OCR_SEARCH.ordinal() + 13)
                                        .withSetSelected(SharedPreferencesManager.getUserSearchPreference().ordinal()
                                                == SearchType.OCR_SEARCH.ordinal()  + 13)
                        ),
                        new PrimaryDrawerItem().withName(R.string.credits).withIcon(R.drawable.ic_credits)
                                .withIdentifier(4).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int)drawerItem.getIdentifier()){
                            case 1:
                                menu.closeDrawer();
                                final CameraFragmentApi cameraFragment = getCameraFragment();
                                if (cameraFragment != null) {
                                    cameraFragment.openSettingDialog();
                                }
                                break;
                            case 2: break; // ok
                            case 3:
                                List<IDrawerItem> subItems = ((PrimaryDrawerItem) drawerItem).getSubItems();
                                int toSelect = 0;
                                if (SharedPreferencesManager.getUserSearchPreference() == SearchType.REVERSE_IMAGE_SEARCH)
                                    toSelect = SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13;
                                else if (SharedPreferencesManager.getUserSearchPreference() == SearchType.OCR_SEARCH)
                                    toSelect = SearchType.OCR_SEARCH.ordinal() + 13;
                                for (IDrawerItem item : subItems) {
                                    item.withSetSelected(false);
                                    if (item.getIdentifier() == toSelect)
                                        item.withSetSelected(true);
                                    menu.updateItem(item);
                                }

                                break;
                            case 4:
                                Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
                                startActivity(intent);
                                menu.closeDrawer();
                                break;
                            default:
                                if ((int)drawerItem.getIdentifier() == SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13)
                                    SharedPreferencesManager.setUserSearchPreference(SearchType.REVERSE_IMAGE_SEARCH);
                                else if ((int)drawerItem.getIdentifier() == SearchType.OCR_SEARCH.ordinal() + 13)
                                    SharedPreferencesManager.setUserSearchPreference(SearchType.OCR_SEARCH);
                        }
                        return false;
                    }
                })
                .withTranslucentStatusBar(true)
                .withSelectedItem(-1)
                .withCloseOnClick(false)
                .build();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String place = result.get(0);
                    LatLng p1 = getLocationFromAddress(this, place);
                }
                break;
        }
    }

    private void processTouchDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        mDrawView.calcPos(mLastX, mLastY, mTmpPiont);
        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;
        mModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        mDrawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        mModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        mDrawView.invalidate();
    }

    private void ProcessTouchUp() {
        mModel.endLine();
    }

    private void onDetectClicked() {

        float pixels[] = mDrawView.getPixelData();
        String[] labels = new String[]{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        float[] modelOutputMnist = new float[labels.length];
        float[] modelOutputConvnet = new float[labels.length];

        // Feed in the values
        inferenceInterface_mnist.feed(INPUT_NODES[0], pixels, INPUT_DIM[0], INPUT_DIM[1]);
        inferenceInterface_convnet.feed(INPUT_NODES[0], pixels, INPUT_DIM[0], INPUT_DIM[1]);

        // Run a session over the fed in data
        inferenceInterface_mnist.run(OUTPUT_NODES);
        inferenceInterface_convnet.run(OUTPUT_NODES);

        // Fetch the result at output node
        inferenceInterface_mnist.fetch(OUTPUT_NODES[0], modelOutputMnist);
        inferenceInterface_convnet.fetch(OUTPUT_NODES[0], modelOutputConvnet);

        int indexMnist = 0;
        int indexConvnet = 0;
        for (int i = 0; i < 10; i++) {
            Log.i(TAG, "" + modelOutputMnist[i]);
            if (modelOutputMnist[i] > modelOutputMnist[indexMnist]) {
                indexMnist = i;
            }
            Log.i(TAG, "" + modelOutputConvnet[i]);
            if (modelOutputConvnet[i] > modelOutputConvnet[indexConvnet]) {
                indexConvnet = i;
            }
        }
        Log.i(TAG, "Softmax model says digit is " + indexMnist);
        Log.i(TAG, "Convnet model says digit is " + indexConvnet);
        mResultTextMnist.setText("softmax : " + labels[indexMnist]);
        mResultTextConvnet.setText("convnet : " + labels[indexConvnet]);
    }

    private void OnClearClicked() {
        mModel.clear();
        mDrawView.reset();
        mDrawView.invalidate();
        mResultTextMnist.setText("");
        mResultTextConvnet.setText("");

    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }

    private class UberWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return checkRedirect(url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (checkRedirect(failingUrl)) {
                return;
            }
            Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }

        private boolean checkRedirect(String url) {
            if (url.startsWith(SyncStateContract.Constants.getUberRedirectUrl(MainActivity.this))) {
                Uri uri = Uri.parse(url);
                UberAuthTokenClient.getUberAuthTokenClient().getAuthToken(
                        SyncStateContract.Constants.getUberClientSecret(MainActivity.this),
                        SyncStateContract.Constants.getUberClientId(MainActivity.this),
                        "authorization_code",
                        uri.getQueryParameter("code"),
                        SyncStateContract.Constants.getUberRedirectUrl(MainActivity.this),
                        new UberCallback<User>() {
                            @Override
                            public void success(User user, Response response) {
                                DemoActivity.start(MainActivity.this, user.getAccessToken(), user.getTokenType());
                                finish();
                            }
                        });
                return true;
            }
            return false;
        }
    }
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(AISettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void serviceSampleClick(final View view) {
        startActivity(AIServiceSampleActivity.class);
    }

    public void buttonSampleClick(final View view) {
        startActivity(AIButtonSampleActivity.class);
    }

    public void dialogSampleClick(final View view) {
        startActivity(AIDialogSampleActivity.class);
    }

    public void textSampleClick(final View view) {
        startActivity(AITextSampleActivity.class);
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
}
